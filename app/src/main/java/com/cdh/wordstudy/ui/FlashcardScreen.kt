package com.cdh.wordstudy.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cdh.wordstudy.data.ProgressStore

@Composable
fun FlashcardScreen(
    screen: Screen.Flashcards,
    progress: ProgressStore,
    speak: (String) -> Unit,
    back: () -> Unit,
) {
    var round by remember { mutableIntStateOf(0) }
    val deck = remember(round) { screen.words.shuffled() }
    var index by remember(round) { mutableIntStateOf(0) }
    var flipped by remember(round) { mutableStateOf(false) }
    var knownCount by remember(round) { mutableIntStateOf(0) }

    Scaffold(topBar = { BackTopBar("${screen.title} 플래시카드", back) }) { padding ->
        if (index >= deck.size) {
            Column(
                Modifier.padding(padding).fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("🎉 다 봤어요!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text("알아요 $knownCount · 몰라요 ${deck.size - knownCount}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(24.dp))
                Button(onClick = { round++ }, modifier = Modifier.fillMaxWidth()) { Text("한 번 더") }
                OutlinedButton(onClick = back, modifier = Modifier.fillMaxWidth()) { Text("돌아가기") }
            }
            return@Scaffold
        }

        val word = deck[index]
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            LinearProgressIndicator(progress = { index / deck.size.toFloat() }, modifier = Modifier.fillMaxWidth())
            Text(
                "${index + 1} / ${deck.size}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
            )
            Card(
                onClick = { flipped = !flipped },
                modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 12.dp),
            ) {
                Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
                    Column(
                        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(word.word, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                            SpeakButton(word.word, speak)
                            FavoriteButton(word.word in progress.favorites) { progress.toggleFavorite(word) }
                        }
                        Text(word.pos, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        if (flipped) {
                            Text(
                                word.meaning,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(Modifier.height(20.dp))
                            UsageSection(word, speak, Modifier.fillMaxWidth())
                        } else {
                            Text(
                                "뜻을 떠올려 보고 카드를 눌러 확인하세요",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        progress.markUnknown(word)
                        flipped = false
                        index++
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                ) { Text("몰라요") }
                Button(
                    onClick = {
                        progress.markKnown(word)
                        knownCount++
                        flipped = false
                        index++
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                ) { Text("알아요") }
            }
        }
    }
}
