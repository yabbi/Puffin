package voice.features.bookOverview.overview

import androidx.annotation.StringRes
import voice.core.data.Book
import voice.core.data.BookComparator
import voice.core.data.BookProgressCategory
import voice.core.data.progressCategory
import voice.core.strings.R as StringsR

enum class BookOverviewCategory(
  @StringRes val nameRes: Int,
  val comparator: Comparator<Book>,
) {
  CURRENT(
    nameRes = StringsR.string.book_header_current,
    comparator = BookComparator.ByLastPlayed,
  ),
  NOT_STARTED(
    nameRes = StringsR.string.book_header_not_started,
    comparator = BookComparator.ByName,
  ),
  FINISHED(
    nameRes = StringsR.string.book_header_completed,
    comparator = BookComparator.ByLastPlayed,
  ),
}

val Book.category: BookOverviewCategory
  get() {
    return when (progressCategory) {
      BookProgressCategory.NOT_STARTED -> BookOverviewCategory.NOT_STARTED
      BookProgressCategory.CURRENT -> BookOverviewCategory.CURRENT
      BookProgressCategory.FINISHED -> BookOverviewCategory.FINISHED
    }
  }
