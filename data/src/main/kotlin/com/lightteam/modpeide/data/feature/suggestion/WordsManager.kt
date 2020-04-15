/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.data.feature.suggestion

import android.text.Editable
import com.lightteam.modpeide.data.feature.Line
import com.lightteam.modpeide.data.utils.extensions.replaceList
import com.lightteam.language.model.SuggestionModel
import com.lightteam.language.suggestion.SuggestionProvider
import java.util.regex.Matcher
import java.util.regex.Pattern

class WordsManager {

    companion object {
        private const val WORDS_REGEX = "\\w((\\w|-)*(\\w))?"
    }

    private val wordsPattern: Pattern = Pattern.compile(WORDS_REGEX)
    private val wordsList: WordsList = WordsList()
    private val predefinedList: MutableList<SuggestionModel> = mutableListOf()

    fun setSuggestions(suggestions: SuggestionProvider) {
        predefinedList.replaceList(suggestions.getAll())
    }

    fun getSuggestions(): List<SuggestionModel> {
        return wordsList.words.map { SuggestionModel(it.value) }
    }

    fun clear() {
        wordsList.words.clear()
    }

    fun deleteLine(line: Line) {
        wordsList.deleteLine(line)
    }

    fun processSuggestions() {
        val line = Line(Int.MIN_VALUE)
        for (word in predefinedList) {
            wordsList.addWord(word.text, line)
        }
    }

    fun processLine(editableText: Editable, line: Line, startIndex: Int, endIndex: Int) {
        if (endIndex >= startIndex && editableText.length >= endIndex) {
            val textLine = editableText.subSequence(startIndex, endIndex)
            processLine(wordsPattern.matcher(textLine), textLine, line)
        }
    }

    private fun processLine(matcher: Matcher, textLine: CharSequence, line: Line) {
        wordsList.clearLine(line)
        while (matcher.find()) {
            val word = textLine.subSequence(matcher.start(), matcher.end())
            wordsList.addWord(word, line)
        }
    }
}