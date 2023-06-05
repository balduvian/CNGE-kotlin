package game

import com.balduvian.cnge.core.util.Color
import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.core.util.Vector
import com.balduvian.cnge.graphics.Camera
import game.tile.Apple
import game.tile.Exit
import game.tile.Portal
import java.util.*
import kotlin.collections.ArrayList

data class Spot(
	val x: Int,
	val y: Int,
	var previous: Spot?,
	var cost: Int,
) {
	private fun renderDirection(camera: Camera, x: Float, y: Float, tileSize: Float, destination: Spot, color0: Color, color1: Color) {
		val origin = Vector(this.x + 0.5f, this.y + 0.5f)
		val toVector = Vector(destination.x.toFloat(), destination.y.toFloat()).setSub(this.x.toFloat(), this.y.toFloat())

		GameResources.gradientShader.get().enable(camera.projectionView, Camera.transform(
			x + origin.x * tileSize,
			y + origin.y * tileSize,
			toVector.length() * tileSize,
			0.1f * tileSize,
			toVector.angle()
		))
			.uniformColor(0, color0)
			.uniformColor(1, color1)
		GameResources.snakeRect.get().render()
	}

	fun render(camera: Camera, x: Float, y: Float, tileSize: Float) {
		//val next = next
		//if (next != null) {
		//	renderDirection(camera, x, y, tileSize, next, Colors.snakeStart, Colors.snakeEnd)
		//}

		val previous = previous
		if (previous != null) {
			renderDirection(camera, x, y, tileSize, previous, Colors.entranceBorder, Colors.entrance)
		}
	}
}

/**
 * entrance just refers to the entrance of the level
 * "start" used anywhere refers to where this specific function is starting from
 * same applies to exit and "end"
 */
