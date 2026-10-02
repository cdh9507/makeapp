package com.cdh.wordstudy.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.cdh.wordstudy.data.ProgressStore
import com.cdh.wordstudy.data.Word

@Composable
fun WordStudyApp(words: List<Word>, progress: ProgressStore, speak: (String) -> Unit) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val navigate: (Screen) -> Unit = { backStack.add(it) }
    val back: () -> Unit = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }

    BackHandler(enabled = backStack.size > 1, onBack = back)

    when (val screen = backStack.last()) {
        Screen.Home -> HomeScreen(words, progress, navigate)
        is Screen.StudyMenu -> StudyMenuScreen(screen, progress, navigate, back)
        is Screen.WordList -> WordListScreen(screen, progress, speak, back)
        is Screen.Flashcards -> FlashcardScreen(screen, progress, speak, back)
        is Screen.Quiz -> QuizScreen(screen, words, progress, speak, back)
    }
}
