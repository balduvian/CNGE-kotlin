package com.balduvian.cnge.core.util

import com.balduvian.cnge.graphics.Shader
import org.lwjgl.opengl.GL46

class Color(
	val r: Float,
	val g: Float,
	val b: Float,
) {
	operator fun component1() = r
	operator fun component2() = g
	operator fun component3() = b

	companion object {
		fun hex(rgb: Int): Color {
			return Color(
				rgb.ushr(16) / 255.0f,
				rgb.ushr(8).and(0xff) / 255.0f,
				rgb.and(0xff) / 255.0f,
			)
		}

		fun channels(r: Int, g: Int, b: Int): Color {
			return Color(
				r / 255.0f,
				g / 255.0f,
				b / 255.0f,
			)
		}

		fun Shader.uniformColor(index: Int, color: Color): Shader {
			GL46.glUniform4f(locations[index], color.r, color.g, color.b, 1.0f)
			return this
		}

		fun Shader.uniformColor(index: Int, color: Color, alpha: Float): Shader {
			GL46.glUniform4f(locations[index], color.r, color.g, color.b, alpha)
			return this
		}
	}
}