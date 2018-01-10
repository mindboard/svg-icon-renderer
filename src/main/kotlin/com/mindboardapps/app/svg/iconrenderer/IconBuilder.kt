package com.mindboardapps.app.svg.iconrenderer

import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage


class IconBuilder(
        private val svgCommands: String,
        private val width: Int,
        private val height: Int,
        private val paintOptions: PaintOptions) {

    fun createImage(): BufferedImage {
        val path = SVGParser().parse(svgCommands)

        val img = BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
        val g = img.graphics as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // paint background
        g.color = paintOptions.background
        g.fillRect(0, 0, width, height)

        val scaleX = width.toDouble() / 24.toDouble()
        val scaleY = height.toDouble() / 24.toDouble()

        val transform1 = AffineTransform.getScaleInstance(scaleX, scaleY)
        val path2 = path.createTransformedShape(transform1)
        g.color = paintOptions.foreground

        if (paintOptions.fill) {
            g.fill(path2)
        }
        else {
            g.stroke = BasicStroke(paintOptions.strokeWidth)
            g.draw(path2)
        }

        //
        g.dispose()

        return img
    }
}
