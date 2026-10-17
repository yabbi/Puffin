package voice.features.bookOverview.overview

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import voice.core.data.BookId
import voice.features.bookOverview.search.BookSearchViewState

@Immutable
data class BookOverviewViewState(
  val books: Map<BookOverviewCategory, Map<BookId, State<BookOverviewItemViewState>>>,
  val layoutMode: BookOverviewLayoutMode,
  val gridColumns: Int,
  val currentBookId: BookId?,
  val isPlaying: Boolean,
  val playButtonState: PlayButtonState?,
  val showAddBookHint: Boolean,
  val showSearchIcon: Boolean,
  val isLoading: Boolean,
  val searchActive: Boolean,
  val searchViewState: BookSearchViewState,
  val showStoragePermissionBugCard: Boolean,
  val showFolderPickerIcon: Boolean,
  val miniPlayer: MiniPlayerViewState?,
  // Persisted last-used category index; null while still loading, -1 when never chosen.
  val selectedCategoryIndex: Int?,
  // Null when no book is marked as adult, so the section isn't even hinted at.
  val adultSection: AdultSection? = null,
) {

  enum class AdultSection {
    Locked,
    Unlocked,
  }

  companion object {
    val Loading = BookOverviewViewState(
      books = mapOf(),
      layoutMode = BookOverviewLayoutMode.List,
      gridColumns = 2,
      currentBookId = null,
      isPlaying = false,
      playButtonState = null,
      showAddBookHint = false,
      showSearchIcon = false,
      isLoading = true,
      searchActive = false,
      searchViewState = BookSearchViewState.EmptySearch(
        suggestedAuthors = emptyList(),
        recentQueries = emptyList(),
        query = "",
      ),
      showStoragePermissionBugCard = false,
      showFolderPickerIcon = true,
      miniPlayer = null,
      selectedCategoryIndex = null,
    )
  }

  enum class PlayButtonState {
    Playing,
    Paused,
  }
}
