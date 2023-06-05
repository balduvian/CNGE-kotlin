package com.balduvian.cnge.core.resource.generate

typealias Pattern = (Int, Char) -> Result

class PatternData(var state: Int, var type: ResultType, var textEnd: Int) {
	fun reset() {
		state = 0
		type = ResultType.NEXT
		textEnd = 0
	}

	companion object {
		fun blank() = PatternData(0, ResultType.NEXT, 0)
	}

	fun isPrime() = type == ResultType.PRIME
	fun isEnd() = type == ResultType.COMPLETE
	fun isInvalid() = type == ResultType.INVALID

	fun invalidate() {
		type = ResultType.INVALID
	}
	fun makePrime() {
		type = ResultType.PRIME
	}
}

fun tryMatch(text: String, textIndex: Int, pattern: Pattern, patternData: PatternData) {
	if (patternData.isPrime()) return patternData.invalidate()
	if (patternData.isEnd()) return patternData.makePrime()

	val result = pattern(patternData.state, text[textIndex])

	patternData.state = result.state
	patternData.type = result.type
	if (result.inclusive) patternData.textEnd = textIndex
}

fun preprocessText(text: String): String {
	return text.replace("\r\n", "\n") + "\n\n"
}

class Token<T>(val type: T, val string: String)

fun <T> tokenize(text: String, unknownTokenType: T, mapping: HashMap<Pattern, T?>): ArrayList<Token<T>> {
	val patterns = mapping.keys.toList()
	val tokenTypes = mapping.values.toList()

	val processedText = preprocessText(text)
	val tokens = ArrayList<Token<T>>()

	val patternDatas = Array(patterns.size) { PatternData.blank() }

	var textIndex = 0
	var startIndex = 0
	while (textIndex < processedText.length) {
		var numStillValid = 0
		var matchedIndex: Int? = null

		for (p in patterns.indices) {
			val pattern = patterns[p]
			val patternData = patternDatas[p]

			if (patternData.isInvalid()) continue

			tryMatch(processedText, textIndex, pattern, patternData)

			if (!patternData.isInvalid()) {
				++numStillValid
				if (patternData.isPrime()) matchedIndex = p
			}
		}

		if (numStillValid == 0) {
			tokens.add(Token(unknownTokenType, processedText.substring(startIndex..textIndex)))

			patternDatas.forEach { it.reset() }
			++textIndex
			startIndex = textIndex
		} else if (numStillValid == 1 && matchedIndex != null) {
			tokenTypes[matchedIndex]?.let { tokenType ->
				tokens.add(Token(tokenType, processedText.substring(startIndex..patternDatas[matchedIndex].textEnd)))
			}

			textIndex = patternDatas[matchedIndex].textEnd + 1
			startIndex = textIndex
			patternDatas.forEach { it.reset() }

		} else {
			++textIndex
		}
	}

	return tokens
}

enum class ResultType {
	NEXT,
	INVALID,
	COMPLETE,
	PRIME,
}

class Result(val type: ResultType, val state: Int, val inclusive: Boolean) {
	companion object {
		fun next(state: Int) = Result(ResultType.NEXT, state, true)
		fun invalid() = Result(ResultType.INVALID, 0, false)
		fun complete(inclusive: Boolean) = Result(ResultType.COMPLETE, 0, inclusive)
	}
}

fun createSingleMatcher(single: Char) = { _: Int, char: Char ->
	if (char == single) Result.complete(true) else Result.invalid()
}

fun createWordMatcher(word: String) = { state: Int, char: Char ->
	if (char == word[state])
		if (state == word.lastIndex)
			Result.complete(true)
		else
			Result.next(state + 1)
	else
		Result.invalid()
}
