package game

import com.balduvian.cnge.core.resource.ShaderResource
import com.balduvian.cnge.core.resource.TextureResource
import com.balduvian.cnge.core.resource.TileTextureResource
import com.balduvian.cnge.core.resource.VAOResource
import com.balduvian.cnge.graphics.TextureParams
import com.balduvian.cnge.graphics.VAO
import org.lwjgl.opengl.GL46.*

object GameResources {
	val colorShader = ShaderResource(
		"/shaders/color/vert.glsl",
		"/shaders/color/frag.glsl",
		null,
		"color",
	)
	val textureShader = ShaderResource(
		"/shaders/texture/vert.glsl",
		"/shaders/texture/frag.glsl",
		null,
		"color",
	)
	val tileShader = ShaderResource(
		"/shaders/tile/vert.glsl",
		"/shaders/tile/frag.glsl",
		null,
		"color",
		"tile",
	)
	val texture3DShader = ShaderResource(
		"/shaders/texture3D/vert.glsl",
		"/shaders/texture3D/frag.glsl",
		"/shaders/texture3D/geom.glsl",
		"light",
		"ambient",
		"lightAngle"
	)
	val vhsShader = ShaderResource(
		"/shaders/vhs/vert.glsl",
		"/shaders/vhs/frag.glsl",
		null,
		"ratio",
		"redOffset",
		"greOffset",
		"bluOffset",
		"scanLines",
		"scanOffset",
		"seed"
	)
	val eyeTexture = TextureResource("/textures/moon.png", TextureParams().filter(GL_LINEAR))
	val hatKidTexture = TextureResource("/textures/hatkid.png", TextureParams().filter(GL_NEAREST))
	val tileTexture = TileTextureResource("/textures/rin.png", TextureParams().filter(GL_LINEAR), 2, 2)
	val rect = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2, 0, 2, 3),
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
	val frameRect = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2, 0, 2, 3),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 0f,
				1f, 0f,
				1f, 1f,
				0f, 1f,
			)),
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 0f,
				1f, 0f,
				1f, 1f,
				0f, 1f,
			))
		)
	)
	val lineRect = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2, 0, 2, 3),
		arrayOf(
			VAO.StaticAttribute(2, floatArrayOf(
				0.0f, -0.5f,
				1.0f, -0.5f,
				1.0f,  0.5f,
				0.0f,  0.5f,
			)),
		)
	)
	val cube = VAOResource(
		GL_TRIANGLES,
		intArrayOf(
			1,  0,  2,  1,  2,  3,
			5,  4,  6,  5,  6,  7,
			9,  8, 10,  9, 10, 11,
			13, 12, 14, 13, 14, 15,
			17, 16, 18, 17, 18, 19,
			21, 20, 22, 21, 22, 23,
		),
		arrayOf(
			VAO.StaticAttribute(3, floatArrayOf(
				0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f, 1f, 1f, 0f,
				1f, 0f, 0f, 1f, 0f, 1f, 1f, 1f, 0f, 1f, 1f, 1f,
				1f, 0f, 1f, 0f, 0f, 1f, 1f, 1f, 1f, 0f, 1f, 1f,
				0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 0f, 1f, 0f,
				1f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, 0f, 0f, 0f, 0f,
				0f, 1f, 0f, 1f, 1f, 0f, 0f, 1f, 1f, 1f, 1f, 1f,
			)),
			VAO.StaticAttribute(2, floatArrayOf(
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
				0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
			))
		)
	)
	val dynTri = VAOResource(
		GL_TRIANGLES,
		intArrayOf(0, 1, 2),
		arrayOf(VAO.DynamicAttribute(2, 3))
	)
}
