package game

import com.balduvian.cnge.core.resource.ShaderResource
import com.balduvian.cnge.core.resource.SoundResource
import com.balduvian.cnge.core.resource.TileTextureResource
import com.balduvian.cnge.core.resource.VAOResource
import com.balduvian.cnge.graphics.TextureParams
import com.balduvian.cnge.graphics.VAO
import org.joml.Math.sin
import org.lwjgl.opengl.GL46.*
import kotlin.math.cos

object GameResources {
	val colorShader = ShaderResource(
		"/shaders/color/vert.glsl",
		"/shaders/color/frag.glsl",
		"color",
	)

	val textureShader = ShaderResource(
		"/shaders/texture/vert.glsl",
		"/shaders/texture/frag.glsl",
		"color",
	)

	val noiseTestShader = ShaderResource(
		"/shaders/noiseTest/vert.glsl",
		"/shaders/noiseTest/frag.glsl",
		"color"
	)

	val rect = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2, 2, 3, 0),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 0f,
				1f, 0f,
				1f, 1f,
				0f, 1f,
			)),
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 1f,
				1f, 1f,
				1f, 0f,
				0f, 0f,
			))
		)
	)

	val centerRect = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2, 2, 3, 0),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				-0.5f, -0.5f,
				0.5f, -0.5f,
				0.5f, 0.5f,
				-0.5f, 0.5f,
			)),
		)
	)

	/* centered in the middle left */
	val snakeRect = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2, 2, 3, 0),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				0f, -0.5f,
				1f, -0.5f,
				1f,  0.5f,
				0f,  0.5f,
			)),
		)
	)

	val gridPiece = VAOResource(
		GL_TRIANGLES,
		intArrayOf(
			0, 1, 2, 2, 3, 0,
			4, 5, 6, 6, 7, 4,
		),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 0f, /* border */
				1f, 0f,
				1f, 1f,
				0f, 1f,

				0.1f, 0.1f, /* inside */
				0.9f, 0.1f,
				0.9f, 0.9f,
				0.1f, 0.9f,
			)),
			VAO.StaticAttribute(1, floatArrayOf(
				0f,
				0f,
				0f,
				0f,
				1f,
				1f,
				1f,
				1f,
			)),
		)
	)

	val gridShader = ShaderResource(
		"/shaders/grid/vert.glsl",
		"/shaders/grid/frag.glsl",
		"borderColor", "insideColor"
	)

	val gradientShader = ShaderResource(
		"/shaders/gradient/vert.glsl",
		"/shaders/gradient/frag.glsl",
		"color0", "color1"
	)

	val inventoryShader = ShaderResource(
		"/shaders/inventory/vert.glsl",
		"/shaders/inventory/frag.glsl",
		"color0", "color1"
	)

	val gridX = VAOResource(
		GL_TRIANGLES,
		intArrayOf(
			0, 1, 7,
			1, 2, 3,
			3, 4, 5,
			5, 6, 7,
			1, 3, 5,
			5, 7, 1
		),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				0.1f, 0.1f,// 0
				0.5f, 0.4f,// 1
				0.9f, 0.1f,// 2
				0.6f, 0.5f,// 3
				0.9f, 0.9f,// 4
				0.5f, 0.6f,// 5
				0.1f, 0.9f,// 6
				0.4f, 0.5f,// 7
			)),
		)
	)

	val pipe = VAOResource(
		GL_TRIANGLES,
		intArrayOf(
			0, 1, 2, 2, 3, 0,
			4, 5, 6, 6, 7, 4,
		),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				/* bottom wall */
				-0.5f, -0.5f,
				0.5f, -0.5f,
				0.5f, -0.3f,
				-0.5f, -0.3f,

				/* top wall */
				-0.5f, 0.5f,
				0.5f, 0.5f,
				0.5f, 0.3f,
				-0.5f, 0.3f,
			)),
		)
	)

	val circle: VAOResource
	init {
		val numPoints = 45
		/* x and y for each point and a center point */
		val circlePoints = FloatArray((numPoints + 1) * 2) { i ->
			when (i) {
				0 -> 0.5f
				1 -> 0.5f
				else -> {
					val along = ((i - 2) / 2) / (numPoints - 1.0f)

					0.5f + (if (i % 2 == 0) cos(along * GameUtil.FPI * 2.0f) else sin(along * GameUtil.FPI * 2.0f)) * 0.5f
				}
			}
		}

		val circleIndices = IntArray(numPoints + 1) { it }

		circle = VAOResource(
			GL_TRIANGLE_FAN,
			circleIndices,
			arrayOf(
				VAO.StaticAttribute(2, circlePoints)
			)
		)
	}

	val apple = VAOResource(
		GL_TRIANGLES,
		intArrayOf(
			/* stem */
			0, 1, 2,

			/* body */
			3, 4, 5,
			5, 6, 7,
			3, 7, 8,
			8, 11, 3,
			8, 9, 11,
			9, 10, 11,
		),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				/* stem */
				0.3f, 0.7f,
				0.5f, 0.6f,
				0.4f, 0.8f,

				/* body */
				0.3f, 0.3f,
				0.4f, 0.2f,
				0.5f, 0.3f,
				0.6f, 0.2f,
				0.7f, 0.3f,
				0.7f, 0.5f,
				0.6f, 0.6f,
				0.4f, 0.6f,
				0.3f, 0.5f,
			)),
			VAO.StaticAttribute(1, floatArrayOf(
				/* stem */
				0f, 0f, 0f,

				/* body */
				1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
			))
		)
	)

	val rightTriangle = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				-0.5f, 0.5f,
				0.5f, -0.5f,
				-0.5f, -0.5f,
			))
		)
	)

	val goTriangle = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				-0.3f, -0.3f,
				0.3f, 0.0f,
				-0.3f, 0.3f,
			))
		)
	)

	val checkerShader = ShaderResource(
		"/shaders/checker/vert.glsl",
		"/shaders/checker/frag.glsl",
		"color0", "color1"
	)

	val fontTiles = TileTextureResource(
		"/textures/font.png",
		TextureParams().filter(GL_NEAREST).wrap(GL_CLAMP_TO_BORDER),
		16,
		8
	)

	val tileShader = ShaderResource(
		"/shaders/tile/vert.glsl",
		"/shaders/tile/frag.glsl",
		"tile", "color"
	)

	val levels = LevelResource.discoverLevels("/levels/")
	val tutorialLevel = LevelResource("/levels/tutorial.png")

	val gameTheme = SoundResource("/sounds/game-theme.wav")
	val introTheme = SoundResource("/sounds/intro-theme.wav")

	val win = SoundResource("/sounds/win.wav")
	val lose = SoundResource("/sounds/lose.wav")
	val select = SoundResource("/sounds/select.wav")
}
