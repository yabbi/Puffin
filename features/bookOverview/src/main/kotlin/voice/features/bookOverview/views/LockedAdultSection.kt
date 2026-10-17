package voice.features.bookOverview.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import voice.core.ui.RavenTheme
import voice.core.ui.R as UiR

@Composable
internal fun LockedAdultSection(
  onUnlock: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 32.dp),
    contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
        painter = painterResource(UiR.drawable.ic_mage_lock),
        contentDescription = null,
        modifier = Modifier.size(40.dp),
        tint = RavenTheme.colors.caption,
      )
      Spacer(Modifier.height(12.dp))
      Text(
        text = "Private books are locked.",
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        letterSpacing = (-0.07).sp,
        color = RavenTheme.colors.caption,
      )
      Spacer(Modifier.height(16.dp))
      Surface(
        modifier = Modifier.clickable(onClick = onUnlock),
        shape = RoundedCornerShape(12.dp),
        color = RavenTheme.colors.primary,
      ) {
        Text(
          text = "Unlock",
          modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
          color = Color.White,
          fontWeight = FontWeight.Medium,
          fontSize = 14.sp,
          letterSpacing = (-0.07).sp,
        )
      }
    }
  }
}
