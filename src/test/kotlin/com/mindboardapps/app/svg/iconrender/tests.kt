package com.mindboardapps.app.svg.iconrender

import kotlin.test.assertEquals
import org.junit.Test

import java.io.File
import java.awt.Color
import javax.imageio.ImageIO

class TestSource {

    companion object {
        val SVG_PEN  = Pair(
            "pen.png",
            "M 9.5,2.6 v 1 h 5 v -1 z M 9.5,4.1 v 13 h 5 v -13 z M 9.6,17.6 l 2.1,4.2 q 0.3,0.5 0.6,0 l 2.1,-4.2 z")
        val SVG_UNDO = Pair(
            "undo.png",
            "M 10.56,5.3999996 L 4.56,12.0 L 10.56,18.6 L 10.56,15.0 Q 18.96,15.6 21.359999,4.7999997 Q 18.96,9.6 10.56,9.0 z")
        val SVG_REDO = Pair(
            "redo.png",
            "M 13.44,18.6 L 19.44,12.0 L 13.44,5.3999996 L 13.44,9.0 Q 5.0399995,8.4 2.6399994,19.2 Q 5.0399995,14.4 13.44,15.0 z")
    }

    @Test fun test1() {
        val list = arrayOf( SVG_PEN, SVG_UNDO, SVG_REDO )
        for( item in list ){
            val pngFile = File(item.first)
            val svgCommands = item.second
            val width = 256
            val height = 256
            val paintOptions = PaintOptions( Color(162,172,170, 255), Color(255,255,255, 0), true )
        
            ImageIO.write(
                IconBuilder(svgCommands, width, height, paintOptions).createImage(),
                "PNG",
                pngFile) 
    
            assertEquals(true, pngFile.exists())
        }
    }
}
