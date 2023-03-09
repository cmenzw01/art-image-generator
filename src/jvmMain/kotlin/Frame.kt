import java.io.File

class Frame(
    val filePath: String,
) {
    val width: Int
    val height: Int
    val company: String
    val type: Type
    val color: String
    val shape: Shape
    val padding: Int
    val wrapSize: Int
    val widthPixels: Int
        get() = width * 100
    val heightPixels: Int
        get() = height * 100

    init {
        val filename = File(filePath).name
        val matchResult = FrameRegex.regex.matchEntire(filename)
        company = matchResult!!.groups[1]!!.value
        type = Type.valueOf(matchResult.groups[2]!!.value)
        color = matchResult.groups[3]!!.value
        width = matchResult.groups[4]!!.value.toInt()
        height = matchResult.groups[5]!!.value.toInt()
        shape = findShape()
        padding = findPadding()
        wrapSize = findWrapSize()
    }

    private fun findShape(): Shape {
        return when {
            width == height -> Shape.SQUARE
            width == 36 && height == 24 -> Shape.HORIZONTAL_36_24
            width > height -> Shape.HORIZONTAL
            width == 24 && height == 36 -> Shape.VERTICAL_24_36
            else -> Shape.VERTICAL
        }
    }

    private fun findPadding(): Int {
        return if (type == Type.FP) {
            200
        } else if (type == Type.FMP && ((width == 30 && height == 40) || (width == 40 && height == 30))) {
            500
        } else if (type == Type.FMP) {
            375
        } else if (type == Type.FMW) {
            150
        } else if (type == Type.FFMW) {
            63
        } else {
            0
        }
    }

    private fun findWrapSize(): Int {
        return if (type == Type.FP) {
            25
        } else {
            0
        }
    }
}

enum class Type {
    CAB,
    FP,
    FMP,
    G,
    FMW,
    FFMW,
}

object FrameRegex {
    val regex = Regex("^([A-Z]{2})([A-Z]{1,4})([A-Z])(\\d{2})(\\d{2})\\.png")
}
