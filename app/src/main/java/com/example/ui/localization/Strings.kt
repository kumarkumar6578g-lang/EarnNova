package com.example.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val label: String) {
  ENGLISH("EN", "English"),
  HINDI("HI", "हिन्दी")
}

class LanguageState {
  var currentLanguage by mutableStateOf(AppLanguage.ENGLISH)

  fun toggle() {
    currentLanguage = if (currentLanguage == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
  }

  fun string(en: String, hi: String): String {
    return if (currentLanguage == AppLanguage.HINDI) hi else en
  }
}

val LocalAppLanguage = compositionLocalOf { LanguageState() }

@Composable
fun loc(en: String, hi: String): String {
  return LocalAppLanguage.current.string(en, hi)
}
