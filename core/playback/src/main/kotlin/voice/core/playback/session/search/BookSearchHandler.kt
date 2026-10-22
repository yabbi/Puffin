package voice.core.playback.session.search

import android.provider.MediaStore
import androidx.datastore.core.DataStore
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first
import voice.core.data.Book
import voice.core.data.BookId
import voice.core.data.repo.BookRepository
import voice.core.data.store.CurrentBookStore
import voice.core.logging.api.Logger

@Inject
class BookSearchHandler(
  private val repo: BookRepository,
  @CurrentBookStore
  private val currentBookStore: DataStore<BookId?>,
) {

  suspend fun handle(
    search: VoiceSearch,
    excludeAdult: Boolean = false,
  ): Book? {
    Logger.i("handle $search")
    val books = repo.all().filterNot { excludeAdult && it.content.isAdult }
    return when (search.mediaFocus) {
      MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> searchByArtist(books, search)
      MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE,
      MediaStore.Audio.Media.ENTRY_CONTENT_TYPE,
      -> searchAlbum(books, search)
      else -> null
    } ?: searchUnstructured(books, search.query)
  }

  private fun searchAlbum(
    books: List<Book>,
    search: VoiceSearch,
  ): Book? {
    if (search.album != null) {
      val foundMatch = books.find {
        val nameMatches = it.content.name.contains(search.album, ignoreCase = true)
        val artistMatches =
          search.artist == null || it.content.author?.contains(search.artist, ignoreCase = true) == true
        nameMatches && artistMatches
      }
      if (foundMatch != null) return foundMatch
    }

    return null
  }

  // Look for anything that might match the query
  private suspend fun searchUnstructured(
    books: List<Book>,
    query: String?,
  ): Book? {
    if (!query.isNullOrBlank()) {
      val foundMatch = books.find {
        val bookNameMatches = it.content.name.contains(query, ignoreCase = true)
        val authorMatches = it.content.author?.contains(query, ignoreCase = true) == true
        val chapterNameMatches = it.chapters.any { chapter ->
          val chapterName = chapter.name
          chapterName != null && chapterName.contains(query, ignoreCase = true)
        }
        bookNameMatches || authorMatches || chapterNameMatches
      }
      if (foundMatch != null) return foundMatch
    }

    Logger.i("continuing from search without query")
    val currentId = currentBookStore.data.first()
    return books.find { it.content.id == currentId }
  }

  private fun searchByArtist(
    books: List<Book>,
    search: VoiceSearch,
  ): Book? {
    Logger.i("searchByArtist")
    if (search.artist != null) {
      val foundMatch = books.find {
        it.content.author?.contains(search.artist, ignoreCase = true) == true
      }
      if (foundMatch != null) {
        return foundMatch
      }
    }
    return null
  }
}
