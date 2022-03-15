package com.balduvian.cnge.core.resource

import com.balduvian.cnge.graphics.Option
import com.balduvian.cnge.graphics.Texture
import com.balduvian.cnge.graphics.TextureParams
import com.balduvian.cnge.graphics.TileTexture

class TextureResource(
	filepath: String,
	textureParams: TextureParams,
) : AbstractTextureResource<Texture>(filepath, textureParams) {
	override fun textureOption(): Option<Texture> {
		return Texture.create(
			width,
			height,
			pixels,
			textureParams
		)
	}
}

class TileTextureResource(
	filepath: String,
	textureParams: TextureParams,
	val tilesWide: Int,
	val tilesTall: Int,
) : AbstractTextureResource<TileTexture>(filepath, textureParams) {
	override fun textureOption(): Option<TileTexture> {
		return TileTexture.create(Texture.create(
			width,
			height,
			pixels,
			textureParams
		), tilesWide, tilesTall)
	}
}
