import java.io.File
import kotlin.math.roundToInt

class Frame(
    val filePath: String,
) {
    val width: Int
    val height: Int
    val company: String
    val type: Type
    val color: String
    val shape: Shape
    val widthPixels: Int
        get() = width * 100
    val heightPixels: Int
        get() = height * 100
    val imageWidth: Double
    val imageHeight: Double
    val imageWidthPixels: Int
        get() = (imageWidth * 100).roundToInt()
    val imageHeightPixels: Int
        get() = (imageHeight * 100).roundToInt()

    init {
        val filename = File(filePath).name
        val matchResult = FrameRegex.regex.matchEntire(filename)
        company = matchResult!!.groups[1]!!.value
        type = Type.valueOf(matchResult.groups[2]!!.value)
        color = matchResult.groups[3]!!.value
        width = matchResult.groups[4]!!.value.toInt()
        height = matchResult.groups[5]!!.value.toInt()
        imageHeight = height + findImageSizeDiff()
        imageWidth = width + findImageSizeDiff()
        shape = findShape()
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

    // difference in image size from frame size
    private fun findImageSizeDiff(): Double {
        return when (type) {
            Type.CAB -> 0.25
            Type.FP -> -3.5
            Type.G -> 4.0
            Type.FMW -> -2.75
            Type.FFMW -> 1.25
            Type.FMP -> findFramePrintWithMatSizeDiff()
        }
    }

    private fun findFramePrintWithMatSizeDiff(): Double {
        return when {
            width == 30 && height == 30 -> -8.5
            width == 40 && height == 30 -> -9.25
            width == 30 && height == 40 -> -9.25
            else -> -7.5
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
