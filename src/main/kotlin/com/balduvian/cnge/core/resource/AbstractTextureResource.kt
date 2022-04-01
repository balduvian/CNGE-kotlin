package com.balduvian.cnge.core.resource

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.graphics.*
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import javax.imageio.ImageIO

abstract class AbstractTextureResource<T: Texture>(
	val filepath: String,
	val textureParams: TextureParams
) : Resource<T>() {
	var texture: T? = null

	var pixels: ByteBuffer? = null
	var width: Int = 0
	var height: Int = 0

	override fun internalAsyncLoad() {
		val imageStream = Window::class.java.getResource(filepath)
			?.openStream() ?: throw Exception("Texture resource $filepath could not be found")

		val image = ImageIO.read(imageStream)

		width = image.width
		height = image.height
		pixels = Images.imageToByteBuffer(image)
	}

	override fun internalSyncLoad(): T {
		return textureOption(pixels!!)
	}

	override fun cleanup() {
		MemoryUtil.memFree(pixels)
		pixels = null
		width = 0
		height = 0
	}

	abstract fun textureOption(pixels: ByteBuffer) : T
}
