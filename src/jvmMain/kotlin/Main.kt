import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File

@Preview
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Art Thumbnail Generator",
        state = rememberWindowState(width = 300.dp, height = 300.dp),
    ) {
        val workingDirectory = remember { mutableStateOf<String?>(null) }
        val progress = remember { mutableStateOf(0.0f) }

        val fileDialog = java.awt.FileDialog(java.awt.Frame()).apply {
            isVisible = false
            addWindowListener(object : java.awt.event.WindowAdapter() {
                override fun windowClosed(e: java.awt.event.WindowEvent?) {
                    workingDirectory.value = directory
                }
            })
        }

        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        fileDialog.isVisible = true
                    },
                ) {
                    Text(workingDirectory.value ?: "Choose image")
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        val dir = File(workingDirectory.value!!)
                        val outputDir = File(workingDirectory.value!! + "/output")
                        if (!outputDir.exists()) {
                            outputDir.mkdir()
                        }

                        val imageFiles = dir.listFiles()?.filter { it.isFile && it.extension == "png" }
                        var counter = 0

                        imageFiles?.forEach { file ->
                            // read master image
                            val image = javax.imageio.ImageIO.read(file)

                            // find frames compatible with image
                            val frames = getCompatibleFrames(file.name, workingDirectory.value!!)

                            frames.forEach { frame ->
                                // read frame
                                val frameFile = File(frame.filePath)
                                val frameImage = javax.imageio.ImageIO.read(frameFile)

                                // scale image to fit in frame
                                val imageSize = calculateImageSize(frame)
                                val scaledImage = image.getScaledInstance(imageSize.first, imageSize.second, Image.SCALE_SMOOTH)

                                // layer images to create thumbnail
                                val finalImage = BufferedImage(Constants.FRAME_CANVAS_WIDTH, Constants.FRAME_CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB)
                                val graphics = finalImage.createGraphics()

                                // calculate image position
                                val imagePosition = calculateImagePosition(frame)
                                graphics.drawImage(scaledImage, imagePosition.first, imagePosition.second, null)
                                graphics.drawImage(frameImage, 0, 0, null)

                                // determine new filename
                                val newFileName = file.nameWithoutExtension.split("-").first().trim() + frameFile.name

                                // save thumbnail
                                val outputFile = File(outputDir, newFileName)
                                javax.imageio.ImageIO.write(finalImage, "PNG", outputFile)
                            }

                            counter++
                            progress.value = counter.toFloat() / imageFiles.size.toFloat()
                        }
                        workingDirectory.value = null
                    },
                    enabled = workingDirectory.value != null,
                ) {
                    Text("Generate")
                }
                LinearProgressIndicator(progress = progress.value)
            }
        }
    }
}

fun calculateImageSize(frame: Frame): Pair<Int, Int> {
    val imageWidth = frame.imageWidthPixels
    val imageHeight = frame.imageHeightPixels
    return Pair(imageWidth, imageHeight)
}

fun calculateImagePosition(frame: Frame): Pair<Int, Int> {
    val frameCanvasWidth = Constants.FRAME_CANVAS_WIDTH
    val frameCanvasHeight = Constants.FRAME_CANVAS_HEIGHT
    val x = ((frameCanvasWidth - frame.imageWidthPixels) / 2)
    val y = ((frameCanvasHeight - frame.imageHeightPixels) / 2)
    return Pair(x, y)
}

fun getImageShape(imageFilename: String): Shape {
    val imageShape = when {
        imageFilename.endsWith("horizontal.png", ignoreCase = true) -> Shape.HORIZONTAL
        imageFilename.endsWith("horizontal 3624.png", ignoreCase = true) -> Shape.HORIZONTAL_36_24
        imageFilename.endsWith("square.png", ignoreCase = true) -> Shape.SQUARE
        imageFilename.endsWith("vertical.png", ignoreCase = true) -> Shape.VERTICAL
        imageFilename.endsWith("vertical 2436.png", ignoreCase = true) -> Shape.VERTICAL_24_36
        else -> throw IllegalArgumentException("Invalid image filename: $imageFilename")
    }
    return imageShape
}

fun getCompatibleFrames(imageFilename: String, workingDirectory: String): List<Frame> {
    val imageShape = getImageShape(imageFilename)
    val dir = File(workingDirectory)
    val framesDir = File(dir.parent + "/frames")
    val allFrameFiles = recursiveListFiles(framesDir)
    val allFrames = allFrameFiles.map { Frame(it.absolutePath) }
    return allFrames.filter { imageShape == it.shape }
}

fun recursiveListFiles(dir: File): List<File> {
    val these = dir.listFiles()?.toList() ?: emptyList()
    return these.filter { it.isFile && it.extension == "png" }.toList().plus(these.filter { it.isDirectory }.flatMap { recursiveListFiles(it) })
}

fun Image.toBufferedImage(): BufferedImage {
    if (this is BufferedImage) {
        return this
    }
    val bufferedImage = BufferedImage(this.getWidth(null), this.getHeight(null), BufferedImage.TYPE_INT_ARGB)

    val graphics2D = bufferedImage.createGraphics()
    graphics2D.drawImage(this, 0, 0, null)
    graphics2D.dispose()

    return bufferedImage
}
