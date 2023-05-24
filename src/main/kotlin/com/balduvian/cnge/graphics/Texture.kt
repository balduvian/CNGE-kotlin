package com.balduvian.cnge.graphics

import org.lwjgl.opengl.GL46.*
import java.nio.ByteBuffer

open class Texture(val texture: Int) : Disposable {
	companion object {
		fun create(width: Int, height: Int, pixels: ByteBuffer, params: TextureParams): Texture {
			if (pixels.limit() != width * height * 4) throw Exception(
				"Incorrect number of pixel bytes, should be ${width * height * 4}, found ${pixels.limit()}"
			)

			val texture = glCreateTextures(GL_TEXTURE_2D)

			glBindTexture(GL_TEXTURE_2D, texture)
			applyParams(params)

			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels)

			return Texture(texture)
		}

		fun create(width: Int, height: Int, params: TextureParams): Texture {
			val texture = glCreateTextures(GL_TEXTURE_2D)

			glBindTexture(GL_TEXTURE_2D, texture)
			applyParams(params)

			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0)

			return Texture(texture)
		}

		fun applyParams(params: TextureParams) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, params.horzWrap)
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, params.vertWrap)
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, params.minFilter)
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, params.magFilter)
		}
	}

	fun bind(layer: Int = 0) {
		glActiveTexture(GL_TEXTURE0 + layer)
		glBindTexture(GL_TEXTURE_2D, texture)
	}

	fun resize(newWidth: Int, newHeight: Int) {
		glBindTexture(GL_TEXTURE_2D, texture)
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, newWidth, newHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0)
	}

	override fun destroy() {
		glDeleteTextures(texture)
	}
}