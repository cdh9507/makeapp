package com.cdh.wordstudy.ui

import com.cdh.wordstudy.data.Word
import com.cdh.wordstudy.quiz.QuizType

sealed interface Screen {
    data object Home : Screen
    data class StudyMenu(val title: String, val words: List<Word>) : Screen
    data class WordList(val title: String, val words: List<Word>) : Screen
    data class Flashcards(val title: String, val words: List<Word>) : Screen
    data class Quiz(val title: String, val words: List<Word>, val type: QuizType) : Screen
}
