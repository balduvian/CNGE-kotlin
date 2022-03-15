package com.balduvian.cnge.graphics

class TileTexture(texture: Int, val tilesWide: Int, val tilesTall: Int) : Texture(texture) {
	companion object {
		fun create(texture: Option<Texture>, tilesWide: Int, tilesTall: Int): Option<TileTexture> {
			return when (texture) {
				is Good -> Good(TileTexture(texture.value.texture, tilesWide, tilesTall))
				is Bad -> texture.forward()
			}
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
