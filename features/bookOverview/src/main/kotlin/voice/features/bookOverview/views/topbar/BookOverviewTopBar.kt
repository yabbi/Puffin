package voice.features.bookOverview.views.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import voice.core.data.BookId
import voice.core.ui.VoiceTheme
import voice.features.bookOverview.overview.BookOverviewLayoutMode
import voice.features.bookOverview.overview.BookOverviewViewState
import voice.features.bookOverview.search.BookSearchViewState
import kotlin.time.Duration.Companion.seconds
import voice.core.ui.R as UiR

@Composable
internal fun BookOverviewTopBar(
  viewState: BookOverviewViewState,
  onPrivateLibraryClick: () -> Unit,
  onBookFolderClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onActiveChange: (Boolean) -> Unit,
  onQueryChange: (String) -> Unit,
  onSearchBookClick: (BookId) -> Unit,
) {
  Column {
    if (viewState.searchActive) {
      BookOverviewSearchBar(
        horizontalPadding = 0.dp,
        onQueryChange = onQueryChange,
        onActiveChange = onActiveChange,
        onBookFolderClick = onBookFolderClick,
        onSettingsClick = onSettingsClick,
        onSearchBookClick = onSearchBookClick,
        searchActive = true,
        showAddBookHint = viewState.showAddBookHint,
        showFolderPickerIcon = viewState.showFolderPickerIcon,
        searchViewState = viewState.searchViewState,
      )
    } else {
      MyLibraryHeader(
        title = if (viewState.privateLibrary == BookOverviewViewState.PrivateLibrary.Open) "Private Library" else "My Library",
        privateLibrary = viewState.privateLibrary,
        onPrivateLibraryClick = onPrivateLibraryClick,
        onLibraryClick = onBookFolderClick,
        onSearchClick = { onActiveChange(true) },
        onSettingsClick = onSettingsClick,
      )
    }
    // Scanning progress is shown at the bottom of the screen (see ScanningBar), not here.
  }
}

@Composable
private fun MyLibraryHeader(
  title: String,
  privateLibrary: BookOverviewViewState.PrivateLibrary?,
  onPrivateLibraryClick: () -> Unit,
  onLibraryClick: () -> Unit,
  onSearchClick: () -> Unit,
  onSettingsClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .padding(start = 20.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = title,
      fontSize = 24.sp,
      fontWeight = FontWeight.Medium,
      letterSpacing = (-0.12).sp,
    )
    Spacer(Modifier.weight(1F))
    if (privateLibrary != null) {
      IconButton(onClick = onPrivateLibraryClick) {
        Icon(
          painter = painterResource(
            if (privateLibrary == BookOverviewViewState.PrivateLibrary.Open) UiR.drawable.ic_incognito_fill else UiR.drawable.ic_incognito,
          ),
          contentDescription = null,
        )
      }
    }
    IconButton(onClick = onSearchClick) {
      Icon(painter = painterResource(UiR.drawable.ic_mage_search), contentDescription = null)
    }
    IconButton(onClick = onLibraryClick) {
      Icon(painter = painterResource(UiR.drawable.ic_mage_file_records), contentDescription = null)
    }
    IconButton(onClick = onSettingsClick) {
      Icon(painter = painterResource(UiR.drawable.ic_mage_settings), contentDescription = null)
    }
  }
}

@Composable
@Preview
private fun BookOverviewTopBarPreview() {
  VoiceTheme {
    BookOverviewTopBar(
      viewState = BookOverviewViewState(
        books = emptyMap(),
        layoutMode = BookOverviewLayoutMode.List,
        gridColumns = 2,
        currentBookId = null,
        isPlaying = false,
        playButtonState = BookOverviewViewState.PlayButtonState.Paused,
        showAddBookHint = true,
        showSearchIcon = true,
        isLoading = true,
        searchActive = false,
        searchViewState = BookSearchViewState.EmptySearch(
          suggestedAuthors = listOf(),
          recentQueries = listOf(),
          query = "",
        ),
        showStoragePermissionBugCard = false,
        showFolderPickerIcon = true,
        miniPlayer = null,
        selectedCategoryIndex = null,
      ),
      onPrivateLibraryClick = {},
      onBookFolderClick = {},
      onSettingsClick = {},
      onActiveChange = {},
      onQueryChange = {},
      onSearchBookClick = {},
    )
  }
}
