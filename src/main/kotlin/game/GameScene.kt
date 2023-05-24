package game

import com.balduvian.cnge.core.Scene
import com.balduvian.cnge.core.util.Color
import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.core.util.Frame
import com.balduvian.cnge.core.util.Timer
import com.balduvian.cnge.core.util.Util
import com.balduvian.cnge.graphics.*
import game.tile.*

class GameScene(val tutorialMode: Boolean) : Scene() {
	companion object {
		const val VIEW_WIDTH = 160.0f
		const val VIEW_HEIGHT = 90.0f

		const val PREPARE_TIME = 1.0
		const val MESSAGE_TIME = 2.0

		const val WIN_MESSAGE = "snake delayed"
		const val BLOCKED_MESSAGE = "snake blocked"
		const val LOSE_MESSAGE = "snake too fast"
	}

	val camera = Camera().setOrtho(VIEW_WIDTH, VIEW_HEIGHT)

	data class TutorialStage(val moves: Int, val items: Array<Tile>, val text: String)
	var tutorialStages = arrayOf(
		TutorialStage(6, emptyArray(), "press go to release the snake"),
		TutorialStage(10, arrayOf(Wall()), "place a wall to block the snake"),
		TutorialStage(10, arrayOf(Wall()), "you cant place walls on the x tiles"),
		TutorialStage(10, arrayOf(Wall(), Wall(), Wall()), "the snake moves at 0 cost on fast tiles"),
		TutorialStage(99, arrayOf(Sticky(), Apple(), Pipe(), Portal(), Portal(), Director()), "there are many types of tiles to explore")
	)

	enum class Phase {
		BUILDING,
		SNAKING,
		LOSING,
		WINNING,
	}

	var phase = Phase.BUILDING
	val prepareTimer = Timer()

	var currentMessage: String = ""
	val messageTimer = Timer()
	private val messageFont = GameFont()
	init { messageFont.colors.add(Colors.grid) }
	private val tutorialFont = GameFont()
	init { tutorialFont.colors.add(Colors.button) }

	val header = Header()

	var levelNumber = 0
	var currentLevel: Level? = null
	var snake: Snake? = null
	var snakePath: ArrayList<Move>? = null
	var inventory: Inventory? = null

	var levelX = 0.0f
	var levelY = 0.0f
	var levelTileSize = 0.0f

	var shouldSwitchScenes = false

	init {
		GameResources.gameTheme.get().setVolume(0.5f)
		GameResources.gameTheme.get().loop()
		startlevel(if (tutorialMode) 0 else 0)
		header.showQuitButton = true
	}

	private fun startlevel(number: Int) {
		if (tutorialMode) {
			if (number == tutorialStages.size) {
				GameResources.gameTheme.get().stop()
				shouldSwitchScenes = true

			} else {
				GameResources.gameTheme.get().setVolume(0.5f)
				val level = GameResources.tutorialLevel.get().clone()

				val tutorialStage = tutorialStages[number]
				level.goalMoves = tutorialStage.moves

				if (number == 2) { /* x tiles */
					level.tiles[3 * level.width + 1] = Blank(false)
				} else if (number == 3) { /* speed strip */
					level.tiles[1 * level.width + 0] = Fast()
					level.tiles[1 * level.width + 1] = Fast()
					level.tiles[1 * level.width + 2] = Fast()
					level.tiles[1 * level.width + 3] = Fast()
					level.tiles[1 * level.width + 4] = Fast()
					level.tiles[2 * level.width + 4] = Fast()
				}

				inventory = Inventory(ArrayList(tutorialStage.items.map { it.clone() }))
				levelNumber = number
				currentLevel = level
				snake = null

				phase = Phase.BUILDING
			}

		} else {
			if (number == GameResources.levels.size) {
				GameResources.gameTheme.get().stop()
				shouldSwitchScenes = true

			} else {
				GameResources.gameTheme.get().setVolume(0.5f)
				val level = GameResources.levels[number].get().clone()

				inventory = Inventory(level.baseInventory)
				levelNumber = number
				currentLevel = level
				snake = null

				phase = Phase.BUILDING
			}
		}
	}

	private fun launch() {
		snakePath = SpotGraph.exhaustiveFullPath(currentLevel!!)

		if (snakePath == null) {
			showMessage(BLOCKED_MESSAGE, Colors.entrance)
		} else {
			phase = Phase.SNAKING
			prepareTimer.start(PREPARE_TIME)
		}
	}

