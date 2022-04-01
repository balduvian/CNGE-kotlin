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
			if (goodOnLine && lineIndex == pattern.length) {
				val dataPart = readRestOfLine(input, i)
				ret.add(ParsedLine(lineStartIndex, i + dataPart.length, dataPart))

				i += dataPart.length
			}

			if (input[i] == '\r') {
				if (i + 1 == input.length) return ret
				if(input[i + 1] == '\n') ++i
			}

			if (input[i] == '\n') {
				lineStartIndex = i + 1
				lineIndex = 0
				goodOnLine = true

			} else if (goodOnLine && lineIndex < pattern.length) {
				if (input[i] != pattern[lineIndex]) goodOnLine = false
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

			/* ignore weird newline */
			} else if (current != '\r') {
				builder.append(current)
			}
		}

		return builder.toString()
	}

	/**
	 * @return null if no < > was found
	 */
	fun parseInAngleBrackets(input: String): String? {
		/* start looking for opening bracket */
		for (i in input.indices) {
			if (input[i] == '<') {
				val builder = StringBuilder(input.length - i)

				/* look for end bracket, copy data inside */
				for (j in i + 1 until input.length) {
					val current = input[j]

					if (current == '>') {
						return builder.toString()
					} else {
						builder.append(current)
					}
				}

				/* no closing bracket found */
				return null
			}
		}

		/* no opening bracket found */
		return null
	}
}
