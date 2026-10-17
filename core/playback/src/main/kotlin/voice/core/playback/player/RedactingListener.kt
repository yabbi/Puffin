package voice.core.playback.player

import androidx.media3.common.AudioAttributes
import androidx.media3.common.DeviceInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.Cue
import androidx.media3.common.text.CueGroup

/**
 * Forwards every callback explicitly: Kotlin's `by` delegation skips Java default methods,
 * which would silently drop all player events.
 */
internal class RedactingListener(
  private val delegate: Player.Listener,
  private val redact: (MediaMetadata) -> MediaMetadata,
) : Player.Listener {

  override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
    delegate.onMediaMetadataChanged(redact(mediaMetadata))
  }

  override fun onEvents(
    player: Player,
    events: Player.Events,
  ) = delegate.onEvents(player, events)

  override fun onTimelineChanged(
    timeline: Timeline,
    reason: Int,
  ) = delegate.onTimelineChanged(timeline, reason)

  override fun onMediaItemTransition(
    mediaItem: MediaItem?,
    reason: Int,
  ) = delegate.onMediaItemTransition(mediaItem, reason)

  override fun onTracksChanged(tracks: Tracks) = delegate.onTracksChanged(tracks)

  override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) = delegate.onPlaylistMetadataChanged(mediaMetadata)

  override fun onIsLoadingChanged(isLoading: Boolean) = delegate.onIsLoadingChanged(isLoading)

  @Deprecated("Deprecated in Java")
  @Suppress("DEPRECATION")
  override fun onLoadingChanged(isLoading: Boolean) = delegate.onLoadingChanged(isLoading)

  override fun onAvailableCommandsChanged(availableCommands: Player.Commands) = delegate.onAvailableCommandsChanged(availableCommands)

  override fun onTrackSelectionParametersChanged(parameters: TrackSelectionParameters) =
    delegate.onTrackSelectionParametersChanged(parameters)

  @Deprecated("Deprecated in Java")
  @Suppress("DEPRECATION")
  override fun onPlayerStateChanged(
    playWhenReady: Boolean,
    playbackState: Int,
  ) = delegate.onPlayerStateChanged(playWhenReady, playbackState)

  override fun onPlaybackStateChanged(playbackState: Int) = delegate.onPlaybackStateChanged(playbackState)

  override fun onPlayWhenReadyChanged(
    playWhenReady: Boolean,
    reason: Int,
  ) = delegate.onPlayWhenReadyChanged(playWhenReady, reason)

  override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) =
    delegate.onPlaybackSuppressionReasonChanged(playbackSuppressionReason)

  override fun onIsPlayingChanged(isPlaying: Boolean) = delegate.onIsPlayingChanged(isPlaying)

  override fun onRepeatModeChanged(repeatMode: Int) = delegate.onRepeatModeChanged(repeatMode)

  override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) = delegate.onShuffleModeEnabledChanged(shuffleModeEnabled)

  override fun onPlayerError(error: PlaybackException) = delegate.onPlayerError(error)

  override fun onPlayerErrorChanged(error: PlaybackException?) = delegate.onPlayerErrorChanged(error)

  @Deprecated("Deprecated in Java")
  @Suppress("DEPRECATION")
  override fun onPositionDiscontinuity(reason: Int) = delegate.onPositionDiscontinuity(reason)

  override fun onPositionDiscontinuity(
    oldPosition: Player.PositionInfo,
    newPosition: Player.PositionInfo,
    reason: Int,
  ) = delegate.onPositionDiscontinuity(oldPosition, newPosition, reason)

  override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) =
    delegate.onPlaybackParametersChanged(playbackParameters)

  override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) = delegate.onSeekBackIncrementChanged(seekBackIncrementMs)

  override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) = delegate.onSeekForwardIncrementChanged(seekForwardIncrementMs)

  override fun onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs: Long) =
    delegate.onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs)

  override fun onAudioSessionIdChanged(audioSessionId: Int) = delegate.onAudioSessionIdChanged(audioSessionId)

  override fun onAudioAttributesChanged(audioAttributes: AudioAttributes) = delegate.onAudioAttributesChanged(audioAttributes)

  override fun onVolumeChanged(volume: Float) = delegate.onVolumeChanged(volume)

  override fun onSkipSilenceEnabledChanged(skipSilenceEnabled: Boolean) = delegate.onSkipSilenceEnabledChanged(skipSilenceEnabled)

  override fun onDeviceInfoChanged(deviceInfo: DeviceInfo) = delegate.onDeviceInfoChanged(deviceInfo)

  override fun onDeviceVolumeChanged(
    volume: Int,
    muted: Boolean,
  ) = delegate.onDeviceVolumeChanged(volume, muted)

  override fun onVideoSizeChanged(videoSize: VideoSize) = delegate.onVideoSizeChanged(videoSize)

  override fun onSurfaceSizeChanged(
    width: Int,
    height: Int,
  ) = delegate.onSurfaceSizeChanged(width, height)

  override fun onRenderedFirstFrame() = delegate.onRenderedFirstFrame()

  @Deprecated("Deprecated in Java")
  @Suppress("DEPRECATION")
  override fun onCues(cues: List<Cue>) = delegate.onCues(cues)

  override fun onCues(cueGroup: CueGroup) = delegate.onCues(cueGroup)

  override fun onMetadata(metadata: Metadata) = delegate.onMetadata(metadata)
}