	private fun showMessage(message: String, color: Color) {
		currentMessage = message
		messageTimer.start(MESSAGE_TIME)
		messageFont.colors[0] = color
	}

	override fun update(input: Input, timing: Timing) {
		camera.update()

		val level = currentLevel
		if (level != null) {
			if (phase === Phase.SNAKING && prepareTimer.update(timing.time)) {
				snake = Snake(snakePath!!)
			}

			val levelTall = VIEW_HEIGHT - 20.0f
			levelY = 5.0f
			levelTileSize = levelTall / level.height

			val leftLevelX = levelTileSize + 5.0f
			val centerLevelX = VIEW_WIDTH / 2.0f - levelTileSize * level.width / 2.0f

			levelX = when (phase) {
				Phase.BUILDING -> leftLevelX
				else -> Util.interp(leftLevelX, centerLevelX, prepareTimer.along())
			}

			val snake = snake
			if (snake != null) {
				snake.update(timing.time)

				/* snake reaches destination */
				if (snake.currentMove == null && phase === Phase.SNAKING) {
					/* win condition */
					phase = if (snake.getPathLength() >= level.goalMoves) {
						showMessage(WIN_MESSAGE, Colors.exit)
						GameResources.gameTheme.get().setVolume(0.1f)
						GameResources.win.get().setVolume(2.0f)
						GameResources.win.get().play()
						Phase.WINNING
					} else {
						showMessage(LOSE_MESSAGE, Colors.entrance)
						GameResources.lose.get().play()
						Phase.LOSING
					}
				}
			}

			if (messageTimer.update(timing.time)) {
				when (phase) {
					Phase.WINNING -> startlevel(levelNumber + 1)
					Phase.LOSING -> {
						phase = Phase.BUILDING
						this.snake = null
					}
					else -> {}
				}
			}

			/* interaction */
			if (phase === Phase.BUILDING && !messageTimer.going) {
				val inventory = inventory
				if (phase === Phase.BUILDING && inventory != null) {
					if (header.goButton.update(input)) {
						launch()
					} else {
						if (!inventory.update(input)) {
							level.update(inventory, input, levelX, levelY, levelTileSize)
						}
					}
				}
			}

			if (header.quitButton.update(input)) {
				GameResources.gameTheme.get().stop()
				shouldSwitchScenes = true
			}
		}
	}

	override fun render() {
		GameResources.colorShader.get().enable(Camera.defaultProjView, Camera.defaultModel)
			.uniformColor(0, Colors.background)
		GameResources.rect.get().render()

		val level = currentLevel
		if (level != null) {
			level.render(camera, levelX, levelY, levelTileSize)

			val snake = snake
			if (snake != null) {
				snake.render(camera, levelX, levelY, levelTileSize)
			}

			level.renderOverlay(camera, levelX, levelY, levelTileSize)

			//spotGraph?.debugRender(camera, levelX, levelY, levelTileSize)

			/* render header */
			val offsetHeader = (VIEW_WIDTH - Inventory.WIDTH) / 2.0f
			val centerHeader = VIEW_WIDTH / 2.0f
			header.render(
				camera,
				when (phase) {
					Phase.BUILDING -> offsetHeader
					else -> Util.interp(offsetHeader, centerHeader, prepareTimer.along())
				},
				levelNumber,
				when (phase) {
					Phase.BUILDING -> null
					else -> snake?.getPathLength()
				},
				level.goalMoves,
				phase === Phase.BUILDING,
			)

			inventory?.render(camera, when (phase) {
				Phase.BUILDING -> 0.0f
				else -> Util.interp(0.0f, Inventory.WIDTH, prepareTimer.along())
			})

			if (messageTimer.going) {
				messageFont.renderString(
					camera,
					VIEW_WIDTH / 2.0f,
					VIEW_HEIGHT / 2.0f - 4f,
					0.5f,
					8f,
					true,
					currentMessage
				)
			}

			if (tutorialMode) {
				tutorialFont.renderString(
					camera,
					VIEW_WIDTH / 2.0f,
					3f,
					0.5f,
					3f,
					true,
					tutorialStages[levelNumber].text
				)
			}
		}
	}

	override fun onResize(bounds: Frame.Bounds) {

	}
}
