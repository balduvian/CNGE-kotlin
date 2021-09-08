package game

import com.balduvian.cnge.graphics.Camera

class LineSet(val points: Array<Vector>) {
	var left: Float = Float.POSITIVE_INFINITY
	var right: Float = Float.NEGATIVE_INFINITY
	var up: Float = Float.NEGATIVE_INFINITY
	var down: Float = Float.POSITIVE_INFINITY

	init {
		points.forEach { point ->
			if (point.x < left) left = point.x
			else if (point.x > right) right = point.x
			if (point.y < down) down = point.y
			else if (point.y > up) up = point.y
		}
	}

	companion object {
		fun mod(i: Int, r: Int) = ((i % r) + r) % r
	}

	operator fun iterator(): Iterator<Line> {
		return object : Iterator<Line> {
			var index = 0

			override fun hasNext(): Boolean {
				return index < size()
			}

			override fun next(): Line {
				return getLine(index++)
			}
		}
	}

	fun inBox(point: Vector): Boolean {
		return point.x in left..right && point.y in down..up
	}

	fun size(): Int {
		return points.size
	}

	fun getLine(index: Int): Line {
		return Line.from(points[mod(index, size())], points[mod(index + 1, size())])
	}

	fun iter(onLine: (Line) -> Unit) {
		for (i in 0 until size()) onLine(getLine(i))
	}

	fun render(camera: Camera, thickness: Float) {
		iter { line ->
			val vector = line.vector()
			val angle = vector.angle()
			val length = vector.length()

			GameResources.colorShader.get().enable(camera.projectionView, Camera.transform(line.sx, line.sy, length, thickness, angle))
			GameResources.colorShader.get().uniformVector4(0, 0.6f, 0.8f, 0.0f, 1.0f)
			GameResources.lineRect.get().render()
		}
	}
}