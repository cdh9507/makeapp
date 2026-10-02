package com.cdh.wordstudy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.cdh.wordstudy.data.Example
import com.cdh.wordstudy.data.Word

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(title: String, onBack: () -> Unit, actions: @Composable () -> Unit = {}) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
            }
        },
        actions = { actions() },
    )
}

@Composable
fun SpeakButton(text: String, speak: (String) -> Unit) {
    IconButton(onClick = { speak(text) }) {
        Icon(Icons.Filled.PlayArrow, contentDescription = "발음 듣기")
    }
}

@Composable
fun FavoriteButton(isFavorite: Boolean, onToggle: () -> Unit) {
    IconButton(onClick = onToggle) {
        Icon(
            if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = "즐겨찾기",
            tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** 예문 속 학습 단어를 굵게 강조한다. */
@Composable
fun highlighted(example: Example): AnnotatedString {
    val color = MaterialTheme.colorScheme.primary
    return buildAnnotatedString {
        append(example.before)
        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = color)) { append(example.target) }
        append(example.after)
    }
}

/** 자주 쓰는 표현 + 원어민 예문 블록 */
@Composable
fun UsageSection(word: Word, speak: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text("자주 쓰는 표현", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(word.phrase, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(word.phraseKo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            SpeakButton(word.phrase.replace("~", ""), speak)
        }
        Spacer(Modifier.height(12.dp))
        Text("원어민 예문", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
        word.examples.forEach { example ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f).padding(vertical = 4.dp)) {
                    Text(highlighted(example), style = MaterialTheme.typography.bodyLarge)
                    Text(example.ko, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                SpeakButton(example.plain, speak)
            }
        }
    }
}
