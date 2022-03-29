package game

import com.balduvian.cnge.core.resource.ShaderResource
import com.balduvian.cnge.core.resource.TileTextureResource
import com.balduvian.cnge.core.resource.VAOResource
import com.balduvian.cnge.graphics.TextureParams
import com.balduvian.cnge.graphics.VAO
import org.lwjgl.opengl.GL46.*

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
}
