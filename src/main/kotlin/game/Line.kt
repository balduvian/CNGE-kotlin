package game

import org.joml.Vector2f

data class Line(
	var sx: Float,
	var sy: Float,
	var ex: Float,
	var ey: Float,
) {
	fun vector(): Vector {
		return Vector(ex - sx, ey - sy)
	}

	fun start(): Vector {
		return Vector(sx, sy)
	}

	fun end(): Vector {
		return Vector(ex, ey)
	}

	fun setStart(vector: Vector): Line {
		sx = vector.x
		sy = vector.y
		return this
	}

	fun setEnd(vector: Vector): Line {
		ex = vector.x
		ey = vector.y
		return this
	}

	fun intersection(o: Line): Float {
		return ((sx - o.sx) * (o.sy - o.ey) - (sy - o.sy) * (o.sx - o.ex)) /
			   ((sx -   ex) * (o.sy - o.ey) - (sy -   ey) * (o.sx - o.ex))
	}

	fun pointAlong(along: Float): Vector {
		return Vector((ex - sx) * along + sx, (ey - sy) * along + sy)
	}

	companion object {
		fun from(start: Vector, end: Vector): Line {
			return Line(start.x, start.y, end.x, end.y)
		}

		val SIDE_LEFT = false
		val SIDE_RIGHT = true
	}

	fun side(px: Float, py: Float): Boolean {
		return (ex - sx) * (py - sy) - (ey - sy) * (px - sx) > 0
	}

	fun side(point: Vector2f) = side(point.x, point.y)
}