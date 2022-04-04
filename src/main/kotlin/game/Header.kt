package game

import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.graphics.Camera

class Header {
	companion object {
		const val HEADER_SIZE = 10.0f
	}

	private val font = GameFont()
	init { font.colors = arrayListOf(
		Colors.background,
		Colors.grid,
		Colors.background,
		Colors.exit,
		Colors.background,
		Colors.snakeEnd
	) }

	val goButton = Button(
		GameScene.VIEW_WIDTH - 15.0f - Inventory.WIDTH - 1.0f,
		GameScene.VIEW_HEIGHT - HEADER_SIZE + 1.0f,
		15.0f,
		8.0f,
		"go"
	)

	var showQuitButton = false
	val quitButton = Button(
		1.0f,
		GameScene.VIEW_HEIGHT - HEADER_SIZE + 1.0f,
		15.0f,
		8.0f,
		"quit"
	)

	fun twoDigitNumber(num: Int): String {
		val ones = (num % 10) + '0'.code
		val tens = ((num / 10) % 10) + '0'.code

		return "${tens.toChar()}${ones.toChar()}"
	}

	fun render(camera: Camera, centerX: Float, levelNumber: Int, moves: Int?, goal: Int, showButton: Boolean) {
		GameResources.colorShader.get().enable(camera.projectionView, Camera.transform(
			0.0f,
			GameScene.VIEW_HEIGHT - HEADER_SIZE,
			GameScene.VIEW_WIDTH,
			HEADER_SIZE,
		))
			.uniformColor(0, Colors.uiDark)
		GameResources.rect.get().render()

		val firstNumber = if (moves == null) "__" else twoDigitNumber(moves)

		/* moves colors */
		font.colors[3] = when {
			moves == null -> Colors.grid
			moves < goal -> Colors.entrance /* bad */
			else -> Colors.exit /* good */
		}

		font.renderString(
			camera,
			centerX,
			GameScene.VIEW_HEIGHT - HEADER_SIZE + 3f,
			0.5f,
			3f,
			true,
			"level ${levelNumber}", " | ", "moves: ", firstNumber, " / ", twoDigitNumber(goal)
		)

		if (showButton) goButton.render(camera)
		if (showQuitButton) quitButton.render(camera)
	}
}
