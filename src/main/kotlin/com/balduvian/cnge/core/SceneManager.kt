package com.balduvian.cnge.core

import com.balduvian.cnge.graphics.Bad
import com.balduvian.cnge.graphics.Good
import com.balduvian.cnge.graphics.Timing
import com.balduvian.cnge.graphics.Window

abstract class SceneManager(var nextSceneId: Int) {
	var currentScene: Scene? = null

	var previousResources: Array<Resource<*>> = emptyArray()

	abstract fun getResources(sceneId: Int): Array<Resource<*>>
	abstract fun createScene(window: Window, sceneId: Int): Scene

	var loading = true
	val resourceLoader = ResourceLoader()

	init {
		resourceLoader.start(previousResources, getResources(nextSceneId))
	}

	fun update(window: Window, timing: Timing) {
		window.poll()

		if (loading) {
			resourceLoader.update()

			when (val option = resourceLoader.result) {
				is Good -> if (option.value) {
					loading = false
					previousResources = resourceLoader.loads
					currentScene = createScene(window, nextSceneId)
				}
				is Bad -> throw Exception(option.value)
			}
		}

		val scene = currentScene
		if (scene != null) {
			if (window.input.didResize) {
				scene.onResize(window.width, window.height)
			}

			scene.update(window.input, timing)

			scene.render()

			val switchScene = scene.switchScene()
			if (switchScene != null) {
				resourceLoader.start(previousResources, getResources(switchScene))
				nextSceneId = switchScene
			}
		}

		window.postFrame()

		window.swap()
	}
}
