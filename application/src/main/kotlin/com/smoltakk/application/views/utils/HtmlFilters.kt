package com.smoltakk.application.views.utils

abstract class HtmlFilters {
    abstract val tag: String

    private val regex: Regex
        get() = """<\s*$tag|<\/$tag\s*>""".toRegex()

    fun escapeTag(input: String): String {
        return input.replace(regex) { matchResult: MatchResult ->  replaceWithHtmlEntities(matchResult.value) }
    }

    fun containsTag(input: String): Boolean {
        return input.matches(regex)
    }

    private fun replaceWithHtmlEntities(character: String): String {
        return character
            .replace("<", "&lt;")
            .replace(">", "&gt;")
    }
}