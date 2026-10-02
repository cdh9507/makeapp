package com.cdh.wordstudy.data

import android.content.Context
import org.json.JSONArray

object WordRepository {
    fun load(context: Context): List<Word> {
        val text = context.assets.open("words.json").bufferedReader().use { it.readText() }
        val array = JSONArray(text)
        return List(array.length()) { i ->
            val o = array.getJSONObject(i)
            val ex = o.getJSONArray("examples")
            Word(
                index = i,
                word = o.getString("word"),
                pos = o.getString("pos"),
                meaning = o.getString("meaning"),
                phrase = o.getString("phrase"),
                phraseKo = o.getString("phraseKo"),
                examples = List(ex.length()) { j ->
                    val e = ex.getJSONObject(j)
                    Example(en = e.getString("en"), ko = e.getString("ko"))
                },
            )
        }
    }
}
