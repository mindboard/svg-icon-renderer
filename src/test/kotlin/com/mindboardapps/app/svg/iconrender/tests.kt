package com.mindboardapps.app.svg.iconrender

import kotlin.test.assertEquals
import org.junit.Test

import java.io.File
import java.awt.Color
import javax.imageio.ImageIO

class TestSource {
    @Test fun test1() {
        val svgCommands = "M 9.5,2.6 v 1 h 5 v -1 z M 9.5,4.1 v 13 h 5 v -13 z M 9.6,17.6 l 2.1,4.2 q 0.3,0.5 0.6,0 l 2.1,-4.2 z"
    
        val width = 32
        val height = 32
    
        val outputPngFile = File("pen.png")
        val paintOptions = PaintOptions( Color(34,52,62), Color(221,218,197), true )
    
        ImageIO.write(
            IconBuilder(svgCommands, width, height, paintOptions).createImage(),
            "PNG",
            outputPngFile) 

        assertEquals(true, outputPngFile.exists())
    }
}
