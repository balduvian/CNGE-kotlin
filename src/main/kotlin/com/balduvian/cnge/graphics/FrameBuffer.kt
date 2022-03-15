package com.balduvian.cnge.graphics

import org.lwjgl.opengl.GL46.*

class FrameBuffer(
	val frameBuffer: Int,
	val texture: Texture,
	var width: Int,
	var height: Int
) : GraphicsObject() {
	companion object {
		fun create(width: Int, height: Int, textureParams: TextureParams): FrameBuffer {
			val frameBuffer = glGenFramebuffers()
			glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer)

			val texture = Texture.create(width, height, textureParams)

			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.texture, 0)

			return FrameBuffer(frameBuffer, texture, width, height)
		}

		fun enableDefault() {
			glBindFramebuffer(GL_FRAMEBUFFER, 0)
		}
	}

	fun enable() {
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer)
		glViewport(0, 0, width, height)
	}

	fun resize(newWidth: Int, newHeight: Int) {
		texture.resize(newWidth, newHeight)
		width = newWidth
		height = newHeight
	}

	override fun destroy() {
		glDeleteFramebuffers(frameBuffer)
		texture.destroy()
	}
}
