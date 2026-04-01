package eu.tutorials.domain.utils

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

fun compressPhoto(
    originalBytes: ByteArray,
    maxSize: Int = 512,
    quality: Float = 0.7f
): ByteArray {

    val inputStream = ByteArrayInputStream(originalBytes)
    val originalImage = ImageIO.read(inputStream)
        ?: throw IllegalArgumentException("Unsupported image format")

    // Масштабування
    val ratio = maxSize.toDouble() / maxOf(originalImage.width, originalImage.height)
    val newWidth = (originalImage.width * ratio).toInt()
    val newHeight = (originalImage.height * ratio).toInt()

    val resized = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
    val graphics = resized.createGraphics()
    graphics.drawImage(
        originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH),
        0,
        0,
        null
    )
    graphics.dispose()

    // Компресія у JPEG
    val baos = ByteArrayOutputStream()
    val writer = ImageIO.getImageWritersByFormatName("jpg").next()
    val ios = ImageIO.createImageOutputStream(baos)
    writer.output = ios

    val params = writer.defaultWriteParam
    params.compressionMode = ImageWriteParam.MODE_EXPLICIT
    params.compressionQuality = quality

    writer.write(null, IIOImage(resized, null, null), params)

    ios.close()
    writer.dispose()

    return baos.toByteArray()
}