class SpotGraph(
	val entrance: Spot,
	val exit: Spot,
	val grid: Array<Spot>,
	val width: Int,
	val height: Int,
) {
	fun debugRender(camera: Camera, x: Float, y: Float, tileSize: Float) {
		for (j in 0 until height) {
			for (i in 0 until width) {
				val spot = grid[j * width + i]
				spot.render(camera, x, y, tileSize)
			}
		}

		entrance.render(camera, x, y, tileSize)
		exit.render(camera, x, y, tileSize)
	}

	fun getSpot(x: Int, y: Int): Spot {
		if (x == entrance.x && y == entrance.y) return entrance
		if (x == exit.x && y == exit.y) return exit
		return grid[y * width + x]
	}

	fun getSpot(xy: Pair<Int, Int>): Spot {
		return getSpot(xy.first, xy.second)
	}

	fun createPath(level: Level, start: Pair<Int, Int>, end: Pair<Int, Int>): ArrayList<Move>? {
		val path = ArrayList<Move>()

		var backIterator = getSpot(end)
		while (true) {
			val (endX, endY) = backIterator
			val (startX, startY) = backIterator.previous ?: break

			val direction = GameUtil.Direction.fromOffset(endX - startX, endY - startY)

			/* warps do not have direction */
			if (direction != null) path.add(Move(
				startX,
				startY,
				direction,
				level.tileAt(endX, endY).travelCost()
			))

			backIterator = backIterator.previous!!
		}

		path.reverse()

		/* path does not exist */
		val (firstX, firstY) = path.firstOrNull() ?: return null
		/* OR path does not go from start to end */
		if (firstX != start.first || firstY != start.second) return null

		return path
	}

	companion object {
		fun locatePortal(level: Level, fromX: Int, fromY: Int): Pair<Int, Int>? {
			for (j in 0 until level.height) {
				for (i in 0 until level.width) {
					if (!(i == fromX && j == fromY) && level.tileAt(i, j) is Portal) return i to j
				}
			}
			return null
		}

		fun locateApple(level: Level): Pair<Int, Int>? {
			for (j in 0 until level.height) {
				for (i in 0 until level.width) {
					if (level.tileAt(i, j) is Apple) return i to j
				}
			}
			return null
		}

		fun appleCount(level: Level): Int {
			var count = 0

			for (j in 0 until level.height) {
				for (i in 0 until level.width) {
					if (level.tileAt(i, j) is Apple) ++count
				}
			}
			return count
		}

		fun hasMove(moves: ArrayList<Move>?, x: Int, y: Int): Boolean {
			if (moves == null) return false
			/* ignore the start of the first move (where the apple is) */
			for (i in 1 until moves.size) {
				val move = moves[i]
				if (move.fromX == x && move.fromY == y) return true
			}
			return false
		}

		fun baseFromLevel(level: Level): SpotGraph {
			return SpotGraph(
				Spot(level.entrancePos.first, level.entrancePos.second, null, Int.MAX_VALUE),
				Spot(level.exitPos.first, level.exitPos.second, null, Int.MAX_VALUE),
				Array(level.width * level.height) { i -> Spot(i % level.width, i / level.width, null, Int.MAX_VALUE) },
				level.width,
				level.height
			)
		}

		fun pathCost(path: ArrayList<Move>): Int {
			return path.fold(0) { cost, move -> cost + move.cost }
		}

		data class Tree(val cost: Int, val x: Int, val y: Int, var ancestor: Tree?) {
			fun inPath(sx: Int, sy: Int): Boolean {
				var current = this.ancestor ?: return false
				while (true) {
					if (current.x == sx && current.y == sy) return true
					current = current.ancestor ?: return false
				}
			}

			companion object {
				const val APPLE_COST = 1000000
			}
		}

		fun exhaustiveFullPath(level: Level): ArrayList<Move>? {
			val appleCount = appleCount(level)

			val root = Tree(Tree.APPLE_COST * appleCount, level.entrancePos.first, level.entrancePos.second, null)
			val searchQueue = PriorityQueue<Tree> { tree0, tree1 -> tree0.cost - tree1.cost }
			searchQueue.add(root)

			var finalBranch: Tree? = null
			while (finalBranch == null && searchQueue.isNotEmpty()) {
				val current = searchQueue.remove()
				val tile = level.tileAt(current.x, current.y)

				for (direction in GameUtil.Direction.values()) {
					val sx = current.x + direction.x
					val sy = current.y + direction.y
					val searchTile = level.tileAt(sx, sy)

					if (tile.passableTo(direction) && searchTile.passableFrom(direction) && !current.inPath(sx, sy)) {
						val next = Tree(
							current.cost - (if (searchTile is Apple) Tree.APPLE_COST else 0) + searchTile.travelCost(),
							sx,
							sy,
							current
						)

						val portalExit = if (searchTile is Portal) {
							val portalLoc = locatePortal(level, sx, sy)
							if (portalLoc != null) {
								Tree(current.cost + searchTile.travelCost(), portalLoc.first, portalLoc.second, next)
							} else {
								null
							}
						} else {
							null
						}

						if (searchTile is Exit) {
							/* it must have the apple to count */
							if (next.cost < Tree.APPLE_COST) {
								finalBranch = next
								break
							}
						} else if (portalExit != null) {
							searchQueue.add(portalExit)
						} else {
							searchQueue.add(next)
						}
					}
				}
			}

			/* did any branch reach the end? if it did we have a path */
			var backIterator = finalBranch ?: return null

			val path = ArrayList<Move>()

			while (true) {
				val (_, endX, endY) = backIterator
				val (_, startX, startY) = backIterator.ancestor ?: break

				val direction = GameUtil.Direction.fromOffset(endX - startX, endY - startY)

				/* warps do not have direction */
				if (direction != null) path.add(Move(
					startX,
					startY,
					direction,
					level.tileAt(endX, endY).travelCost()
				))

				backIterator = backIterator.ancestor!!
			}

			path.reverse()

			return path
		}

		fun subPathfind(level: Level, start: Pair<Int, Int>, previous: ArrayList<Move>?): SpotGraph {
			val internalGraph = baseFromLevel(level)
			val heads = ArrayList<Spot>()

			val startingSpot = internalGraph.getSpot(start)
			startingSpot.cost = 0
			heads.add(startingSpot)

			while (heads.isNotEmpty()) {
				val toAddToHeads = ArrayList<Spot>()
				heads.removeIf { spot ->
					val x = spot.x
					val y = spot.y

					val fromTile = level.tileAt(x, y)

					for (tryDirection in GameUtil.Direction.values()) {
						val tryX = x + tryDirection.x
						val tryY = y + tryDirection.y

						val toTile = level.tileAt(tryX, tryY)

						if (fromTile.passableTo(tryDirection) && toTile.passableFrom(tryDirection)) {
							val nextSpot = internalGraph.getSpot(tryX, tryY)

							if (
								!hasMove(previous, tryX, tryY) && /* next is not already taken by the previous path */
								spot.cost + toTile.travelCost() < nextSpot.cost /* next is the most efficient connection */
							) {
								nextSpot.cost = spot.cost + toTile.travelCost()
								nextSpot.previous = spot

								/* teleportation! */
								val portalExit = if (toTile is Portal) {
									val portalLoc = locatePortal(level, tryX, tryY)
									if (portalLoc != null) {
										val portalSpot = internalGraph.getSpot(portalLoc.first, portalLoc.second)
										portalSpot.cost = nextSpot.cost
										portalSpot.previous = nextSpot
										portalSpot
									} else {
										null
									}
								} else {
									null
								}

								toAddToHeads.add(portalExit ?: nextSpot)
							}
						}
					}

					true
				}
				heads.addAll(toAddToHeads)
			}

			return internalGraph
		}

		fun routeOutcome(
			level: Level,
			start0: Pair<Int, Int>,
			end0: Pair<Int, Int>,
			start1: Pair<Int, Int>,
			end1: Pair<Int, Int>,
		): ArrayList<Move>? {
			/* both legs of the journey must be completable */
			val graph0 = subPathfind(level, start0, null)
			val path0 = graph0.createPath(level, start0, end0) ?: return null

			val graph1 = subPathfind(level, start1, path0)
			val path1 = graph1.createPath(level, start1, end1) ?: return null

			val (firstPath, secondPath) = if (start0 == level.entrancePos) {
				path0 to path1
			} else {
				path1 to path0
			}

			/* concatenate the paths */
			firstPath.addAll(secondPath)
			return firstPath
		}

		fun superPathfind(level: Level): ArrayList<Move>? {
			val applePos = locateApple(level)

			/* single path finding */
			return if (applePos == null) {
				val graph = subPathfind(level, level.entrancePos, null)
				graph.createPath(level, level.entrancePos, level.exitPos)

			/* multi path finding */
			} else {
				/* first try in order */
				val firstRoute = routeOutcome(level, level.entrancePos, applePos, applePos, level.exitPos)
				val secondRoute = routeOutcome(level, applePos, level.exitPos, level.entrancePos, applePos)

				/* the path that works and is the best (both may be null) */
				when {
					firstRoute == null -> secondRoute
					secondRoute == null -> firstRoute
					pathCost(firstRoute) < pathCost(secondRoute) -> firstRoute
					else -> secondRoute
				}
			}
		}

		//fun pathfind(level: Level): SpotGraph {
		//	val spotGrid = Array(level.width * level.height) { i -> Spot(i % level.width, i / level.width, null, Int.MAX_VALUE) }
		//	val startingSpot = Spot(level.entrancePos.first, level.entrancePos.second, null, Int.MAX_VALUE)
		//	val endingSpot = Spot(level.exitPos.first, level.exitPos.second, null, Int.MAX_VALUE)
		//	val heads = ArrayList<Spot>()
//
		//	val spotGraph = SpotGraph(startingSpot, endingSpot, spotGrid, level.width, level.height)
//
		//	startingSpot.cost = 0
		//	heads.add(startingSpot)
//
		//	while (heads.isNotEmpty()) {
		//		val toAddToHeads = ArrayList<Spot>()
		//		heads.removeIf { spot ->
		//			val x = spot.x
		//			val y = spot.y
//
		//			val fromTile = level.tileAt(x, y)
//
		//			for (tryDirection in GameUtil.Direction.values()) {
		//				val toTile = level.tileAt(x + tryDirection.x, y + tryDirection.y)
//
		//				if (fromTile.passableTo(tryDirection) && toTile.passableFrom(tryDirection)) {
		//					val nextSpot = spotGraph.getSpot(x + tryDirection.x, y + tryDirection.y)
//
		//					if (spot.cost + toTile.travelCost() < nextSpot.cost) {
		//						nextSpot.cost = spot.cost + toTile.travelCost()
		//						nextSpot.previous = spot
//
		//						/* teleportation! */
		//						val portalExit = if (toTile is Portal) {
		//							val portalLoc = locatePortal(level, x + tryDirection.x, y + tryDirection.y)
		//							if (portalLoc != null) {
		//								val portalSpot = spotGraph.getSpot(portalLoc.first, portalLoc.second)
		//								portalSpot.cost = nextSpot.cost
		//								portalSpot.previous = nextSpot
		//								portalSpot
		//							} else {
		//								null
		//							}
		//						} else {
		//							null
		//						}
//
		//						toAddToHeads.add(portalExit ?: nextSpot)
		//					}
		//				}
		//			}
//
		//			true
		//		}
		//		heads.addAll(toAddToHeads)
		//	}
//
		//	return spotGraph
		//}
	}
}
