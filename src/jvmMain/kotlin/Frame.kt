import java.io.File
import kotlin.math.roundToInt

class Frame(
    val filePath: String,
) {
    val width: Int
    val height: Int
    val type: Type
    val color: String?
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
        type = Type.valueOf(matchResult!!.groups[1]!!.value.replace("-", ""))
        color = matchResult.groups[2]?.value
        width = matchResult.groups[3]!!.value.toInt()
        height = matchResult.groups[4]!!.value.toInt()
        val imageSize = findImageSize()
        imageWidth = imageSize.first
        imageHeight = imageSize.second
        shape = findShape()
    }

    private fun findShape(): Shape {
        return if (type == Type.FMP) {
            when {
                width < height -> Shape.VERTICAL
                width == height -> Shape.SQUARE
                width == 36 && height == 24 -> Shape.HORIZONTAL_36_24
                width == 40 && height == 20 -> Shape.HORIZONTAL_48_20
                (width == 48 || width == 60) && height == 20 -> Shape.HORIZONTAL_60_20
                width == 60 && height == 30 -> Shape.HORIZONTAL_48_20
                else -> Shape.HORIZONTAL
            }
        } else if (type == Type.FTP || type == Type.GW || type == Type.FFMW || type == Type.AC) {
            when {
                width < height -> Shape.VERTICAL
                width == height -> Shape.SQUARE
                width == 36 && height == 24 -> Shape.HORIZONTAL_36_24
                width == 40 && height == 20 -> Shape.HORIZONTAL_40_20
                width == 48 && height == 20 -> Shape.HORIZONTAL_48_20
                width == 60 && height == 20 -> Shape.HORIZONTAL_60_20
                width == 60 && height == 30 -> Shape.HORIZONTAL_40_20
                else -> Shape.HORIZONTAL
            }
        } else {
            throw IllegalStateException("Not implemented")
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
                width == 18 && height == 24 -> Pair(12.56, 16.75)
                width == 20 && height == 20 -> Pair(12.75, 12.75)
                width == 24 && height == 30 -> Pair(16.69, 22.25)
                width == 30 && height == 24 -> Pair(22.25, 16.69)
                width == 30 && height == 30 -> Pair(21.75, 21.75)
                width == 36 && height == 24 -> Pair(28.25, 18.83)
                width == 36 && height == 36 -> Pair(27.5, 27.5)
                width == 40 && height == 20 -> Pair(32.25, 13.44)
                width == 40 && height == 30 -> Pair(31.25, 23.44)
                width == 48 && height == 20 -> Pair(33.6, 11.2)
                width == 60 && height == 20 -> Pair(35.0, 11.67)
                width == 60 && height == 30 -> Pair(36.0, 15.0)
                else -> throw IllegalArgumentException("Invalid frame size: $width x $height for type $type")
            }
        } else if (type == Type.FTP) {
            return when {
                width == 18 && height == 24 -> Pair(15.3, 20.4)
                width == 20 && height == 20 -> Pair(16.5, 16.5)
                width == 24 && height == 30 -> Pair(20.5, 27.33)
                width == 30 && height == 24 -> Pair(27.33, 20.5)
                width == 30 && height == 30 -> Pair(26.5, 26.5)
                width == 36 && height == 24 -> Pair(32.5, 21.67)
                width == 36 && height == 36 -> Pair(32.5, 32.5)
                width == 40 && height == 20 -> Pair(36.5, 18.25)
                width == 40 && height == 30 -> Pair(36.5, 27.38)
                width == 48 && height == 20 -> Pair(37.5, 15.52)
                width == 60 && height == 20 -> Pair(38.0, 12.67)
                width == 60 && height == 30 -> Pair(38.0, 19.0)
                else -> throw IllegalArgumentException("Invalid frame size: $width x $height for type $type")
            }
        } else if (type == Type.GW) {
            return when {
                width == 18 && height == 24 -> Pair(22.0, 29.33)
                width == 20 && height == 20 -> Pair(24.0, 24.0)
                width == 24 && height == 30 -> Pair(28.0, 37.33)
                width == 30 && height == 24 -> Pair(37.33, 28.0)
                width == 30 && height == 30 -> Pair(34.0, 34.0)
                width == 36 && height == 24 -> Pair(42.0, 28.0)
                width == 36 && height == 36 -> Pair(40.0, 40.0)
                width == 40 && height == 20 -> Pair(48.0, 24.0)
                width == 40 && height == 30 -> Pair(45.33, 34.0)
                width == 48 && height == 20 -> Pair(45.6, 19.0)
                width == 60 && height == 20 -> Pair(46.5, 15.5)
                width == 60 && height == 30 -> Pair(44.0, 22.0)
                else -> throw IllegalArgumentException("Invalid frame size: $width x $height for type $type")
            }
        } else if (type == Type.FFMW) {
            return when {
                width == 40 && height == 20 -> Pair(42.0, 21.0)
                width == 48 && height == 20 -> Pair(42.0, 17.5)
                width == 60 && height == 20 -> Pair(42.75, 14.25)
                width == 60 && height == 30 -> Pair(42.0, 21.0)
                else -> throw IllegalArgumentException("Invalid frame size: $width x $height for type $type")
            }
        } else if (type == Type.AC) {
            return when {
                width == 18 && height == 24 -> Pair(18.25, 24.33)
                width == 20 && height == 20 -> Pair(20.25, 20.25)
                width == 24 && height == 30 -> Pair(24.25, 32.33)
                width == 30 && height == 24 -> Pair(32.33, 24.25)
                width == 30 && height == 30 -> Pair(30.25, 30.25)
                width == 36 && height == 24 -> Pair(36.37, 24.25)
                width == 36 && height == 36 -> Pair(36.25, 36.25)
                width == 40 && height == 20 -> Pair(40.5, 20.5)
                width == 40 && height == 30 -> Pair(40.33, 30.25)
                width == 48 && height == 20 -> Pair(40.25, 16.77)
                width == 60 && height == 20 -> Pair(40.52, 13.42)
                width == 60 && height == 30 -> Pair(40.5, 20.25)
                else -> throw IllegalArgumentException("Invalid frame size: $width x $height for type $type")
            }
        } else {
            throw IllegalArgumentException("Invalid frame type: $type")
        }
    }
}

enum class Type {
    AC, // Acrylic
    FMP, // Framed Print with Mat
    FTP, // Framed Textured Panel
    GW, // Gallery Wrapped Canvas
    FFMW, // Museum Wrap Canvas, Float Frame
}

object FrameRegex {
    val regex = Regex("^([A-Z\\-]{2,5})-(?>(\\d{2})-)?(\\d{2})(\\d{2})\\.png")
}
