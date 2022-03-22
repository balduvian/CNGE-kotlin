package com.balduvian.cnge.core.resource

import com.balduvian.cnge.graphics.Texture
import com.balduvian.cnge.graphics.TextureParams
import java.nio.ByteBuffer

class TextureResource(
	filepath: String,
	textureParams: TextureParams,
) : AbstractTextureResource<Texture>(filepath, textureParams) {
	override fun textureOption(pixels: ByteBuffer): Texture {
		return Texture.create(
			width,
			height,
			pixels,
			textureParams
		)
	}
}
