package com.mindboardapps.app.svg.iconrenderer

import java.util.*

class SVGParser {
    private val isUpperCase: (s: String) -> Boolean = { it == it.toUpperCase() }

    private fun createTokenList(svgCmds: String): ArrayList<Token> {
        val tokenList = ArrayList<Token>()
        val regex = Regex("[MmLlHhVvCcSsQqZz]")
        svgCmds.forEach {
            if (regex.matches(it.toString())) {
                val token = Token(it)
                tokenList.add(token)
            } else {
                val lastToken = tokenList[tokenList.size - 1]
                lastToken.params += it.toString()
            }
        }
        return tokenList
    }

    fun parse(svgCmds: String): Path {
        val path = Path()

        var currentX = 0f
        var currentY = 0f

        val proc1: (token: Token, drawCmd: (x: Float, y: Float) -> Unit) -> Unit = { token, drawCmd ->
            token.setCurrentValues(currentX, currentY)

            // x,y 値を得る
            val x = token.getX()
            val y = token.getY()

            // 描写実行
            drawCmd(x, y)

            // currentX,Y 値の更新
            currentX = x
            currentY = y
        }

        // quadTo などの点パラメータが 2個のもの
        // TODO quadTo に固定されているならば... わざわざ drawCmd を closure として渡す意味ないよね？
        // まあ将来の拡張性はあることになるけど
        val proc2: (token: Token, drawCmd: (x1: Float, y1: Float, x2: Float, y2: Float) -> Unit) -> Unit = { token, drawCmd ->
            token.setCurrentValues(currentX, currentY)

            // x,y 値を得る
            val x1 = token.getX(0)
            val y1 = token.getY(0)

            val x2 = token.getX(1)
            val y2 = token.getY(1)

            // 描写実行
            drawCmd(x1, y1, x2, y2)

            // currentX,Y 値の更新
            currentX = x2
            currentY = y2
        }

        // quadTo などの点パラメータが 3個のもの
        // TODO cubicTo に固定されているならば... わざわざ drawCmd を closure として渡す意味ないよね？
        // まあ将来の拡張性はあることになるけど
        val proc3: (token: Token, drawCmd: (x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) -> Unit) -> Unit = { token, drawCmd ->
            token.setCurrentValues(currentX, currentY)

            // x,y 値を得る
            val x1 = token.getX(0)
            val y1 = token.getY(0)

            val x2 = token.getX(1)
            val y2 = token.getY(1)

            val x3 = token.getX(2)
            val y3 = token.getY(2)

            // 描写実行
            drawCmd(x1, y1, x2, y2, x3, y3)

            // currentX,Y 値の更新
            currentX = x3
            currentY = y3
        }

        var prevToken: Token? = null
        createTokenList(svgCmds).forEach { token ->
            val svgCmd = token.type
            when (svgCmd) {
                'M', 'm' -> proc1(token, { x, y -> path.moveTo(x, y) })
                'L', 'H', 'V', 'l', 'h', 'v' -> proc1(token, { x, y -> path.lineTo(x, y) })
                'C', 'c' -> proc3(token, { x1, y1, x2, y2, x3, y3 -> path.cubicTo(x1, y1, x2, y2, x3, y3) })
                'S', 's' -> {
                    // S は前に示したものと同種の曲線を生成しますが、これが別の S コマンドや C コマンドの後に続く場合は、1 番目の制御点が前を曲線で用いられた制御点の対向にするものとみなします。
                    // S コマンドが別の S または C コマンドの後にない場合は、その曲線の 2 つの制御点は同じ場所であるとみなします。
                    if (prevToken != null) {
                        proc2(token, { x1, y1, x2, y2 ->
                            when (prevToken?.type) {
                            //
                            // s,c だった場合（つまり相対値だったら）はこの計算では間違っている...
                            //
                                'S', 's' -> {
                                    val prevX1 = prevToken!!.getX(0)
                                    val prevY1 = prevToken!!.getY(0)
                                    val prevX2 = prevToken!!.getX(1)
                                    val prevY2 = prevToken!!.getY(1)

                                    val taikouPointX = prevX2 + (prevX2 - prevX1)
                                    val taikouPointY = prevY2 + (prevY2 - prevY1)

                                    path.cubicTo(taikouPointX, taikouPointY, x1, y1, x2, y2)
                                }
                                'C', 'c' -> {
                                    val prevX1 = prevToken!!.getX(1)
                                    val prevY1 = prevToken!!.getY(1)
                                    val prevX2 = prevToken!!.getX(2)
                                    val prevY2 = prevToken!!.getY(2)

                                    val taikouPointX = prevX2 + (prevX2 - prevX1)
                                    val taikouPointY = prevY2 + (prevY2 - prevY1)

                                    path.cubicTo(taikouPointX, taikouPointY, x1, y1, x2, y2)
                                }
                                else -> {
                                    path.cubicTo(x1, y1, x1, y1, x2, y2)
                                }
                            }
                        })
                    } else {
                        proc2(token, { x1, y1, x2, y2 -> path.cubicTo(x1, y1, x1, y1, x2, y2) })
                    }
                }
                'Q', 'q' -> proc2(token, { x1, y1, x2, y2 -> path.quadTo(x1, y1, x2, y2) })
                'Z', 'z' -> path.close()
            }

            prevToken = token
        }

        return path
    }

    class Token(type: Char) {
        val type = type
        var params = ""

        private var currentX: Float = 0f
        private var currentY: Float = 0f

        fun setCurrentValues(x: Float, y: Float) {
            this.currentX = x
            this.currentY = y
        }

        companion object {
            val BR = System.getProperty("line.separator")
        }

        private fun fix(s: String): String = s.split(delimiters = BR).map({ it.trim() }).joinToString(separator = "")

        fun toFloat(index: Int): Float {
            val fixedParams = fix(params)
            val array = fixedParams.split(regex = Regex("[, ]"))
            return if (index < array.size) {
                array[index].toFloat()
            } else {
                0f
            }
        }


        fun getX(index: Int = 0): Float {
            var retVal: Float = 0f
            val offset = index * 2
            val v0 = toFloat(0 + offset)

            when (type) {
                'H' -> retVal = v0 // X座標だけがかわるタイプ
                'h' -> retVal = v0 + currentX // X座標だけがかわるタイプ

                'V', 'v' -> retVal = currentX // Y座標だけがかわるタイプ( Xはカレントを維持 )

                'M', 'L', 'C', 'S', 'Q' -> retVal = v0
                'm', 'l', 'c', 's', 'q' -> retVal = v0 + currentX
            }
            return retVal
        }

        //fun getY(currentY: Float, index:Int=0):Float {
        fun getY(index: Int = 0): Float {
            var retVal: Float = 0f

            val offset = index * 2
            val v0 = toFloat(0 + offset)
            val v1 = toFloat(1 + offset)

            when (type) {
                'H', 'h' -> retVal = currentY // X座標だけがかわるタイプ ( Yはカレントを維持 )

                'V' -> retVal = v0 // Y座標だけがかわるタイプ
                'v' -> retVal = v0 + currentY // Y座標だけがかわるタイプ

                'M', 'L', 'C', 'S', 'Q' -> retVal = v1
                'm', 'l', 'c', 's', 'q' -> retVal = v1 + currentY
            }
            return retVal
        }
    }
}
