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

	/**
	 * @return the loading is done
	 */
	fun updateLoadOnly(resourceLoader: ResourceLoader): Boolean {
		if (!loading) {
			resourceLoader.stake(resources)
			loading = true
		}

		if (scene == null) {
			val (done, errors) = ResourceLoader.getDone(resources)
			if (errors.isNotEmpty()) {
				throw errors.first()
			}

			return done
		}

		return false
	}

	fun update(input: Input, timing: Timing, resourceLoader: ResourceLoader) {
		var doResize = input.didResize

		if (updateLoadOnly(resourceLoader)) {
			doResize = true
			scene = create()
		}

		val scene = scene
		if (scene != null) {
			if (doResize) scene.onResize(input.bounds)
			scene.update(input, timing)
		}
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

	fun syncLoad(resourceLoader: ResourceLoader) {
		resourceLoader.blocking(resources)
	}
}
