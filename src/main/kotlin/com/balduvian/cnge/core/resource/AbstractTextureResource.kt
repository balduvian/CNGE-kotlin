package com.balduvian.cnge.core.resource

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.graphics.*
import org.lwjgl.BufferUtils
import java.awt.image.BufferedImage
import java.lang.Exception
import java.nio.ByteBuffer
import javax.imageio.ImageIO

abstract class AbstractTextureResource<T: Texture>(
	val filepath: String,
	val textureParams: TextureParams
) : Resource<T>() {
	var texture: T? = null

	var pixels: ByteBuffer = BufferUtils.createByteBuffer(0)
	var width: Int = 0
	var height: Int = 0

	override fun internalAsyncLoad(): String? {
		val imageStream = Window::class.java.getResource(filepath)
			?.openStream() ?: return "Texture resource $filepath could not be found"

		return try {
			val image = ImageIO.read(imageStream)
			width = image.width
			height = image.height
			pixels = bytesFromImage(image)

			null

		} catch (ex: Exception) {
			ex.message
		}
	}

	override fun internalSyncLoad(): Option<T> {
		return textureOption()
	}

	override fun cleanup() {
		pixels = BufferUtils.createByteBuffer(0)
		width = 0
		height = 0
	}

	abstract fun textureOption() : Option<T>

	companion object {
		fun bytesFromImage(image: BufferedImage): ByteBuffer {
			val pixels = image.getRGB(0, 0, image.width, image.height, null, 0, image.width)

			val size = image.width * image.height

			val ret = BufferUtils.createByteBuffer(size * 4)

			for (i in 0 until size) {
				ret.put(pixels[i].ushr(16).toByte()) /* R */
				ret.put(pixels[i].ushr( 8).toByte()) /* G */
				ret.put(pixels[i]         .toByte()) /* B */
				ret.put(pixels[i].ushr(24).toByte()) /* A */
			}

			ret.flip()

			return ret
		}
	}
}
