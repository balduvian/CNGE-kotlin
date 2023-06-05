package game

import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.graphics.Camera
import com.balduvian.cnge.graphics.Input
import org.lwjgl.glfw.GLFW
import kotlin.math.floor

class Inventory(var items: ArrayList<Tile>) {
	private val font = GameFont()
	init { font.colors = arrayListOf(Colors.background) }

	companion object {
		const val WIDTH = 35.0f

		const val BUFFER = 2.5f
		const val ITEM_SIZE = (WIDTH - BUFFER * 3.0f) / 2.0f
	}

	private var highlighted: Int? = null
	var heldItem: Int? = null

	fun getHeld(): Tile? {
		return items[heldItem ?: return null]
	}

	fun removeHeld() {
		val heldItem = heldItem ?: return
		items.removeAt(heldItem)
		this.heldItem = null
	}

	/**
	 * @return if the inventory was interacted with
	 */
	fun update(input: Input): Boolean {
		val (cursorX, cursorY) = GameUtil.screenToGameCoords(input)

		/* top left corner of the items */
		val originX = GameScene.VIEW_WIDTH - WIDTH + BUFFER
		val originY = GameScene.VIEW_HEIGHT - Header.HEADER_SIZE - BUFFER - ITEM_SIZE

		val spaceX = floor((cursorX - originX) / (ITEM_SIZE + BUFFER)).toInt()
		val spaceY = -floor((cursorY - originY) / (ITEM_SIZE + BUFFER)).toInt()

		val highlighted = if (spaceX in 0..1 && spaceY >= 0) {
			/* convert to 1D index of item */
			val index = spaceY * 2 + spaceX
			if (index >= this.items.size) null else index
		} else {
			null
		}

		var result = false

		if (highlighted != null && input.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
			result = true
			heldItem = if (heldItem == highlighted) {
				null
			} else {
				highlighted
			}
		}

		this.highlighted = highlighted

		return result
	}

	fun render(camera: Camera, offsetX: Float) {
		font.renderString(
			camera,
			offsetX + GameScene.VIEW_WIDTH - WIDTH / 2.0f,
			GameScene.VIEW_HEIGHT - Header.HEADER_SIZE + 3f,
			0.5f,
			3f,
			true,
			"inventory",
		)

		/* inventory background */
		GameResources.inventoryShader.get().enable(camera.projectionView, Camera.transform(
			offsetX + GameScene.VIEW_WIDTH - WIDTH,
			0.0f,
			WIDTH,
			GameScene.VIEW_HEIGHT - Header.HEADER_SIZE
		))
			.uniformColor(0, Colors.grid, 0f)
			.uniformColor(1, Colors.grid)
		GameResources.rect.get().render()

		/* all of the items */
		for (i in items.indices) {
			val x = i % 2
			val y = i / 2

			val renderX = GameScene.VIEW_WIDTH - WIDTH + BUFFER + (ITEM_SIZE + BUFFER) * x
			val renderY = GameScene.VIEW_HEIGHT -  Header.HEADER_SIZE - BUFFER - ITEM_SIZE - (ITEM_SIZE + BUFFER) * y

			items[i].render(
				camera,
				offsetX + renderX,
				renderY,
				ITEM_SIZE,
				ITEM_SIZE,
				false
			)

			val highlightColor = when(i) {
				heldItem -> Colors.entrance
				highlighted -> Colors.background
				else -> null
			}

			if (highlightColor != null) {
				GameResources.colorShader.get().enable(camera.projectionView, Camera.transform(
					offsetX + renderX,
					renderY,
					ITEM_SIZE,
					ITEM_SIZE
				))
					.uniformColor(0, highlightColor, 0.25f)
				GameResources.rect.get().render()
			}
		}
	}
}
