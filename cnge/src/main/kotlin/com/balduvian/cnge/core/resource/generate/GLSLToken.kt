package com.balduvian.cnge.core.resource.generate

val semicolon = createSingleMatcher(';')
val squareBracketLeft = createSingleMatcher('[')
val squareBracketRight = createSingleMatcher(']')

fun number(state: Int, char: Char): Result {
	return when (state) {
		0 -> if (char == '.') Result.next(1) else if (char in '0'..'9') Result.next(3) else Result.invalid()
		/* starts with dot */
		1 -> if (char in '0'..'9') Result.next(2) else Result.invalid()
		2 -> if (char in '0'..'9') Result.next(2) else Result.complete(false)
		/* starts with digit */
		3 -> if (char == '.') Result.next(4) else if (char in '0'..'9') Result.next(3) else Result.complete(false)
		/* 4 */ else -> if (char in '0'..'9') Result.next(4) else Result.complete(false)
	}
}

fun singleLineComment(state: Int, char: Char): Result {
	return when (state) {
		0 -> if (char == '/') Result.next(1) else Result.invalid()
		1 -> if (char == '/') Result.next(2) else Result.invalid()
		/* 2 */ else -> if (char == '\n') Result.complete(false) else Result.next(2)
	}
}

fun multiLineComment(state: Int, char: Char): Result {
	return when (state) {
		0 -> if (char == '/') Result.next(1) else Result.invalid()
		1 -> if (char == '*') Result.next(2) else Result.invalid()
		2 -> if (char == '*') Result.next(3) else Result.next(2)
		/* 3 */ else -> if (char == '/') Result.complete(true) else Result.next(2)
	}
}

fun identifier(state: Int, char: Char): Result {
	return when (state) {
		0 -> if (char in 'a'..'z' || char in 'A'..'Z' || char == '_') Result.next(1) else Result.invalid()
		/* 1 */ else -> if (char in '0'..'9' || char in 'a'..'z' || char in 'A'..'Z' || char == '_') Result.next(1) else Result.complete(false)
	}
}

fun whitespace(state: Int, char: Char): Result {
	return when (state) {
		0 -> if (char <= ' ') Result.next(1) else Result.invalid()
		/* 1 */ else -> if (char <= ' ') Result.next(1) else Result.complete(false)
	}
}

enum class GLSLTokenType {
	SEMICOLON,
	NUMBER,
	IDENTIFIER,
	SQUARE_BRACKET_LEFT,
	SQUARE_BRACKET_RIGHT,
	UNKNOWN,
}

val tokenMapping = hashMapOf(
	semicolon to GLSLTokenType.SEMICOLON,
	::number to GLSLTokenType.NUMBER,
	::singleLineComment to null,
	::multiLineComment to null,
	::identifier to GLSLTokenType.IDENTIFIER,
	squareBracketLeft to GLSLTokenType.SQUARE_BRACKET_LEFT,
	squareBracketRight to GLSLTokenType.SQUARE_BRACKET_RIGHT,
	::whitespace to null,
)

/* TEST */
fun main() {
	val testText = """
		layout (location = 0) in vec2 vertex;

		uniform mat4 pvm;
		
		out vec2 pos;
		
		void main() {
		    gl_Position = pvm * vec4(vertex, 0, 1);
		    pos = vertex;
		}
	""".trimIndent()

	val tokens = tokenize(testText, GLSLTokenType.UNKNOWN, tokenMapping)

	println(tokens.joinToString("\n") { "${it.type.name}(\"${it.string.replace("\n", "\\n")}\")" })
}