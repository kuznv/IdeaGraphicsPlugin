package graphics.swing

import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import java.awt.Image
import javax.swing.JFrame

class GraphicsFrame(
    width: Int,
    height: Int,
    image: Image
) : JFrame() {

    init {
        title = "Graphics"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(width, height)
        setLocationRelativeTo(null)

        add(object : Canvas() {
            override fun paint(g: Graphics) {
                println("paint")
                g.drawImage(
                    image,
                    0,
                    0,
                    image.getWidth(this),
                    image.getHeight(this),
                    Color.gray,
                    this
                )
            }
        })
    }
}