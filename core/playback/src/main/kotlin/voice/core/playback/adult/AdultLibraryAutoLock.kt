package voice.core.playback.adult

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.SystemClock
import androidx.core.content.ContextCompat
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import voice.core.initializer.AppInitializer

@ContributesIntoSet(AppScope::class)
class AdultLibraryAutoLock(private val lock: AdultLibraryLock) : AppInitializer {

  override fun onAppStart(application: Application) {
    ContextCompat.registerReceiver(
      application,
      object : BroadcastReceiver() {
        override fun onReceive(
          context: Context,
          intent: Intent,
        ) {
          lock.onScreenOff()
        }
      },
      IntentFilter(Intent.ACTION_SCREEN_OFF),
      ContextCompat.RECEIVER_NOT_EXPORTED,
    )
    application.registerActivityLifecycleCallbacks(StartedActivityCounter())
  }

  private inner class StartedActivityCounter : Application.ActivityLifecycleCallbacks {

    private var started = 0

    override fun onActivityStarted(activity: Activity) {
      if (started++ == 0) lock.onAppForegrounded(SystemClock.elapsedRealtime())
    }

    override fun onActivityStopped(activity: Activity) {
      if (--started == 0) lock.onAppBackgrounded(SystemClock.elapsedRealtime())
    }

    override fun onActivityCreated(
      activity: Activity,
      savedInstanceState: Bundle?,
    ) = Unit

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(
      activity: Activity,
      outState: Bundle,
    ) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit
  }
}
