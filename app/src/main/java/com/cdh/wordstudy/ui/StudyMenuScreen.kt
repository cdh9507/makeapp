package com.cdh.wordstudy.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cdh.wordstudy.data.ProgressStore
import com.cdh.wordstudy.quiz.QuizType

@Composable
fun StudyMenuScreen(
    screen: Screen.StudyMenu,
    progress: ProgressStore,
    navigate: (Screen) -> Unit,
    back: () -> Unit,
) {
    val words = screen.words
    val unknown = words.filter { it.word !in progress.known }

    Scaffold(topBar = { BackTopBar(screen.title, back) }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "${words.size}개 단어 · 외움 ${words.size - unknown.size}개",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            MenuCard("📖 단어 목록", "뜻 · 자주 쓰는 표현 · 원어민 예문 보기") {
                navigate(Screen.WordList(screen.title, words))
            }
            MenuCard("🃏 플래시카드", "카드를 뒤집으며 뜻과 예문 익히기") {
                navigate(Screen.Flashcards(screen.title, words))
            }
            if (unknown.isNotEmpty() && unknown.size < words.size) {
                MenuCard("🔁 안 외운 단어만 플래시카드", "${unknown.size}개 단어 다시 보기") {
                    navigate(Screen.Flashcards("${screen.title} · 복습", unknown))
                }
            }
            MenuCard("✏️ 퀴즈: ${QuizType.MEANING.label}", "영어 단어를 보고 뜻 고르기") {
                navigate(Screen.Quiz(screen.title, words, QuizType.MEANING))
            }
            MenuCard("✏️ 퀴즈: ${QuizType.WORD.label}", "뜻을 보고 영어 단어 고르기") {
                navigate(Screen.Quiz(screen.title, words, QuizType.WORD))
            }
            MenuCard("💬 퀴즈: ${QuizType.SENTENCE.label}", "원어민 문장 속 빈칸에 알맞은 단어 고르기") {
                navigate(Screen.Quiz(screen.title, words, QuizType.SENTENCE))
            }
        }
    }
}

@Composable
private fun MenuCard(title: String, description: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
