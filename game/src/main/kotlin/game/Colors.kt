package game

import com.balduvian.cnge.core.util.Color
import com.balduvian.cnge.core.util.Util

object Colors {
	val background = Color.hex(0xffffff)
	val blank = Color.hex(0xffffff)
	val grid = Color.hex(0x828282)
	val wallBorder = Color.hex(0x000000)
	val uiDark = Color.hex(0x1C1C1C)
	val placedWall = Color.hex(0x32307F)

	val entrance = Color.hex(0xEF226C)
	val entranceBorder = Color.hex(0x68002A)

	val exit = Color.hex(0x00A651)
	val exitBorder = Color.hex(0x003D1C)

	val snakeStart = Color.hex(0x21F910)
	val snakeEnd = Color.hex(0x21F9E4)

	val button = Color.hex(0x1bb343)
	val buttonHighlight = Color.hex(0x3df56e)
	val buttonText = Color.hex(0x000000)
	val buttonHighlightText = Color.hex(0xd4ffe2)

	val sticky = Color.hex(0x87e07b)

	val portalEdge = Color.hex(0xd4560d)
	val portalVoid = Color.hex(0x000000)

	val appleStem = Color.hex(0x0AFF4B)
	val appleBody = Color.hex(0xFF1533)

	val checker0 = Color.hex(0x1c1a1a)
	val checker1 = Color.hex(0xf2e9e9)

	fun getSnakeColor(along: Float): Color {
		return Color(
			Util.interp(snakeStart.r, snakeEnd.r, along),
			Util.interp(snakeStart.g, snakeEnd.g, along),
			Util.interp(snakeStart.b, snakeEnd.b, along),
		)
	}
}
