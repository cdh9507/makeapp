package com.cdh.wordstudy.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cdh.wordstudy.data.ProgressStore
import com.cdh.wordstudy.data.Word
import com.cdh.wordstudy.quiz.QuizGenerator
import com.cdh.wordstudy.quiz.QuizQuestion
import com.cdh.wordstudy.quiz.QuizType

@Composable
fun QuizScreen(
    screen: Screen.Quiz,
    allWords: List<Word>,
    progress: ProgressStore,
    speak: (String) -> Unit,
    back: () -> Unit,
) {
    var round by remember { mutableIntStateOf(0) }
    val questions = remember(round) { QuizGenerator.generate(screen.words, allWords, screen.type) }
    var index by remember(round) { mutableIntStateOf(0) }
    var selected by remember(round) { mutableStateOf<Int?>(null) }
    var score by remember(round) { mutableIntStateOf(0) }
    val missed = remember(round) { mutableStateListOf<Word>() }

    Scaffold(topBar = { BackTopBar("${screen.title} · ${screen.type.label}", back) }) { padding ->
        if (index >= questions.size) {
            QuizResult(
                score = score,
                total = questions.size,
                missed = missed,
                onRetry = { round++ },
                onBack = back,
                modifier = Modifier.padding(padding),
            )
            return@Scaffold
        }

        val q = questions[index]
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        ) {
            LinearProgressIndicator(progress = { index / questions.size.toFloat() }, modifier = Modifier.fillMaxWidth())
            Text(
                "${index + 1} / ${questions.size}   ·   점수 $score",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
            )
            Spacer(Modifier.height(16.dp))
            QuestionCard(q, answered = selected != null, speak = speak)
            Spacer(Modifier.height(16.dp))

            q.choices.forEachIndexed { i, choice ->
                val answered = selected != null
                val container = when {
                    !answered -> MaterialTheme.colorScheme.surfaceVariant
                    i == q.answerIndex -> CorrectColor
                    i == selected -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                val content = when {
                    answered && (i == q.answerIndex || i == selected) -> Color.White
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                Button(
                    onClick = {
                        if (selected == null) {
                            selected = i
                            val correct = i == q.answerIndex
                            if (correct) score++ else missed.add(q.word)
                            progress.recordQuizAnswer(q.word, correct)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = container,
                        contentColor = content,
                        disabledContainerColor = container,
                        disabledContentColor = content,
                    ),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(vertical = 4.dp),
                ) {
                    Text(choice, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                }
            }

            if (selected != null) {
                Spacer(Modifier.height(16.dp))
                AnswerExplanation(q, correct = selected == q.answerIndex, speak = speak)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        selected = null
                        index++
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) { Text(if (index + 1 < questions.size) "다음 문제" else "결과 보기") }
            }
        }
    }
}

@Composable
private fun QuestionCard(q: QuizQuestion, answered: Boolean, speak: (String) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when {
                q.example != null -> {
                    Text("빈칸에 알맞은 단어를 고르세요", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.height(12.dp))
                    Text(q.prompt, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                    q.hint?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "※ 문장에 맞게 형태가 바뀌어 들어갈 수 있어요 (예: -s, -ed, -ing)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                q.prompt == q.word.word -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(q.prompt, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                        SpeakButton(q.word.word, speak)
                    }
                }
                else -> {
                    Text(q.prompt, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                    if (answered) SpeakButton(q.word.word, speak)
                }
            }
        }
    }
}

@Composable
private fun AnswerExplanation(q: QuizQuestion, correct: Boolean, speak: (String) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                if (correct) "⭕ 정답!" else "❌ 오답 — 오답 노트에 저장했어요",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (correct) CorrectColor else MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${q.word.word}  ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(q.word.meaning, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                SpeakButton(q.word.word, speak)
            }
            q.example?.let { ex ->
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(highlighted(ex), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    SpeakButton(ex.plain, speak)
                }
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            UsageSection(q.word, speak)
        }
    }
}

@Composable
private fun QuizResult(
    score: Int,
    total: Int,
    missed: List<Word>,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val emoji = when {
            score == total -> "🏆"
            score * 10 >= total * 7 -> "👍"
            else -> "💪"
        }
        Text(emoji, style = MaterialTheme.typography.displayMedium)
        Text("$score / $total", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        if (missed.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text("틀린 단어", style = MaterialTheme.typography.titleMedium)
            missed.forEach { w ->
                Text("${w.word} — ${w.meaning}", style = MaterialTheme.typography.bodyLarge)
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) { Text("다시 풀기") }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("돌아가기") }
    }
}
