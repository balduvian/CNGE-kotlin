package game.tile

import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.graphics.Camera
import game.Colors
import game.GameResources
import game.GameUtil
import game.Tile

class Blank(val canBuildOn: Boolean): Tile() {
	override fun passableFrom(direction: GameUtil.Direction): Boolean {
		return true
	}

	override fun passableTo(direction: GameUtil.Direction): Boolean {
		return true
	}

	override fun render(camera: Camera, x: Float, y: Float, width: Float, height: Float, ghost: Boolean) {
		renderBlank(camera, x, y, width, height)

		if (!canBuildOn) {
			GameResources.gridX.get().render()
		}
	}

	override fun clone(): Tile {
		return Blank(canBuildOn)
	}

	companion object {
		fun renderBlank(camera: Camera, x: Float, y: Float, width: Float, height: Float) {
			GameResources.gridShader.get().enable(camera.projectionView, Camera.transform(x, y, width, height))
				.uniformColor(0, Colors.grid)
				.uniformColor(1, Colors.blank)
			GameResources.gridPiece.get().render()
		}
	}
}
