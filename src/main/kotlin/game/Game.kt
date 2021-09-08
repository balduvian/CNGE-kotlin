package game

import com.balduvian.cnge.graphics.Loop
import com.balduvian.cnge.graphics.Timing
import com.balduvian.cnge.graphics.Window

fun main() {
	if (!Window.init(::println)) return println("GLFW failed to initialize")

	val window = Window.create(4, 6, true, true, "CNGE Test", false, true)
		?: return println("Window failed to initialize")

	val sceneManager = GameSceneManager()

	object : Loop() {
		override fun getProps(): Props {
			return Props(window.shouldClose(), true, window.refreshRate())
		}

		override fun doFrame(timing: Timing) {
			sceneManager.update(window, timing)
		}
	}
}
