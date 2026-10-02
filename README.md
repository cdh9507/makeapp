# 중학 영단어 (Android)

미국 중학생 수준(Grade 6~8) 영단어 160개를 공부하는 안드로이드 앱입니다.
Kotlin + Jetpack Compose로 만들었습니다.

## 기능
- **Day별 학습**: 하루 20단어씩 8일 코스, 진도율 표시
- **자주 쓰는 표현**: 단어마다 원어민이 함께 많이 쓰는 표현 (예: `on purpose` 일부러)
- **원어민 예문 2개**: 일상 대화에서 실제로 쓰는 문장 + 해석, 학습 단어 강조
- **발음 듣기**: 단어·표현·예문 모두 TTS(미국식 발음)로 재생
- **플래시카드**: 카드를 뒤집어 뜻/예문 확인 → 알아요/몰라요
- **퀴즈 3종**: 영어→뜻, 뜻→영어, **예문 빈칸 채우기**
- **오답 노트 / 즐겨찾기 / 전체 검색**

## 폰에 설치하기
1. GitHub 저장소의 **Actions** 탭 → 가장 최근 `Android Build` 실행을 엽니다.
2. 아래 **Artifacts**의 `wordstudy-debug-apk`를 내려받아 압축을 풉니다.
3. `app-debug.apk`를 폰으로 옮겨 실행합니다. ("출처를 알 수 없는 앱 설치"를 허용해야 합니다.)

## 직접 빌드하기
Android Studio로 이 폴더를 열고 ▶ Run, 또는 터미널에서:

```bash
./gradlew assembleDebug        # APK: app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest    # 단위 테스트
```

## 단어 추가/수정
`app/src/main/assets/words.json`을 고치면 됩니다. 예문에서 학습 단어는 `[대괄호]`로 표시합니다
(빈칸 퀴즈와 강조 표시에 사용). 20개 단위로 Day가 자동으로 나뉩니다.
