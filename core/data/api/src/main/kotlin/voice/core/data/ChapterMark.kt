package voice.core.data

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
public data class MarkData(
  val startMs: Long,
  val name: String,
) : Comparable<MarkData> {
  override fun compareTo(other: MarkData): Int {
    return startMs.compareTo(other.startMs)
  }
}

@Serializable
public data class ChapterMark(
  val name: String?,
  val startMs: Long,
  val endMs: Long,
) {

  init {
    require(startMs < endMs) {
      "Start must be less than end in $this"
    }
  }

  public operator fun contains(position: Duration): Boolean = position.inWholeMilliseconds in startMs..endMs
  public operator fun contains(positionMs: Long): Boolean = positionMs in startMs..endMs
}

public val ChapterMark.durationMs: Long get() = (endMs - startMs).coerceAtLeast(0L)

public fun Chapter.markForPosition(positionInChapterMs: Long): ChapterMark {
  return chapterMarks.find { positionInChapterMs in it.startMs..it.endMs }
    ?: chapterMarks.firstOrNull { positionInChapterMs == it.endMs }
    ?: chapterMarks.first()
}

/** 1-based number of the chapter mark at the position, counted across all files of the book. */
public fun List<Chapter>.markNumber(
  chapterIndex: Int,
  positionInChapterMs: Long,
): Int {
  val chapter = this[chapterIndex]
  val marksBefore = take(chapterIndex).sumOf { it.chapterMarks.size }
  return marksBefore + chapter.chapterMarks.indexOf(chapter.markForPosition(positionInChapterMs)) + 1
}

public val Book.currentMarkNumber: Int
  get() = chapters.markNumber(content.currentChapterIndex, content.positionInChapter)

/**
 * Resolves the human-facing chapter name and the position/length *within that chapter mark*
 * (not the whole audio file, not the global book position) for a position inside this chapter.
 */
public data class ChapterPositionInfo(
  val name: String?,
  val positionInMarkMs: Long,
  val markDurationMs: Long,
)

public fun Chapter.positionInfo(positionInChapterMs: Long): ChapterPositionInfo {
  val mark = markForPosition(positionInChapterMs)
  return ChapterPositionInfo(
    name = mark.name ?: name,
    positionInMarkMs = (positionInChapterMs - mark.startMs).coerceAtLeast(0L),
    markDurationMs = mark.durationMs,
  )
}
