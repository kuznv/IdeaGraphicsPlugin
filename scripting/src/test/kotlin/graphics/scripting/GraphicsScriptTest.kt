package graphics.scripting

import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import graphics.scripting.host.GraphicsScriptHost
import org.junit.Test
import java.awt.Color
import java.awt.Graphics
import kotlin.script.experimental.api.valueOr
import kotlin.script.experimental.api.valueOrThrow


class GraphicsScriptTest {

    private val host = GraphicsScriptHost()

    @Test
    fun compiles() {
        val graphics = mock<Graphics>()
        val result = host.eval(
            """
                println(2 + 2)
            """.trimIndent(),
            graphics
        )
        result.valueOr { result.reports.forEach(::println); it.valueOrThrow() }
    }

    @Test
    fun graphics() {
        val graphics = mock<Graphics>()
        val result = host.eval(
            """
                graphics.color = Color.green
                graphics.drawLine(1, 2, 3, 4)
            """.trimIndent(),
            graphics
        )
        result.valueOrThrow()

        inOrder(graphics) {
            verify(graphics).color = Color.green
            verify(graphics).drawLine(1, 2, 3, 4)
            verifyNoMoreInteractions()
        }
    }
    @Test
    fun `graphics receiver injected`() {
        val graphics = mock<Graphics>()
        val result = host.eval(
            """
                color = Color.green
                drawLine(1, 2, 3, 4)
            """.trimIndent(),
            graphics
        )
        result.valueOrThrow()

        inOrder(graphics) {
            verify(graphics).color = Color.green
            verify(graphics).drawLine(1, 2, 3, 4)
            verifyNoMoreInteractions()
        }
    }
}
