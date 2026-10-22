package voice.core.playback.player

import android.net.Uri
import androidx.media3.extractor.Extractor
import androidx.media3.extractor.ExtractorInput
import androidx.media3.extractor.ExtractorsFactory
import androidx.media3.extractor.MpegAudioUtil
import androidx.media3.extractor.PositionHolder
import androidx.media3.extractor.mp3.Mp3Extractor

/**
 * Some MP3 encoders write a Xing table of contents that is not monotonic. Media3 trusts it blindly,
 * so seeks land hours away from the requested position. Removing the table makes Media3 fall back
 * to index seeking, which is exact.
 */
class XingTocSanitizingExtractorsFactory(private val delegate: ExtractorsFactory) : ExtractorsFactory {

  override fun createExtractors(): Array<Extractor> = delegate.createExtractors().wrapMp3Extractors()

  override fun createExtractors(
    uri: Uri,
    responseHeaders: Map<String, List<String>>,
  ): Array<Extractor> = delegate.createExtractors(uri, responseHeaders).wrapMp3Extractors()

  private fun Array<Extractor>.wrapMp3Extractors(): Array<Extractor> {
    return map { if (it is Mp3Extractor) XingTocSanitizingExtractor(it) else it }.toTypedArray()
  }
}

private class XingTocSanitizingExtractor(private val delegate: Extractor) : Extractor by delegate {

  private var lastInput: ExtractorInput? = null
  private var sanitizingInput: ExtractorInput? = null

  override fun read(
    input: ExtractorInput,
    seekPosition: PositionHolder,
  ): Int = delegate.read(sanitizing(input), seekPosition)

  override fun getUnderlyingImplementation(): Extractor = delegate.underlyingImplementation

  private fun sanitizing(input: ExtractorInput): ExtractorInput {
    if (input !== lastInput) {
      lastInput = input
      sanitizingInput = XingTocSanitizingExtractorInput(input)
    }
    return sanitizingInput!!
  }
}

private class XingTocSanitizingExtractorInput(private val delegate: ExtractorInput) : ExtractorInput by delegate {

  override fun peekFully(
    target: ByteArray,
    offset: Int,
    length: Int,
  ) {
    delegate.peekFully(target, offset, length)
    removeNonMonotonicXingToc(target, offset, length)
  }

  override fun peekFully(
    target: ByteArray,
    offset: Int,
    length: Int,
    allowEndOfInput: Boolean,
  ): Boolean {
    val peeked = delegate.peekFully(target, offset, length, allowEndOfInput)
    if (peeked) removeNonMonotonicXingToc(target, offset, length)
    return peeked
  }
}

private const val XING_TAG = 0x58696e67
private const val FLAG_FRAME_COUNT = 0x01
private const val FLAG_DATA_SIZE = 0x02
private const val FLAG_TOC = 0x04
private const val TOC_SIZE = 100

internal fun removeNonMonotonicXingToc(
  frame: ByteArray,
  offset: Int,
  length: Int,
) {
  val end = offset + length
  if (length < 4) return
  val header = MpegAudioUtil.Header()
  if (!header.setForHeaderData(frame.readInt(offset))) return

  val tagPosition = offset + xingTagOffset(header)
  val flagsPosition = tagPosition + 4
  if (flagsPosition + 4 > end || frame.readInt(tagPosition) != XING_TAG) return
  val flags = frame.readInt(flagsPosition)
  if (flags and FLAG_TOC == 0) return

  var tocStart = flagsPosition + 4
  if (flags and FLAG_FRAME_COUNT != 0) tocStart += 4
  if (flags and FLAG_DATA_SIZE != 0) tocStart += 4
  val tocEnd = tocStart + TOC_SIZE
  if (tocEnd > end) return

  val tocIsMonotonic = (tocStart + 1 until tocEnd).all { frame.unsigned(it) >= frame.unsigned(it - 1) }
  if (tocIsMonotonic) return

  frame[flagsPosition + 3] = (flags and FLAG_TOC.inv()).toByte()
  frame.copyInto(frame, destinationOffset = tocStart, startIndex = tocEnd, endIndex = end)
  frame.fill(0, fromIndex = end - TOC_SIZE, toIndex = end)
}

// Mirrors Mp3Extractor: the tag follows the side information, whose size depends on version and channels.
private fun xingTagOffset(header: MpegAudioUtil.Header): Int {
  val isMpeg1 = header.version and 1 != 0
  val isMono = header.channels == 1
  return when {
    isMpeg1 -> if (isMono) 21 else 36
    else -> if (isMono) 13 else 21
  }
}

private fun ByteArray.unsigned(index: Int): Int = this[index].toInt() and 0xFF

private fun ByteArray.readInt(index: Int): Int {
  return (unsigned(index) shl 24) or (unsigned(index + 1) shl 16) or (unsigned(index + 2) shl 8) or unsigned(index + 3)
}
