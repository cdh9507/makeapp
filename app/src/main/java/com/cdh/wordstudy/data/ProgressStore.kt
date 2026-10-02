package com.cdh.wordstudy.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * 학습 진도를 기기에 저장한다. 단어 철자를 키로 쓰므로 단어 순서가 바뀌어도 기록이 유지된다.
 */
class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("progress", Context.MODE_PRIVATE)

    /** 외운 단어 */
    var known: Set<String> by mutableStateOf(read(KEY_KNOWN))
        private set

    /** 오답 노트 (틀렸거나 '몰라요'를 누른 단어) */
    var wrong: Set<String> by mutableStateOf(read(KEY_WRONG))
        private set

    /** 즐겨찾기 */
    var favorites: Set<String> by mutableStateOf(read(KEY_FAVORITES))
        private set

    fun markKnown(word: Word) {
        known = known + word.word
        wrong = wrong - word.word
        save()
    }

    fun markUnknown(word: Word) {
        known = known - word.word
        wrong = wrong + word.word
        save()
    }

    fun recordQuizAnswer(word: Word, correct: Boolean) {
        if (correct) {
            wrong = wrong - word.word
        } else {
            wrong = wrong + word.word
            known = known - word.word
        }
        save()
    }

    fun toggleFavorite(word: Word) {
        favorites = if (word.word in favorites) favorites - word.word else favorites + word.word
        save()
    }

    fun reset() {
        known = emptySet()
        wrong = emptySet()
        save()
    }

    private fun read(key: String): Set<String> = prefs.getStringSet(key, emptySet())?.toSet() ?: emptySet()

    private fun save() {
        prefs.edit()
            .putStringSet(KEY_KNOWN, known)
            .putStringSet(KEY_WRONG, wrong)
            .putStringSet(KEY_FAVORITES, favorites)
            .apply()
    }

    private companion object {
        const val KEY_KNOWN = "known"
        const val KEY_WRONG = "wrong"
        const val KEY_FAVORITES = "favorites"
    }
}
