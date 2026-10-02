package com.cdh.wordstudy.quiz

import com.cdh.wordstudy.data.Example
import com.cdh.wordstudy.data.Word
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class QuizGeneratorTest {
    private val words = (0 until 30).map { i ->
        Word(
            index = i,
            word = "word$i",
            pos = "명",
            meaning = "뜻$i",
            phrase = "phrase$i",
            phraseKo = "표현$i",
            examples = listOf(Example("I like [word${i}s] a lot.", "해석$i")),
        )
    }

    @Test
    fun answerIndexPointsToCorrectChoice() {
        for (type in QuizType.values()) {
            val questions = QuizGenerator.generate(words.take(20), words, type, random = Random(1))
            assertEquals(10, questions.size)
            for (q in questions) {
                assertEquals(QuizGenerator.CHOICES, q.choices.size)
                assertEquals(q.choices.size, q.choices.toSet().size)
                val expected = if (type == QuizType.MEANING) q.word.meaning else q.word.word
                assertEquals(expected, q.choices[q.answerIndex])
            }
        }
    }

    @Test
    fun sentenceQuizHidesTargetWord() {
        val q = QuizGenerator.generate(words.take(1), words, QuizType.SENTENCE, random = Random(2)).single()
        assertEquals("I like _____ a lot.", q.prompt)
        assertFalse(q.prompt.contains("word0"))
        assertEquals("해석0", q.hint)
    }

    @Test
    fun smallPoolStillProducesQuestions() {
        val q = QuizGenerator.generate(words.take(2), words.take(2), QuizType.MEANING).first()
        assertEquals(2, q.choices.size)
        assertTrue(q.answerIndex in q.choices.indices)
    }

    @Test
    fun exampleHelpers() {
        val e = Example("Winter is [approaching], so get a coat.", "")
        assertEquals("approaching", e.target)
        assertEquals("Winter is approaching, so get a coat.", e.plain)
        assertEquals("Winter is _____, so get a coat.", e.blanked)
    }
}
