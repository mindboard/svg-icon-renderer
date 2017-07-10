package com.mindboardapps.app.svg.iconrenderer

import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.GeneralPath

class Path {

    val gPath = GeneralPath()

    fun createTransformedShape(at: AffineTransform): Shape {
        return gPath.createTransformedShape(at)
    }

    fun moveTo(x1: Float, y1: Float) {
        gPath.moveTo(x1.toDouble(), y1.toDouble())
    }

    fun lineTo(x1: Float, y1: Float) {
        gPath.lineTo(x1.toDouble(), y1.toDouble())
    }

    fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) {
        gPath.quadTo(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())
    }

    fun cubicTo(
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float,
            x3: Float,
            y3: Float) {

        gPath.curveTo(
                x1.toDouble(),
                y1.toDouble(),
                x2.toDouble(),
                y2.toDouble(),
                x3.toDouble(),
                y3.toDouble())
    }

    fun close() {
        gPath.closePath()
    }
}
