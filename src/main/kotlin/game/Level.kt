package game

import com.balduvian.cnge.core.util.Color
import com.balduvian.cnge.core.util.Color.Companion.uniformColor
import com.balduvian.cnge.graphics.Camera

class Level {
	val width = 11
	val height = 8

	val blockColor = Color.hex(0x37d280)

	val blocks = arrayOf(
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1,
		0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1,
		0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1,
		0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	)

	fun blockAt(x: Int, y: Int): Int {
		if (x < 0 || x >= width || y < 0 || y >= height) return 0
		return blocks[y * width + x]
	}

	fun render(camera: Camera) {
		for (j in 0 until height) {
			for (i in 0 until width) {
				val block = blocks[j * width + i]

				if (block == 1) {
					GameResources.colorShader.get().enable(camera.projectionView, Camera.transform(i.toFloat(), j.toFloat(), 1.0f, 1.0f))
					GameResources.colorShader.get().uniformColor(0, blockColor)
					GameResources.rect.get().render()
				}
			}
		}
	}
}