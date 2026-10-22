package voice.core.playback.adult

import android.app.Application
import androidx.car.app.connection.CarConnection
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import voice.core.initializer.AppInitializer

@ContributesIntoSet(AppScope::class)
class CarConnectionObserver(private val carState: CarState) : AppInitializer {

  override fun onAppStart(application: Application) {
    CarConnection(application).type.observeForever { type ->
      carState.onCarConnectionChanged(type != CarConnection.CONNECTION_TYPE_NOT_CONNECTED)
    }
  }
}
