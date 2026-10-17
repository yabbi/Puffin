package voice.core.playback.adult

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Held in memory only, so a process restart always starts locked.
 * Short trips to other apps keep it unlocked to avoid re-authenticating constantly.
 */
@SingleIn(AppScope::class)
@Inject
class AdultLibraryLock {

  private val _unlocked = MutableStateFlow(false)
  val unlocked: StateFlow<Boolean> = _unlocked.asStateFlow()

  private var backgroundedAtMs: Long? = null

  fun unlock() {
    _unlocked.value = true
  }

  fun lock() {
    _unlocked.value = false
  }

  fun onScreenOff() {
    lock()
  }

  fun onAppBackgrounded(nowMs: Long) {
    backgroundedAtMs = nowMs
  }

  fun onAppForegrounded(nowMs: Long) {
    val backgroundedAt = backgroundedAtMs ?: return
    backgroundedAtMs = null
    if (nowMs - backgroundedAt >= BACKGROUND_GRACE.inWholeMilliseconds) {
      lock()
    }
  }

  companion object {
    val BACKGROUND_GRACE: Duration = 10.minutes
  }
}
