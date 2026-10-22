package voice.core.playback.player

import android.net.Uri
import androidx.media3.datasource.FileDataSource
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.Extractor
import androidx.media3.extractor.mp3.Mp3Extractor
import androidx.media3.test.utils.FakeExtractorOutput
import androidx.media3.test.utils.TestUtil
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class XingTocSanitizingExtractorsFactoryTest {

  private val seekTimesUs = listOf(3_000_000L, 12_500_000L, 21_000_000L, 28_000_000L)

  @Test
  fun plainMp3ExtractorMisplacesSeeksInFileWithNonMonotonicToc() {
    val file = testFile("vbr_non_monotonic_xing_toc.mp3")

    val misplacedSeeks = seekTimesUs.count { !seekLandsOnMatchingAudio(Mp3Extractor(), file, it) }

    misplacedSeeks shouldNotBe 0
  }

  @Test
  fun seeksLandOnMatchingAudioInFileWithNonMonotonicToc() {
    val file = testFile("vbr_non_monotonic_xing_toc.mp3")

    seekTimesUs.forEach { seekTimeUs ->
      seekLandsOnMatchingAudio(sanitizingMp3Extractor(), file, seekTimeUs) shouldBe true
    }
  }

  @Test
  fun validTocIsLeftUntouched() {
    val bytes = testFile("vbr_valid_xing_toc.mp3").readBytes()
    val firstFrame = bytes.copyOf(1024)

    removeNonMonotonicXingToc(firstFrame, offset = 0, length = firstFrame.size)

    firstFrame shouldBe bytes.copyOf(1024)
  }

  private fun seekLandsOnMatchingAudio(
    extractor: Extractor,
    file: File,
    seekTimeUs: Long,
  ): Boolean {
    val reference = TestUtil.extractAllSamplesFromFilePath(Mp3Extractor(), file.path).trackOutputs[0]
    val uri = Uri.fromFile(file)
    val dataSource = FileDataSource()
    val output = FakeExtractorOutput()
    val seekMap = TestUtil.extractSeekMap(extractor, output, dataSource, uri)
    val trackOutput = output.trackOutputs[0]

    val sampleIndex = TestUtil.seekToTimeUs(extractor, seekMap, seekTimeUs, dataSource, trackOutput, uri)

    val landedTimeUs = trackOutput.getSampleTimeUs(sampleIndex)
    val landedJustBeforeTarget = landedTimeUs in seekTimeUs - MAX_SEEK_LEAD_US..seekTimeUs
    val referenceIndex = reference.sampleTimesUs.indexOf(landedTimeUs)
    val audioMatchesTimestamp = referenceIndex != -1 &&
      reference.getSampleData(referenceIndex).contentEquals(trackOutput.getSampleData(sampleIndex))
    return landedJustBeforeTarget && audioMatchesTimestamp
  }

  private fun sanitizingMp3Extractor(): Extractor {
    val factory = XingTocSanitizingExtractorsFactory(
      DefaultExtractorsFactory()
        .setConstantBitrateSeekingEnabled(true)
        .setMp3ExtractorFlags(Mp3Extractor.FLAG_ENABLE_INDEX_SEEKING),
    )
    return factory.createExtractors().single { it.underlyingImplementation is Mp3Extractor }
  }

  private fun testFile(name: String): File = File(javaClass.classLoader!!.getResource("mp3/$name")!!.file)

  private companion object {
    // Seeks resume at the closest earlier frame; the player discards the frames up to the target.
    const val MAX_SEEK_LEAD_US = 200_000L
  }
}
