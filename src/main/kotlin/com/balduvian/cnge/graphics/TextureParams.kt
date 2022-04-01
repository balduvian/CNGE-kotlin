package com.balduvian.cnge.graphics;

import org.lwjgl.opengl.GL46.*

class TextureParams {
	var horzWrap = defaultHorzWrap
	var vertWrap = defaultVertWrap
	var minFilter = defaultMinFilter
	var magFilter = defaultMagFilter

	fun horzWrap(value: Int): TextureParams {
		horzWrap = value
		return this
	}

	fun vertWrap(value: Int): TextureParams {
		vertWrap = value
		return this
	}

	fun wrap(value: Int): TextureParams {
		horzWrap = value
		vertWrap = value
		return this
	}

	fun minFilter(value: Int): TextureParams {
		minFilter = value
		return this
	}

	fun magFilter(value: Int): TextureParams {
		magFilter = value
		return this
	}

	fun filter(value: Int): TextureParams {
		minFilter = value
		magFilter = value
		return this
	}

	companion object {
		private var defaultHorzWrap = GL_CLAMP_TO_EDGE
		private var defaultVertWrap = GL_CLAMP_TO_EDGE
		private var defaultMinFilter = GL_LINEAR
		private var defaultMagFilter = GL_NEAREST

		fun setDefaults(horzWrap: Int, vertWrap: Int, minFilter: Int, magFilter: Int) {
			defaultHorzWrap = horzWrap
			defaultVertWrap = vertWrap
			defaultMinFilter = minFilter
			defaultMagFilter = magFilter
		}
	}
}
