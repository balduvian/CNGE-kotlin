package com.balduvian.cnge.graphics

class TileTexture(texture: Int, val tilesWide: Int, val tilesTall: Int) : Texture(texture) {
	companion object {
		fun create(texture: Texture, tilesWide: Int, tilesTall: Int): TileTexture {
			return TileTexture(texture.texture, tilesWide, tilesTall)
		}
	}

	fun tile(x: Int, y: Int = 0): Array<Float> {
		return arrayOf(
			x.toFloat() / tilesWide,
			y.toFloat() / tilesTall,
			1.0f / tilesWide,
			1.0f / tilesTall
		)
	}
}
