package game

import com.balduvian.cnge.core.Resource
import com.balduvian.cnge.core.Scene
import com.balduvian.cnge.core.SceneManager
import com.balduvian.cnge.graphics.Window

class GameSceneManager : SceneManager(SCENE_GAME) {
	companion object {
		const val SCENE_MENU = 0
		const val SCENE_GAME = 1
	}
	override fun getResources(sceneId: Int): Array<Resource<*>> {
		return arrayOf(
			GameResources.rect,
			GameResources.textureShader,
			GameResources.fontTiles,
		)
	}

	override fun createScene(window: Window, sceneId: Int): Scene {
		return GameScene(window)
	}
}
