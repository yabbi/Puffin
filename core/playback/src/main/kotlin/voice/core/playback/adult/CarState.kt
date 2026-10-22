package voice.core.playback.adult

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import voice.core.logging.api.Logger

/**
 * Follows the system-wide car connection rather than Android Auto connecting to the media
 * session, as the Android Auto app also connects while the phone isn't in a car.
 */
@SingleIn(AppScope::class)
@Inject
class CarState {

  private val _inCar = MutableStateFlow(false)
  val inCar: StateFlow<Boolean> = _inCar.asStateFlow()

  fun onCarConnectionChanged(connected: Boolean) {
    if (_inCar.value != connected) Logger.i("Car connection changed, inCar=$connected")
    _inCar.value = connected
  }
}
