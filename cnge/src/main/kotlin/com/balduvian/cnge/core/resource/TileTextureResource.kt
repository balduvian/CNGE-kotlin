package com.balduvian.cnge.core.resource

import com.balduvian.cnge.graphics.Texture
import com.balduvian.cnge.graphics.TextureParams
import com.balduvian.cnge.graphics.TileTexture
import java.nio.ByteBuffer

class TileTextureResource(
	filepath: String,
	textureParams: TextureParams,
	val tilesWide: Int,
	val tilesTall: Int,
) : AbstractTextureResource<TileTexture>(filepath, textureParams) {
	override fun textureOption(pixels: ByteBuffer): TileTexture {
		return TileTexture.create(
			Texture.create(
			width,
			height,
			pixels,
			textureParams
		), tilesWide, tilesTall)
	}
}
