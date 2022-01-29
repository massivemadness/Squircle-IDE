/*
 * Copyright 2022 Squircle IDE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.feature.fonts.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.viewstate.ViewState
import com.blacksquircle.ui.domain.model.fonts.FontModel
import com.blacksquircle.ui.domain.providers.resources.StringProvider
import com.blacksquircle.ui.domain.repository.fonts.FontsRepository
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.feature.fonts.viewstate.ExternalFontViewState
import com.blacksquircle.ui.feature.fonts.viewstate.FontsViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FontsViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val fontsRepository: FontsRepository
) : ViewModel() {

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent

    private val _popBackStackEvent = MutableSharedFlow<Unit>()
    val popBackStackEvent: SharedFlow<Unit> = _popBackStackEvent

    private val _fontsState = MutableStateFlow<ViewState>(ViewState.Loading)
    val fontsState: StateFlow<ViewState> = _fontsState

    private val _externalFontState = MutableStateFlow<ViewState>(ExternalFontViewState.Invalid)
    val externalFontState: StateFlow<ViewState> = _externalFontState

    init {
        fetchFonts("")
    }

    fun fetchFonts(query: String) {
        viewModelScope.launch {
            try {
                val fonts = fontsRepository.fetchFonts(query)
                if (fonts.isNotEmpty()) {
                    _fontsState.value = FontsViewState.Data(query, fonts)
                } else {
                    _fontsState.value = FontsViewState.Empty(query)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _toastEvent.emit(stringProvider.getString(R.string.message_unknown_exception))
            }
        }
    }

    fun createFont(fontModel: FontModel) {
        viewModelScope.launch {
            try {
                fontsRepository.createFont(fontModel)
                _popBackStackEvent.emit(Unit)
                _toastEvent.emit(stringProvider.getString(
                    R.string.message_new_font_available,
                    fontModel.fontName
                ))
                fetchFonts("")
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _toastEvent.emit(stringProvider.getString(R.string.message_unknown_exception))
            }
        }
    }

    fun removeFont(fontModel: FontModel) {
        viewModelScope.launch {
            try {
                fontsRepository.removeFont(fontModel)
                _toastEvent.emit(stringProvider.getString(
                    R.string.message_font_removed,
                    fontModel.fontName
                ))
                fetchFonts("")
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _toastEvent.emit(stringProvider.getString(R.string.message_unknown_exception))
            }
        }
    }

    fun selectFont(fontModel: FontModel) {
        viewModelScope.launch {
            try {
                fontsRepository.selectFont(fontModel)
                _toastEvent.emit(stringProvider.getString(
                    R.string.message_selected,
                    fontModel.fontName
                ))
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _toastEvent.emit(stringProvider.getString(R.string.message_unknown_exception))
            }
        }
    }

    fun validateInput(fontName: String, fontPath: String) {
        val isFontNameValid = fontName.trim().isNotBlank()
        val isFontPathValid = fontPath.trim().isNotBlank() && File(fontPath)
            .run { exists() && name.endsWith(TTF, ignoreCase = true) }

        if (isFontNameValid && isFontPathValid) {
            _externalFontState.value = ExternalFontViewState.Valid
        } else {
            _externalFontState.value = ExternalFontViewState.Invalid
        }
    }

    companion object {
        private const val TAG = "FontsViewModel"
        private const val TTF = ".ttf"
    }
}