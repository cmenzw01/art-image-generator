import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser

@OptIn(ExperimentalMaterialApi::class)
@Preview
fun main() = application {
    val scope = rememberCoroutineScope()
    val workingDirectory = remember { mutableStateOf<String?>(null) }
    val progress = remember { mutableStateOf(0.0f) }
    val animatedProgress = animateFloatAsState(
        targetValue = progress.value,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    ).value
    val buttonEnabled = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Art Preview Generator",
        state = rememberWindowState(width = 300.dp, height = 300.dp),
    ) {
        val jfc = JFileChooser()
        jfc.fileFilter = javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png")

        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        val returnValue = jfc.showOpenDialog(null)
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            val selectedFile = jfc.selectedFile
                            workingDirectory.value = selectedFile.parent
                            buttonEnabled.value = true
                        }
                    },
                ) {
                    Text(workingDirectory.value ?: "Choose image")
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        buttonEnabled.value = false
                        scope.launch(Dispatchers.IO) {
                            generateImages(workingDirectory, progress, errorMessage)
                        }
                    },
                    enabled = buttonEnabled.value,
                ) {
                    Text("Generate")
                }
                LinearProgressIndicator(progress = animatedProgress, modifier = Modifier.height(10.dp).align(Alignment.CenterHorizontally))
                Text((progress.value * 100).toInt().toString() + "%", modifier = Modifier.align(Alignment.CenterHorizontally))

                if (errorMessage.value != null) {
                    AlertDialog(
                        onDismissRequest = ::exitApplication,
                        confirmButton = {
                            TextButton(
                                onClick = ::exitApplication,
                            ) {
                                Text(text = "Ok")
                            }
                        },
                        title = {
                            Text(text = "Error")
                        },
                        text = {
                            Text(text = errorMessage.value ?: "Unknown error")
                        },
                    )
                }
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
        imageFilename.endsWith("3624.png", ignoreCase = true) -> Shape.HORIZONTAL_36_24
        imageFilename.endsWith("square.png", ignoreCase = true) -> Shape.SQUARE
        imageFilename.endsWith("vertical.png", ignoreCase = true) -> Shape.VERTICAL
        imageFilename.endsWith("2436.png", ignoreCase = true) -> Shape.VERTICAL_24_36
        else -> throw IllegalArgumentException("Invalid image filename: $imageFilename")
    }
    return imageShape
}

fun getCompatibleFrames(imageFilename: String, workingDirectory: String): List<Frame> {
    val imageShape = getImageShape(imageFilename)
    val dir = File(workingDirectory)
    val framesDir = File(dir.parent + "/frames")
    val allFrameFiles = recursiveListFiles(framesDir)
    val allFrames = allFrameFiles.map {
        try {
            Frame(it.absolutePath)
        } catch (e: Exception) {
            throw IllegalArgumentException("Error reading frame: ${it.absolutePath}")
        }
    }
    return allFrames.filter { imageShape == it.shape }
}

fun recursiveListFiles(dir: File): List<File> {
    val these = dir.listFiles()?.toList() ?: emptyList()
    return these.filter { it.isFile && it.extension == "png" }.toList().plus(these.filter { it.isDirectory }.flatMap { recursiveListFiles(it) })
}

suspend fun generateImages(workingDirectory: MutableState<String?>, progress: MutableState<Float>, errorMessage: MutableState<String?>) {
    try {
        val dir = File(workingDirectory.value!!)
        val outputDir = File("${workingDirectory.value!!}/output")
        if (!outputDir.exists()) {
            outputDir.mkdir()
        }

        val imageFiles = dir.listFiles()?.filter { it.isFile && it.extension == "png" }
        var imageCounter = 0f

        imageFiles?.forEach { file ->
            // read master image
            val image = ImageIO.read(file)

            // find frames compatible with image
            val frames = getCompatibleFrames(file.name, workingDirectory.value!!)
            var frameCounter = 0f
            val imagePercent = imageCounter / imageFiles.size

            frames.forEach { frame ->

                // read frame
                val frameFile = File(frame.filePath)
                val frameImage = ImageIO.read(frameFile)

                // scale image to fit in frame
                val imageSize = calculateImageSize(frame)
                val scaledImage = image.getScaledInstance(imageSize.first, imageSize.second, Image.SCALE_SMOOTH)

                // layer images to create thumbnail
                val finalImage = BufferedImage(
                    Constants.FRAME_CANVAS_WIDTH,
                    Constants.FRAME_CANVAS_HEIGHT,
                    BufferedImage.TYPE_INT_RGB,
                )
                val graphics = finalImage.createGraphics()

                // calculate image position
                val imagePosition = calculateImagePosition(frame)
                graphics.drawImage(scaledImage, imagePosition.first, imagePosition.second, null)
                graphics.drawImage(frameImage, 0, 0, null)

                // determine new filename
                val newFileName = file.nameWithoutExtension.split("-").first().trim() + frameFile.name

                // save thumbnail
                val outputFile = File(outputDir, newFileName)
                ImageIO.write(finalImage, "PNG", outputFile)

                frameCounter++
                val framePercent = frameCounter / (frames.size)
                progress.value = imagePercent + (framePercent / imageFiles.size)
            }

            imageCounter++
        }

        workingDirectory.value = null
    } catch (e: Exception) {
        errorMessage.value = e.message
    }
}
