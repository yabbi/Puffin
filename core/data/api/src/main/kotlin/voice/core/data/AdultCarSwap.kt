package voice.core.data

import kotlinx.serialization.Serializable

/** The adult book that was current when a car connected, and the book that replaced it. */
@Serializable
public data class AdultCarSwap(
  val adultBookId: BookId,
  val fallbackBookId: BookId?,
)
