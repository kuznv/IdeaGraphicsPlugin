package graphics.swing

import java.awt.Color
import java.awt.Graphics
import java.awt.Image
import java.awt.image.BufferedImage


fun eval(width: Int = 400, height: Int = 400, program: Graphics.() -> Unit): Image =
    withGraphics(width, height, program)


fun withGraphics(width: Int, height: Int, program: (Graphics) -> Unit): Image =
    BufferedImage(width, height, BufferedImage.TYPE_INT_RGB).apply {
        createGraphics().run {
            background = Color.gray
            fillRect(0, 0, width, height)
            color = Color.black
            try {
                program(this)
            } finally {
                dispose()
            }
        }
    }