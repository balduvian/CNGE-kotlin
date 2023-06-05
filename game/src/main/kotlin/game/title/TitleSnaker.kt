package game.title

import game.GameUtil
import game.Move
import java.awt.image.BufferedImage

object TitleSnaker {
	fun fromImage(image: BufferedImage):  ArrayList<ArrayList<Move>> {
		val width = image.width
		val height = image.height

		data class Node(val x: Int, val y: Int, var visited: Boolean)

		val nodes = Array(width * height) { i -> Node(i % width, i / width, false) }

		val starts = ArrayList<Node>()

		fun getPixel(x: Int, y: Int): Int {
			return image.getRGB(x, height - y - 1).and(0x00ffffff)
		}

		fun getNode(x: Int, y: Int): Node {
			return nodes[y * width + x]
		}

		fun findAround(node: Node, startingDir: Int, matchColor: Int, matchFound: Boolean): Pair<Node?, Int> {
			for (i in startingDir until GameUtil.Direction.values().size) {
				val dir = GameUtil.Direction.values()[i]

				val sx = node.x + dir.x
				val sy = node.y + dir.y
				val searchNode = getNode(sx, sy)

				if (getPixel(sx, sy) == matchColor && searchNode.visited == matchFound) {
					searchNode.visited = !matchFound
					return getNode(sx, sy) to dir.ordinal
				}
			}

			return null to GameUtil.Direction.values().lastIndex
		}

		/**
		 * @return the last node found
		 */
		fun explore(origin: Node, matchColor: Int): Node {
			var current = origin
			while (true) {
				val (next) = findAround(current, 0, matchColor, false)
				if (next == null) return current else current = next
			}
		}

		for (j in 1 until height) {
			for (i in 1 until width) {
				val color = getPixel(i, j)
				val currentNode = nodes[j * width + i]

				if (color != 0xffffff && !currentNode.visited) {
					currentNode.visited = true
					val (found0, dir) = findAround(currentNode, 0, color, false)
					val (found1) = findAround(currentNode, dir + 1, color, false)

					val cap0 = if (found0 != null) explore(found0, color) else null
					val cap1 = if (found1 != null) explore(found1, color) else null

					starts.add(cap0 ?: cap1 ?: currentNode)
				}
			}
		}

		return starts.map { start ->
			val path = ArrayList<Move>()

			val matchColor = getPixel(start.x, start.y)

			var current = start
			current.visited = false
			while (true) {
				val (next) = findAround(current, 0, matchColor, true)

				if (next == null) {
					break
				} else {
					val direction = GameUtil.Direction.fromOffset(next.x - current.x, next.y - current.y)
					if (direction != null) path.add(Move(
						current.x,
						current.y,
						direction,
						0,
					))
					current = next
				}
			}

			path
		} as ArrayList<ArrayList<Move>>
	}
}
