package game

data class Move(
	val fromX: Int,
	val fromY: Int,
	val direction: GameUtil.Direction,
	val cost: Int,
) {
	fun toX(): Int {
		return fromX + direction.x
	}

	fun toY(): Int {
		return fromY + direction.y
	}
}
