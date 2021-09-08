package game

object CameraPositioner {
	data class CameraPosition(val left: Float, val right: Float, val bottom: Float, val top: Float)

	fun position(
		aspect: Float,
		buffer: Float,
		stageMinX: Float,
		stageMaxX: Float,
		stageMinY: Float,
		stageMaxY: Float,
		focalPoints: Array<Vector>
	): CameraPosition {
		var minX = Float.POSITIVE_INFINITY
		var maxX = Float.NEGATIVE_INFINITY
		var minY = Float.POSITIVE_INFINITY
		var maxY = Float.NEGATIVE_INFINITY

		focalPoints.map { point ->
			Vector(
				if (point.x < stageMinX + buffer) stageMinX + buffer
				else if (point.x > stageMaxX - buffer) stageMaxX - buffer
				else point.x,
				if (point.y < stageMinY + buffer) stageMinY + buffer
				else if (point.y > stageMaxY - buffer) stageMaxY - buffer
				else point.y,
			)
		}.forEach { point ->
			if (point.x < minX) minX = point.x
			if (point.x > maxX) maxX = point.x
			if (point.y < minY) minY = point.y
			if (point.y > maxY) maxY = point.y
		}

		minX -= buffer
		maxX += buffer
		minY -= buffer
		maxY += buffer

		val currentWidth = maxX - minX
		val currentHeight = maxY - minY
		val currentAspect = currentWidth / currentHeight

		/* too wide */
		if (currentAspect > aspect) {
			val shouldHeight = 1 / aspect * currentWidth
			val heightExtend = (shouldHeight - currentHeight) / 2
			minY -= heightExtend
			maxY += heightExtend
		/* too tall */
		} else {
			val shouldWidth = aspect * currentHeight
			val widthExtend = (shouldWidth - currentWidth) / 2
			minX -= widthExtend
			maxX += widthExtend
		}

		return CameraPosition(minX, maxX, minY, maxY)
	}
}
