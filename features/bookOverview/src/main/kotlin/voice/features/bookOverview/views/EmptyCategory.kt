package voice.features.bookOverview.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import voice.core.ui.RavenTheme
import voice.features.bookOverview.overview.BookOverviewCategory
import voice.core.ui.R as UiR

@Composable
internal fun EmptyCategory(
  category: BookOverviewCategory,
  isLoading: Boolean,
  modifier: Modifier = Modifier,
) {
  // While scanning, stay blank so the ScanningBar is the only progress signal.
  if (isLoading) return

  val (iconRes, message) = when (category) {
    BookOverviewCategory.NOT_STARTED -> UiR.drawable.ic_mage_inbox to "No new audiobooks here.\nAdded books you haven't started appear here."
    BookOverviewCategory.CURRENT -> UiR.drawable.ic_mage_chart_15 to "Nothing in progress.\nStart a book and it shows up here."
    BookOverviewCategory.FINISHED -> UiR.drawable.ic_mage_inbox_check to "No finished books yet.\nBooks you complete are collected here."
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 32.dp),
    contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = Modifier.size(40.dp),
        tint = RavenTheme.colors.caption,
      )
      Spacer(Modifier.height(12.dp))
      Text(
        text = message,
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        letterSpacing = (-0.07).sp,
        color = RavenTheme.colors.caption,
      )
    }
  }
}
