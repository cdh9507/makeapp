package com.cdh.wordstudy.quiz

import com.cdh.wordstudy.data.Example
import com.cdh.wordstudy.data.Word
import kotlin.random.Random

enum class QuizType(val label: String) {
    MEANING("영어 → 뜻"),
    WORD("뜻 → 영어"),
    SENTENCE("예문 빈칸 채우기"),
}

data class QuizQuestion(
    val word: Word,
    val prompt: String,
    /** 보조 설명 (빈칸 문제의 한국어 해석 등) */
    val hint: String?,
    val choices: List<String>,
    val answerIndex: Int,
    /** 빈칸 문제에 사용된 예문 */
    val example: Example?,
)

object QuizGenerator {
    const val CHOICES = 4

    fun generate(
        targets: List<Word>,
        pool: List<Word>,
        type: QuizType,
        count: Int = 10,
        random: Random = Random.Default,
    ): List<QuizQuestion> = targets.shuffled(random).take(count).map { word ->
        val example = if (type == QuizType.SENTENCE) word.examples.randomOrNull(random) else null
        val answer = when (type) {
            QuizType.MEANING -> word.meaning
            QuizType.WORD, QuizType.SENTENCE -> word.word
        }
        val distractors = pool.asSequence()
            .filter { it.word != word.word }
            .map { if (type == QuizType.MEANING) it.meaning else it.word }
            .filter { it != answer }
            .distinct()
            .toList()
            .shuffled(random)
            .take(CHOICES - 1)
        val choices = (distractors + answer).shuffled(random)
        QuizQuestion(
            word = word,
            prompt = when (type) {
                QuizType.MEANING -> word.word
                QuizType.WORD -> word.meaning
                QuizType.SENTENCE -> example?.blanked ?: word.meaning
            },
            hint = example?.ko,
            choices = choices,
            answerIndex = choices.indexOf(answer),
            example = example,
        )
    }
}
