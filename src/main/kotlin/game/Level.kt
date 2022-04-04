package game

import com.balduvian.cnge.core.util.Color
import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.core.util.Vector
import com.balduvian.cnge.graphics.Camera
import com.balduvian.cnge.graphics.GraphicsObject
import com.balduvian.cnge.graphics.Input
import game.tile.*
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2
import java.awt.image.BufferedImage
import kotlin.math.floor

class Level(
	val entrancePos: Pair<Int, Int>,
	val exitPos: Pair<Int, Int>,
	val width: Int,
	val height: Int,
	val tiles: Array<Tile>,
	val baseInventory: ArrayList<Tile>,
	var goalMoves: Int,
): GraphicsObject() {
	val outOfBounds = Hole()

	val entrance = Entrance(entrancePos, width)
	val exit = Exit(exitPos, width)

	var hovering: Pair<Tile, Int>? = null

	fun tileAt(x: Int, y: Int): Tile {
		if (x == entrancePos.first && y == entrancePos.second) return entrance
		if (x == exitPos.first && y == exitPos.second) return exit
		if (x < 0 || x >= width || y < 0 || y >= height) return outOfBounds
		return tiles[y * width + x]
	}

	fun clone(): Level {
		val duplicateTiles = Array(tiles.size) { i ->
			tiles[i].clone()
		}

		val duplicateInventory = ArrayList(Array(baseInventory.size) { i ->
			baseInventory[i].clone()
		}.toMutableList())

		return Level(
			entrancePos,
			exitPos,
			width,
			height,
			duplicateTiles,
			duplicateInventory,
			goalMoves,
		)
	}

	fun render(camera: Camera, x: Float, y: Float, tileSize: Float) {
		/* main tiles */
		for (j in 0 until height) {
			for (i in 0 until width) {
				val tile = tiles[j * width + i]
				tile.render(camera, x + i * tileSize, y + j * tileSize, tileSize, tileSize, false)
			}
		}

		/* entrance and exit */
		val (nx, ny) = entrancePos
		entrance.render(camera, x + nx * tileSize, y + ny * tileSize, tileSize, tileSize, false)

		val (ex, ey) = exitPos
		exit.render(camera, x + ex * tileSize, y + ey * tileSize, tileSize, tileSize, false)

		/* ghost */
		val hovering = hovering
		if (hovering != null) {
			val (hoverTile, hoverPlace) = hovering
			val tx = hoverPlace % width
			val ty = hoverPlace / width

			hoverTile.render(camera, x + tx * tileSize, y + ty * tileSize, tileSize, tileSize, true)
		}
	}

	fun renderOverlay(camera: Camera, x: Float, y: Float, tileSize: Float) {
		val (nx, ny) = entrancePos
		entrance.renderGradient(camera, x + nx * tileSize, y + ny * tileSize, tileSize, tileSize)

		val (ex, ey) = exitPos
		exit.renderGradient(camera, x + ex * tileSize, y + ey * tileSize, tileSize, tileSize)
	}

	fun update(inventory: Inventory, input: Input, x: Float, y: Float, tileSize: Float) {
		val heldItem = inventory.getHeld()

		val (cursorX, cursorY) = GameUtil.screenToGameCoords(input)
		val gridX = floor((cursorX - x) / tileSize).toInt()
		val gridY = floor((cursorY - y) / tileSize).toInt()
		val gridLocation = if (gridX in 0 until width && gridY in 0 until height) gridY * width + gridX else null

		hovering = if (heldItem != null && gridLocation != null) {
			heldItem to gridLocation
		} else {
			null
		}

		if (input.buttonPressed(GLFW_MOUSE_BUTTON_1)) {
			if (heldItem != null && gridLocation == null) {
				inventory.heldItem = null
			}

			val hovering = hovering
			if (hovering != null) {
				val (placeTile, place) = hovering
				val placeOn = tiles[place]

				if (placeOn is Blank && placeOn.canBuildOn) {
					tiles[place] = placeTile
					inventory.removeHeld()
					this.hovering = null
				} else {
					inventory.heldItem = null
				}
			}

			if (hovering == null && gridLocation != null) {
				val pickUp = tiles[gridLocation]

				if (
					pickUp is Wall ||
					pickUp is Sticky ||
					pickUp is Pipe ||
					pickUp is Portal ||
					pickUp is Apple ||
					pickUp is Director
				) {
					tiles[gridLocation] = Blank(true)
					inventory.items.add(pickUp)
					inventory.heldItem = inventory.items.lastIndex
					this.hovering = pickUp to gridLocation
				}
			}
		}

		if (input.buttonPressed(GLFW_MOUSE_BUTTON_2)) {
			if (gridLocation != null) {
				tiles[gridLocation].rightClick()
			}
		}
	}

	companion object {
		fun loadFromImage(image: BufferedImage): Level {
			val imageWidth = image.width
			val imageHeight = image.height

			/* determine goal moves */
			val goalMoves = image.getRGB(0, 0).and(0x00ffffff)

			/* determine the playable area */
			val boardHeight = imageHeight - 2
			var boardWidth = 0
			for (i in 1 until imageWidth) {
				/* find the white to the right of the board the separates the inventory */
				if (image.getRGB(i, 0).and(0x00ffffff) == 0xffffff) {
					boardWidth = i - 2
					break
				}
			}

			var entrace = 0 to 0
			var exit = 0 to 0
			val tiles = Array<Tile>(boardWidth * boardHeight) { Blank(true) }

			for (j in 0 until imageHeight) {
				for (i in 0 until boardWidth + 2) {
					/* normalized into board coordinates */
					val x = i - 1
					val y = imageHeight - j - 2

					val rgb = image.getRGB(i, j).and(0x00ffffff)

					/* on board */
					if (j in 1..boardHeight && i in 1..boardWidth) {
						when {
							rgb == 0x7f0000 -> tiles[y * boardWidth + x] = Hole()
							rgb == 0x007f00 -> tiles[y * boardWidth + x] = Blank(false)
							rgb == 0x00007f -> tiles[y * boardWidth + x] = Fast()
						}
					/* metadata */
					} else {
						when {
							rgb == 0xff0000 -> entrace = x to y
							rgb == 0x00ff00 -> exit = x to y
						}
					}
				}
			}

			/* inventory to the right of the board */
			val inventory = ArrayList<Tile>()
			for (j in 0 until imageHeight) {
				for (i in boardWidth + 2 until imageWidth) {
					val rgb = image.getRGB(i, j).and(0x00ffffff)

					when {
						rgb == 0x7f0000 -> inventory.add(Wall())
						rgb == 0x7f7f00 -> inventory.add(Sticky())
						rgb == 0x00007f -> inventory.add(Pipe())
						rgb == 0x007f7f -> inventory.add(Portal())
						rgb == 0x7f007f -> inventory.add(Apple())
						rgb == 0x7f7f7f -> inventory.add(Director())
					}
				}
			}

			return Level(entrace, exit, boardWidth, boardHeight, tiles, inventory, goalMoves)
		}
	}

	/**
	 * order of introduction of mechanics:
	 *
	 * holes, walls
	 * unplaceables
	 * sticky
	 * fast
	 * pipe
	 * director
	 * portal
	 * apple
	 */

	override fun destroy() {}
}
