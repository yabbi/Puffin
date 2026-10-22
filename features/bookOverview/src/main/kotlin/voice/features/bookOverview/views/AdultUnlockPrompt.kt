package voice.features.bookOverview.views

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

private const val AUTHENTICATORS = BIOMETRIC_WEAK or DEVICE_CREDENTIAL

@Composable
internal fun rememberAdultUnlockPrompt(onUnlocked: () -> Unit): () -> Unit {
  val activity = LocalContext.current.findFragmentActivity()
  val currentOnUnlocked by rememberUpdatedState(onUnlocked)
  return remember(activity) {
    {
      if (BiometricManager.from(activity).canAuthenticate(AUTHENTICATORS) != BiometricManager.BIOMETRIC_SUCCESS) {
        Toast.makeText(activity, "Set up a screen lock to open private books", Toast.LENGTH_LONG).show()
      } else {
        BiometricPrompt(
          activity,
          ContextCompat.getMainExecutor(activity),
          object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
              currentOnUnlocked()
            }
          },
        ).authenticate(
          BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock private books")
            .setAllowedAuthenticators(AUTHENTICATORS)
            // Continue as soon as face unlock matches instead of waiting for a confirm tap.
            .setConfirmationRequired(false)
            .build(),
        )
      }
    }
  }
}

private tailrec fun Context.findFragmentActivity(): FragmentActivity = when (this) {
  is FragmentActivity -> this
  is ContextWrapper -> baseContext.findFragmentActivity()
  else -> error("No FragmentActivity in $this")
}
