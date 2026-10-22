package voice.features.bookOverview.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import voice.features.bookOverview.overview.BookOverviewCategory
import voice.core.ui.R as UiR

internal val bookFilters = listOf(
  Triple(BookOverviewCategory.CURRENT, "In Progress", UiR.drawable.ic_mage_chart_15),
  Triple(BookOverviewCategory.NOT_STARTED, "Not started", UiR.drawable.ic_mage_inbox),
  Triple(BookOverviewCategory.FINISHED, "Completed", UiR.drawable.ic_mage_inbox_check),
)

@Composable
internal fun FilterChips(
  selectedIndex: Int,
  onSelect: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    bookFilters.forEachIndexed { index, (_, label, icon) ->
      FilterChip(
        modifier = Modifier.weight(1f),
        label = label,
        iconRes = icon,
        selected = index == selectedIndex,
        onClick = { onSelect(index) },
      )
    }
  }
}

@Composable
private fun FilterChip(
  label: String,
  iconRes: Int,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = voice.core.ui.RavenTheme.colors
  Surface(
    modifier = modifier.clickable(onClick = onClick),
    shape = RoundedCornerShape(88.dp),
    color = if (selected) colors.active else colors.bgSecondary,
    contentColor = if (selected) colors.inverse else colors.subTitle,
  ) {
    Row(
      modifier = Modifier.padding(10.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = Modifier.size(14.dp),
      )
      Spacer(Modifier.width(4.dp))
      Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.06).sp,
      )
    }
  }
}
