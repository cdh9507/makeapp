package com.cdh.wordstudy.data

/**
 * 원어민 예문 한 개. [en] 안의 학습 단어는 대괄호로 표시한다.
 * 예: "Are you sure this information is [accurate]?"
 */
data class Example(val en: String, val ko: String) {
    /** 대괄호를 제거한 원문 */
    val plain: String get() = en.replace("[", "").replace("]", "")

    /** 문장 속에 실제로 쓰인 단어 형태 (예: approaching) */
    val target: String get() = en.substringAfter('[').substringBefore(']')

    val before: String get() = en.substringBefore('[')
    val after: String get() = en.substringAfter(']')

    /** 학습 단어를 빈칸으로 바꾼 문장 */
    val blanked: String get() = "${before}_____$after"
}

data class Word(
    val index: Int,
    val word: String,
    val pos: String,
    val meaning: String,
    /** 원어민이 자주 함께 쓰는 표현 */
    val phrase: String,
    val phraseKo: String,
    val examples: List<Example>,
) {
    val day: Int get() = index / WORDS_PER_DAY + 1

    companion object {
        const val WORDS_PER_DAY = 20
    }
}
