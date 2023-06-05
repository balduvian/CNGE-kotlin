package com.balduvian.cnge.graphics

class TileTexture(texture: Int, val tilesWide: Int, val tilesTall: Int) : Texture(texture) {
	companion object {
		fun create(texture: Texture, tilesWide: Int, tilesTall: Int): TileTexture {
			return TileTexture(texture.texture, tilesWide, tilesTall)
		}

		private val values = FloatArray(4)
	}

	fun tile(x: Int, y: Int = 0): FloatArray {
		values[0] = x.toFloat() / tilesWide
		values[1] = y.toFloat() / tilesTall
		values[2] = 1.0f / tilesWide
		values[3] = 1.0f / tilesTall

		return values
	}
}
