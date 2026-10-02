package com.cdh.wordstudy.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cdh.wordstudy.data.ProgressStore
import com.cdh.wordstudy.data.Word

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(words: List<Word>, progress: ProgressStore, navigate: (Screen) -> Unit) {
    val days = remember(words) { words.groupBy { it.day }.toSortedMap() }
    val knownCount = words.count { it.word in progress.known }
    var showReset by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("실전 영단어", fontWeight = FontWeight.Bold) },
                actions = { TextButton(onClick = { showReset = true }) { Text("초기화") } },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp)) {
                        Text("전체 진도", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "$knownCount / ${words.size} 단어 외움",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { if (words.isEmpty()) 0f else knownCount / words.size.toFloat() },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            item {
                val wrongWords = words.filter { it.word in progress.wrong }
                val favoriteWords = words.filter { it.word in progress.favorites }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    FilledTonalButton(
                        onClick = { navigate(Screen.StudyMenu("오답 노트", wrongWords)) },
                        enabled = wrongWords.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                    ) { Text("오답 ${wrongWords.size}") }
                    FilledTonalButton(
                        onClick = { navigate(Screen.StudyMenu("즐겨찾기", favoriteWords)) },
                        enabled = favoriteWords.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                    ) { Text("즐겨찾기 ${favoriteWords.size}") }
                    FilledTonalButton(
                        onClick = { navigate(Screen.WordList("전체 단어", words)) },
                        modifier = Modifier.weight(1f),
                    ) { Text("전체 검색") }
                }
            }
            items(days.entries.toList(), key = { it.key }) { (day, dayWords) ->
                val dayKnown = dayWords.count { it.word in progress.known }
                Card(
                    onClick = { navigate(Screen.StudyMenu("Day $day", dayWords)) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Day $day", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(
                                dayWords.take(4).joinToString(", ") { it.word } + " …",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { dayKnown / dayWords.size.toFloat() },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Spacer(Modifier.padding(8.dp))
                        if (dayKnown == dayWords.size) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "완료", tint = CorrectColor)
                        } else {
                            Text("$dayKnown/${dayWords.size}", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }

    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false },
            title = { Text("학습 기록 초기화") },
            text = { Text("외운 단어와 오답 노트 기록을 모두 지울까요? (즐겨찾기는 유지돼요)") },
            confirmButton = {
                TextButton(onClick = { progress.reset(); showReset = false }) { Text("초기화") }
            },
            dismissButton = { TextButton(onClick = { showReset = false }) { Text("취소") } },
        )
    }
}
