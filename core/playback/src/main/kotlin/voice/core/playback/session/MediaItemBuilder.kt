package voice.core.playback.session

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

internal enum class MediaType {
  AudioBook,
  AudioBookChapter,
  AudioBookRoot,
}

internal fun MediaItem(
  title: String,
  mediaId: MediaId,
  isPlayable: Boolean,
  browsable: Boolean,
  album: String? = null,
  artist: String? = null,
  genre: String? = null,
  sourceUri: Uri? = null,
  imageUri: Uri? = null,
  mediaType: MediaType,
  redacted: Boolean = false,
): MediaItem {
  val metadata =
    MediaMetadata.Builder()
      .setAlbumTitle(album)
      .setTitle(title)
      .setArtist(artist)
      .setGenre(genre)
      .setIsBrowsable(browsable)
      .setIsPlayable(isPlayable)
      .setArtworkUri(imageUri)
      .setMediaType(
        when (mediaType) {
          MediaType.AudioBook -> MediaMetadata.MEDIA_TYPE_AUDIO_BOOK
          MediaType.AudioBookChapter -> MediaMetadata.MEDIA_TYPE_AUDIO_BOOK_CHAPTER
          MediaType.AudioBookRoot -> MediaMetadata.MEDIA_TYPE_FOLDER_AUDIO_BOOKS
        },
      )
      .apply {
        if (redacted) setExtras(Bundle().apply { putBoolean(EXTRA_REDACTED, true) })
      }
      .build()

  return MediaItem.Builder()
    .setMediaId(Json.encodeToString(MediaId.serializer(), mediaId))
    .setMediaMetadata(metadata)
    .setUri(sourceUri)
    .build()
}

private const val EXTRA_REDACTED = "voice.redacted"

/** Redacted items must only expose their own metadata, never the tags embedded in the audio file. */
val MediaItem.isRedacted: Boolean
  get() = mediaMetadata.extras?.getBoolean(EXTRA_REDACTED) == true

fun String.toMediaIdOrNull(): MediaId? = try {
  Json.decodeFromString(MediaId.serializer(), this)
} catch (e: SerializationException) {
  null
}
