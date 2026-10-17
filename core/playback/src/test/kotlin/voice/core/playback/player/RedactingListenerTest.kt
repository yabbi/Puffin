package voice.core.playback.player

import androidx.media3.common.Player
import io.kotest.matchers.shouldBe
import io.mockk.mockkClass
import org.junit.Test
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class RedactingListenerTest {

  @Test
  fun `forwards every listener callback`() {
    val received = mutableSetOf<String>()
    val delegate = Proxy.newProxyInstance(
      Player.Listener::class.java.classLoader,
      arrayOf(Player.Listener::class.java),
    ) { _, method, _ ->
      received += method.signature()
      null
    } as Player.Listener
    val listener = RedactingListener(delegate) { it }

    val callbacks = Player.Listener::class.java.methods.toList()
    callbacks.forEach { method ->
      method.invoke(listener, *method.parameterTypes.map(::dummyArgument).toTypedArray())
    }

    received shouldBe callbacks.map { it.signature() }.toSet()
  }

  private fun Method.signature(): String = "$name(${parameterTypes.joinToString { it.simpleName }})"

  private fun dummyArgument(type: Class<*>): Any = when (type) {
    Boolean::class.javaPrimitiveType -> false
    Int::class.javaPrimitiveType -> 0
    Long::class.javaPrimitiveType -> 0L
    Float::class.javaPrimitiveType -> 0F
    List::class.java -> emptyList<Any>()
    else -> mockkClass(type.kotlin, relaxed = true)
  }
}
