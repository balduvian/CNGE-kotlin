package com.balduvian.cnge.graphics

import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.nio.ByteBuffer

object Images {
	/**
	 * make sure you manually free the returned ByteBuffer
	 */
	fun imageToByteBuffer(bufferedImage: BufferedImage): ByteBuffer {
		val byteBuffer = MemoryUtil.memAlloc(bufferedImage.width * bufferedImage.height * 4)

		val pixels = IntArray(bufferedImage.width * bufferedImage.height)
		bufferedImage.getRGB(0, 0, bufferedImage.width, bufferedImage.height, pixels, 0, bufferedImage.width)

		for (b in 0 until bufferedImage.width * bufferedImage.height) {
			byteBuffer.put(b * 4, pixels[b].shr(16).toByte()) /* r */
			byteBuffer.put(b * 4 + 1, pixels[b].shr(8).toByte()) /* g */
			byteBuffer.put(b * 4 + 2, pixels[b].toByte()) /* b */
			byteBuffer.put(b * 4 + 3, pixels[b].shr(24).toByte()) /* a */
		}

		return byteBuffer
	}

}