package com.balduvian.cnge.graphics

object Parsers {
	data class ParsedLine(
		val startIndex: Int,
		val endIndex: Int,
		val dataPart: String,
	)

	/**
	 * @return indices into input string immediately after pattern on lines that start with pattern
	 */
	fun findLinesStartingWith(input: String, pattern: String): ArrayList<ParsedLine> {
		val ret = ArrayList<ParsedLine>()

		var lineStartIndex = 0
		var lineIndex = 0
		var goodOnLine = true

		var i = 0
		while (i < input.length) {
			var current = input[i]

			if (goodOnLine && lineIndex == pattern.length) {
				val dataPart = readRestOfLine(input, i)
				ret.add(ParsedLine(lineStartIndex, i + dataPart.length, dataPart))

				i += dataPart.length
				current = input[i]
			}

			if (current == '\n') {
				lineStartIndex = i + 1
				lineIndex = 0
				goodOnLine = true

			} else if (goodOnLine && lineIndex < pattern.length) {
				if (current != pattern[lineIndex]) goodOnLine = false
				++lineIndex
			}

			++i
		}

		return ret
	}

	fun readRestOfLine(input: String, start: Int): String {
		val builder = StringBuilder()

		for (i in start until input.length) {
			val current = input[i]
			if (current == '\n') {
				return builder.toString()
			} else {
				builder.append(current)
			}
		}

		return builder.toString()
	}
}