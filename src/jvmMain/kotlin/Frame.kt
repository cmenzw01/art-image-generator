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
        val imageSize = findImageSize()
        imageWidth = imageSize.first
        imageHeight = imageSize.second
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
//    private fun findImageSizeDiff(): Double {
//        return when (type) {
//            Type.CAB -> 0.25
//            Type.FP -> -3.5
//            Type.G -> 4.0
//            Type.FMW -> -2.75
//            Type.FFMW -> 1.25
//            Type.FMP -> findFramePrintWithMatSizeDiff()
//        }
//    }

//    private fun findFramePrintWithMatSizeDiff(): Double {
//        return when {
//            width == 30 && height == 30 -> -8.25
//            width == 40 && height == 30 -> -9.25
//            width == 30 && height == 40 -> -9.25
//            else -> -7.75
//        }
//    }

    private fun findImageSize(): Pair<Double, Double> {
        if (type == Type.FMP) {
            return when {
                width == 24 && height == 24 -> Pair(16.25, 16.25)
                width == 30 && height == 30 -> Pair(21.75, 21.75)
                width == 30 && height == 24 -> Pair(22.25, 16.69)
                width == 24 && height == 30 -> Pair(16.69, 22.25)
                width == 36 && height == 24 -> Pair(28.25, 18.83)
                width == 24 && height == 36 -> Pair(18.83, 28.25)
                width == 40 && height == 30 -> Pair(31.25, 23.44)
                width == 30 && height == 40 -> Pair(23.44, 31.25)
                else -> throw IllegalArgumentException("Invalid frame size: $width x $height for type $type")
            }
        } else if (type == Type.FP) {
            return when {
                width == 24 && height == 24 -> Pair(20.5, 20.5)
                width == 30 && height == 30 -> Pair(26.5, 26.5)
                width == 30 && height == 24 -> Pair(27.33, 20.5)
                width == 24 && height == 30 -> Pair(20.5, 27.33)
                width == 36 && height == 24 -> Pair(32.5, 21.67)
                width == 24 && height == 36 -> Pair(21.67, 32.5)
                width == 40 && height == 30 -> Pair(36.5, 27.38)
                width == 30 && height == 40 -> Pair(27.3, 36.5)
                else -> throw IllegalArgumentException("Invalid frame size: $width x $height for type $type")
            }
        } else if (type == Type.G) {
            return when {
                width == 24 && height == 24 -> Pair(28.0, 28.0)
                width == 30 && height == 30 -> Pair(34.0, 34.0)
                width == 30 && height == 24 -> Pair(37.33, 28.0)
                width == 24 && height == 30 -> Pair(28.0, 37.33)
                width == 36 && height == 24 -> Pair(42.0, 28.0)
                width == 24 && height == 36 -> Pair(28.0, 42.0)
                width == 40 && height == 30 -> Pair(45.33, 34.0)
                width == 30 && height == 40 -> Pair(34.0, 45.33)
                else -> throw IllegalArgumentException("Invalid frame size: $width x $height for type $type")
            }
        } else if (type == Type.FFMW) {
            return when {
                width == 24 && height == 24 -> Pair(25.25, 25.25)
                width == 30 && height == 30 -> Pair(31.25, 31.25)
                width == 30 && height == 24 -> Pair(33.67, 25.25)
                width == 24 && height == 30 -> Pair(25.25, 33.67)
                width == 36 && height == 24 -> Pair(37.88, 25.25)
                width == 24 && height == 36 -> Pair(25.25, 37.88)
                width == 40 && height == 30 -> Pair(41.67, 31.25)
                width == 30 && height == 40 -> Pair(31.25, 41.67)
                else -> throw IllegalArgumentException("Invalid frame size: $width x $height for type $type")
            }
        } else if (type == Type.FMW) {
            return when {
                width == 24 && height == 24 -> Pair(21.25, 21.25)
                width == 30 && height == 30 -> Pair(27.25, 27.25)
                width == 30 && height == 24 -> Pair(27.25, 21.25)
                width == 24 && height == 30 -> Pair(21.25, 27.25)
                width == 36 && height == 24 -> Pair(33.25, 21.25)
                width == 24 && height == 36 -> Pair(21.25, 33.25)
                width == 40 && height == 30 -> Pair(37.25, 27.25)
                width == 30 && height == 40 -> Pair(27.25, 37.25)
                else -> throw IllegalArgumentException("Invalid frame size: $width x $height for type $type")
            }
        } else if (type == Type.CAB) {
            return when {
                width == 24 && height == 24 -> Pair(24.25, 24.25)
                width == 30 && height == 30 -> Pair(30.25, 30.25)
                width == 30 && height == 24 -> Pair(32.33, 24.25)
                width == 24 && height == 30 -> Pair(24.25, 32.33)
                width == 36 && height == 24 -> Pair(36.37, 24.25)
                width == 24 && height == 36 -> Pair(24.25, 36.37)
                width == 40 && height == 30 -> Pair(30.25, 40.33)
                width == 30 && height == 40 -> Pair(40.33, 30.25)
                else -> throw IllegalArgumentException("Invalid frame size: $width x $height for type $type")
            }
        } else {
            throw IllegalArgumentException("Invalid frame type: $type")
        }
    }
}

enum class Type {
    CAB, // Acrylic
    FP, // Frame Print NO mat
    FMP, // Framed Print with Mat
    G, // Gallery Wrapped Canvas
    FMW, // Museum Wrap Canvas, Cap Frame
    FFMW, // Museum Wrap Canvas, Float Frame
}

object FrameRegex {
    val regex = Regex("^([A-Z]{2})([A-Z]{1,4})([A-Z])(\\d{2})(\\d{2})\\.png")
}
