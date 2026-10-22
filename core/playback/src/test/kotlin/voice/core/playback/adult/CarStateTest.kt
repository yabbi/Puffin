package voice.core.playback.adult

import io.kotest.matchers.shouldBe
import org.junit.Test

class CarStateTest {

  private val carState = CarState()

  @Test
  fun `starts out of the car`() {
    carState.inCar.value shouldBe false
  }

  @Test
  fun `follows the car connection`() {
    carState.onCarConnectionChanged(connected = true)
    carState.inCar.value shouldBe true

    carState.onCarConnectionChanged(connected = false)
    carState.inCar.value shouldBe false
  }
}
