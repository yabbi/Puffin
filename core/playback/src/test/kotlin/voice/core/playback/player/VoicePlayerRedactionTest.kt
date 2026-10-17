package voice.core.playback.player

import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.extractor.metadata.id3.TextInformationFrame
import androidx.media3.test.utils.FakeMediaSource
import androidx.media3.test.utils.FakeTimeline
import androidx.media3.test.utils.TestExoPlayerBuilder
import androidx.media3.test.utils.robolectric.TestPlayerRunHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.TestScope
import org.junit.Test
import org.junit.runner.RunWith
import voice.core.data.BookId
import voice.core.data.ChapterId
import voice.core.playback.session.MediaId
import voice.core.playback.session.MediaItem
import voice.core.playback.session.MediaType

@RunWith(AndroidJUnit4::class)
class VoicePlayerRedactionTest {

  private val embeddedTags = Format.Builder()
    .setSampleMimeType(MimeTypes.AUDIO_MPEG)
    .setMetadata(
      Metadata(
        TextInformationFrame("TPE1", null, listOf("Embedded Author")),
        TextInformationFrame("TALB", null, listOf("Embedded Album")),
      ),
    )
    .build()

  private val internalPlayer = TestExoPlayerBuilder(ApplicationProvider.getApplicationContext())
    .setMediaSourceFactory(
      mockk {
        every { createMediaSource(any()) } answers {
          FakeMediaSource(
            FakeTimeline(
              FakeTimeline.TimelineWindowDefinition.Builder()
                .setMediaItem(arg(0))
                .build(),
            ),
            embeddedTags,
          )
        }
      },
    )
    .build()

  private val player = VoicePlayer(
    player = internalPlayer,
    repo = mockk(relaxed = true),
    contentRepo = mockk(relaxed = true),
    historyRecorder = mockk(relaxed = true),
    currentBookStoreId = mockk(relaxed = true),
    seekTimeStore = mockk(relaxed = true),
    autoRewindAmountStore = mockk(relaxed = true),
    scope = TestScope(),
    chapterRepo = mockk(relaxed = true),
    mediaItemProvider = mockk(relaxed = true),
    volumeGain = mockk(relaxed = true),
    sleepTimer = mockk(relaxed = true),
    analytics = mockk(relaxed = true),
  )

  @Test
  fun `regular books expose tags embedded in the file`() {
    play(chapterItem(redacted = false))

    player.mediaMetadata.artist shouldBe "Embedded Author"
  }

  @Test
  fun `redacted books hide tags embedded in the file`() {
    play(chapterItem(redacted = true))

    player.mediaMetadata.title shouldBe "Chapter 1"
    player.mediaMetadata.artist shouldBe "Audiobook"
    player.mediaMetadata.albumTitle shouldBe null
  }

  @Test
  fun `listeners receive redacted metadata`() {
    val received = mutableListOf<MediaMetadata>()
    player.addListener(
      object : Player.Listener {
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
          received += mediaMetadata
        }
      },
    )

    play(chapterItem(redacted = true))

    received.map { it.albumTitle }.toSet() shouldBe setOf(null)
    received.last().artist shouldBe "Audiobook"
  }

  @Test
  fun `listeners still receive playback events`() {
    val states = mutableListOf<Int>()
    player.addListener(
      object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
          states += playbackState
        }
      },
    )

    play(chapterItem(redacted = false))

    states.last() shouldBe Player.STATE_READY
  }

  private fun play(item: MediaItem) {
    internalPlayer.setMediaItem(item)
    internalPlayer.prepare()
    TestPlayerRunHelper.runUntilPlaybackState(internalPlayer, Player.STATE_READY)
  }

  private fun chapterItem(redacted: Boolean): MediaItem = MediaItem(
    title = if (redacted) "Chapter 1" else "Real Title",
    artist = if (redacted) "Audiobook" else null,
    mediaId = MediaId.Chapter(bookId = BookId("book"), chapterId = ChapterId("file:///chapter.mp3")),
    browsable = false,
    isPlayable = true,
    mediaType = MediaType.AudioBookChapter,
    redacted = redacted,
  )
}
