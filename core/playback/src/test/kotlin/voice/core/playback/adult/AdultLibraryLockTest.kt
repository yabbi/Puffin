package voice.core.playback.adult

import io.kotest.matchers.shouldBe
import org.junit.Test
import kotlin.time.Duration.Companion.minutes

class AdultLibraryLockTest {

  private val lock = AdultLibraryLock()

  @Test
  fun `starts locked`() {
    lock.unlocked.value shouldBe false
  }

  @Test
  fun `screen off locks`() {
    lock.unlock()

    lock.onScreenOff()

    lock.unlocked.value shouldBe false
  }

  @Test
  fun `short trips to other apps stay unlocked`() {
    lock.unlock()

    lock.onAppBackgrounded(nowMs = 0)
    lock.onAppForegrounded(nowMs = 9.minutes.inWholeMilliseconds)

    lock.unlocked.value shouldBe true
  }

  @Test
  fun `long stays in the background lock`() {
    lock.unlock()

    lock.onAppBackgrounded(nowMs = 0)
    lock.onAppForegrounded(nowMs = 10.minutes.inWholeMilliseconds)

    lock.unlocked.value shouldBe false
  }

  @Test
  fun `foregrounding without a prior background keeps the state`() {
    lock.unlock()

    lock.onAppForegrounded(nowMs = 60.minutes.inWholeMilliseconds)

    lock.unlocked.value shouldBe true
  }
}
