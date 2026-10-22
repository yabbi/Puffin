package voice.core.playback.adult

import androidx.media3.common.MediaItem
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import voice.core.data.AdultCarSwap
import voice.core.data.Book
import voice.core.data.BookId
import voice.core.data.Chapter
import voice.core.data.ChapterId
import voice.core.data.repo.BookRepository
import voice.core.playback.MemoryDataStore
import voice.core.playback.player.VoicePlayer
import voice.core.playback.session.MediaItemProvider
import voice.core.playback.session.search.book
import java.time.Instant

class AdultCarGuardTest {

  private val olderRegular = testBook("older", lastPlayedAt = 1)
  private val newerRegular = testBook("newer", lastPlayedAt = 2)
  private val adult = testBook("adult", lastPlayedAt = 3, isAdult = true)

  private var books = listOf(olderRegular, newerRegular, adult)
  private val currentBookStore = MemoryDataStore<BookId?>(null)
  private val swapStore = MemoryDataStore<AdultCarSwap?>(null)
  private val player = mockk<VoicePlayer>(relaxed = true)

  private val guard = AdultCarGuard(
    carState = CarState(),
    player = player,
    repo = mockk<BookRepository> {
      coEvery { all() } answers { books }
      coEvery { this@mockk.get(any()) } answers { books.find { it.id == firstArg() } }
    },
    currentBookStore = currentBookStore,
    swapStore = swapStore,
    mediaItemProvider = mockk<MediaItemProvider> {
      every { mediaItem(any<Book>()) } answers { MediaItem.Builder().setMediaId(firstArg<Book>().id.value).build() }
    },
    positionUpdater = mockk(relaxed = true),
    scope = TestScope(),
  )

  @Test
  fun `entering the car swaps an adult book for the most recent regular one`() = runTest {
    currentBookStore.updateData { adult.id }

    guard.onEnterCar()

    currentBookStore.data.first() shouldBe newerRegular.id
    swapStore.data.first() shouldBe AdultCarSwap(adultBookId = adult.id, fallbackBookId = newerRegular.id)
    verify { player.setMediaItem(match<MediaItem> { it.mediaId == newerRegular.id.value }) }
  }

  @Test
  fun `entering the car keeps an adult book that is already playing`() = runTest {
    currentBookStore.updateData { adult.id }
    every { player.isPlaying } returns true

    guard.onEnterCar()

    currentBookStore.data.first() shouldBe adult.id
    swapStore.data.first() shouldBe null
  }

  @Test
  fun `entering the car keeps a regular book`() = runTest {
    currentBookStore.updateData { olderRegular.id }

    guard.onEnterCar()

    currentBookStore.data.first() shouldBe olderRegular.id
    swapStore.data.first() shouldBe null
    verify(exactly = 0) { player.setMediaItem(any<MediaItem>()) }
  }

  @Test
  fun `entering the car without regular books leaves nothing to resume`() = runTest {
    books = listOf(adult)
    currentBookStore.updateData { adult.id }

    guard.onEnterCar()

    currentBookStore.data.first() shouldBe null
    verify { player.clearMediaItems() }
  }

  @Test
  fun `leaving the car restores the adult book`() = runTest {
    currentBookStore.updateData { adult.id }
    guard.onEnterCar()

    guard.onLeaveCar()

    currentBookStore.data.first() shouldBe adult.id
    swapStore.data.first() shouldBe null
    verify { player.setMediaItem(match<MediaItem> { it.mediaId == adult.id.value }) }
  }

  @Test
  fun `leaving the car keeps a book picked in the car`() = runTest {
    currentBookStore.updateData { adult.id }
    guard.onEnterCar()
    currentBookStore.updateData { olderRegular.id }

    guard.onLeaveCar()

    currentBookStore.data.first() shouldBe olderRegular.id
    swapStore.data.first() shouldBe null
  }

  @Test
  fun `leaving the car keeps a book that is still playing`() = runTest {
    currentBookStore.updateData { adult.id }
    guard.onEnterCar()
    every { player.isPlaying } returns true

    guard.onLeaveCar()

    currentBookStore.data.first() shouldBe newerRegular.id
  }

  private fun testBook(
    name: String,
    lastPlayedAt: Long,
    isAdult: Boolean = false,
  ): Book {
    val chapter = Chapter(
      id = ChapterId("$name-chapter"),
      name = name,
      duration = 10_000,
      fileLastModified = Instant.EPOCH,
      markData = emptyList(),
    )
    val book = book(listOf(chapter), id = BookId(name))
    return book.copy(
      content = book.content.copy(
        lastPlayedAt = Instant.ofEpochSecond(lastPlayedAt),
        isAdult = isAdult,
      ),
    )
  }
}
