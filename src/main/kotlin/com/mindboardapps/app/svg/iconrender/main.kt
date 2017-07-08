package com.mindboardapps.app.svg.iconrender

import org.json.JSONObject
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    if( args.size>0 && File(args[0]).exists() ) {
        val text = File(args[0]).readText(charset("UTF-8"))
        val jsonObj = JSONObject(text)
        val svgCommands = jsonObj.getString("svgCommands")
        val width = jsonObj.getInt("width")
        val height = jsonObj.getInt("height")
        val pngFile = jsonObj.getString("pngFile")

        val createColor: (obj: JSONObject) -> Color = {
            Color(
                    it.getInt("r"),
                    it.getInt("g"),
                    it.getInt("b"),
                    it.getInt("a"))
        }

        val paintOptions = PaintOptions(
                createColor(jsonObj.getJSONObject("foregroundColor")),
                createColor(jsonObj.getJSONObject("backgroundColor")),
                jsonObj.getBoolean("fill"))

        ImageIO.write(
                IconBuilder(svgCommands, width, height, paintOptions).createImage(),
                "PNG",
                File(pngFile))
    }
}
