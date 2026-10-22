package voice.core.playback.adult

import androidx.datastore.core.DataStore
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import voice.core.data.AdultCarSwap
import voice.core.data.BookId
import voice.core.data.repo.BookRepository
import voice.core.data.store.AdultCarSwapStore
import voice.core.data.store.CurrentBookStore
import voice.core.logging.api.Logger
import voice.core.playback.di.PlaybackScope
import voice.core.playback.player.VoicePlayer
import voice.core.playback.playstate.PositionUpdater
import voice.core.playback.session.MediaItemProvider

/**
 * Keeps an adult book from autostarting in the car: a paused adult current book is swapped for the
 * most recent regular one while connected, and comes back once the car is gone. Books the user
 * picks or is already listening to are left alone.
 */
@SingleIn(PlaybackScope::class)
@Inject
class AdultCarGuard(
  private val carState: CarState,
  private val player: VoicePlayer,
  private val repo: BookRepository,
  @CurrentBookStore
  private val currentBookStore: DataStore<BookId?>,
  @AdultCarSwapStore
  private val swapStore: DataStore<AdultCarSwap?>,
  private val mediaItemProvider: MediaItemProvider,
  private val positionUpdater: PositionUpdater,
  private val scope: CoroutineScope,
) {

  fun attach() {
    scope.launch {
      carState.inCar.collect { inCar ->
        if (inCar) onEnterCar() else onLeaveCar()
      }
    }
  }

  internal suspend fun onEnterCar() {
    val currentId = currentBookStore.data.first() ?: return
    val current = repo.get(currentId) ?: return
    if (!current.content.isAdult || player.isPlaying) return

    Logger.i("Car connected, swapping out the adult book")
    positionUpdater.flushPositionNow()

    val fallback = repo.all()
      .filter { it.content.isActive && !it.content.isAdult }
      .maxByOrNull { it.content.lastPlayedAt }
    swapStore.updateData { AdultCarSwap(adultBookId = currentId, fallbackBookId = fallback?.id) }
    currentBookStore.updateData { fallback?.id }
    if (fallback != null) {
      player.setMediaItem(mediaItemProvider.mediaItem(fallback))
      player.prepare()
    } else {
      player.stop()
      player.clearMediaItems()
    }
  }

  internal suspend fun onLeaveCar() {
    val swap = swapStore.data.first() ?: return
    swapStore.updateData { null }
    // Keep whatever was picked or is still playing in the car.
    if (player.isPlaying || currentBookStore.data.first() != swap.fallbackBookId) return
    val adultBook = repo.get(swap.adultBookId) ?: return

    Logger.i("Car disconnected, restoring the adult book")
    currentBookStore.updateData { adultBook.id }
    player.setMediaItem(mediaItemProvider.mediaItem(adultBook))
    player.prepare()
  }
}
