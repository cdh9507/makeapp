package com.cdh.wordstudy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cdh.wordstudy.data.ProgressStore
import com.cdh.wordstudy.data.Word

@Composable
fun WordListScreen(
    screen: Screen.WordList,
    progress: ProgressStore,
    speak: (String) -> Unit,
    back: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = remember(query, screen.words) {
        val q = query.trim()
        if (q.isEmpty()) screen.words
        else screen.words.filter { it.word.contains(q, ignoreCase = true) || it.meaning.contains(q) }
    }

    Scaffold(topBar = { BackTopBar("${screen.title} 단어 목록", back) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("영어 또는 뜻으로 검색") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            )
            Text(
                "단어를 누르면 자주 쓰는 표현과 원어민 예문이 펼쳐져요",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
                items(filtered, key = { it.word }) { word ->
                    WordRow(word, progress, speak)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun WordRow(word: Word, progress: ProgressStore, speak: (String) -> Unit) {
    var expanded by rememberSaveable(word.word) { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(word.word, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Text(word.pos, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    if (word.word in progress.known) {
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Filled.CheckCircle, contentDescription = "외움", tint = CorrectColor)
                    }
                }
                Text(word.meaning, style = MaterialTheme.typography.bodyLarge)
            }
            SpeakButton(word.word, speak)
            FavoriteButton(word.word in progress.favorites) { progress.toggleFavorite(word) }
        }
        AnimatedVisibility(visible = expanded) {
            UsageSection(word, speak, Modifier.padding(top = 8.dp, end = 12.dp))
        }
    }
}
