package com.balduvian.cnge.core

import com.balduvian.cnge.graphics.Input
import com.balduvian.cnge.graphics.Timing

class SceneHolder<T : Scene>(
	val resources: Array<Resource<*>>,
	val loadScreen: LoadScreen?,
	val create: () -> T,
) {
	var scene: T? = null
	var loading: Boolean = false

	fun update(input: Input, timing: Timing, resourceLoader: ResourceLoader) {
		if (!loading) {
			resourceLoader.stake(resources)
			loading = true
		}

		if (scene == null) {
			val (done, errors) = ResourceLoader.getDone(resources)
			if (errors.isNotEmpty()) {
				throw errors.first()
			}

			if (done) {
				scene = create()
				//scene?.onResize()
			}
		}

		scene?.update(input, timing)
	}

	fun render() {
		val scene = scene
		if (scene == null) {
			loadScreen?.render(ResourceLoader.getAlong(resources))

		} else {
			scene.render()
		}
	}

	fun unload(resourceLoader: ResourceLoader) {
		loading = false
		resourceLoader.unstake(resources)
	}
}
