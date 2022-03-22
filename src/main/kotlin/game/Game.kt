package game

import com.balduvian.cnge.graphics.Loop
import com.balduvian.cnge.graphics.Window
import javax.imageio.ImageIO

fun main() {
	if (!Window.init(::println)) return println("GLFW failed to initialize")

	val window = Window.create(4, 6, true, true, "CNGE Demo", false, true)
		?: return println("Window failed to initialize")

	window.setIcon(
		Window::class.java.getResource("/textures/CNGE-logo.png") ?: return println("Icon missing"),
	)

	val sceneManager = GameSceneManager()

	val loop = Loop(window) { timing ->
		sceneManager.update(window, timing)
	}

	loop.loop()
}
