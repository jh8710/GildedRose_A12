# CursorRules 설계보고서 Prompting

## 사용 프롬프트

```text
[P] 당신은 레거시 코드 QA/리팩토링을 돕는 시니어 Java 엔지니어입니다.
[C] Gilded Rose Java 프로젝트에서 Cursor AI가 항상 따라야 하는 규칙을
    프로젝트 루트의 .cursorrules로 작성하려고 합니다.
[T] 아래 요구를 만족하는 .cursorrules 내용을 작성해줘.
    - 기술 스택: Java 21 + Maven + JUnit 5 (+ JaCoCo)
    - 절대 규칙: Item 클래스 수정 금지, quality 0~50 (Sulfuras 제외), sellIn 음수 허용
    - 테스트 규칙: Given-When-Then, @ParameterizedTest, 경계값(quality 0/50, sellIn 0/-1) 포함
    - 리팩토링 규칙: 테스트 Green 상태에서만 진행, 매직 넘버 상수화 권장
[F] .cursorrules 파일에 그대로 붙여넣을 수 있는 텍스트
```

## 산출물

# CursorRules 설계보고서

## 1. 작성 목적

Gilded Rose Java 프로젝트에서 Cursor AI가 레거시 코드 QA와 리팩토링 작업을 수행할 때 항상 따라야 할 프로젝트 규칙을 정의한다.

이 규칙은 Java 21, Maven, JUnit 5, JaCoCo 기반의 Gilded Rose 프로젝트에서 도메인 제약을 보존하고, 안전한 테스트 중심 리팩토링을 유도하기 위한 기준이다.

## 2. 적용 범위

1. Gilded Rose Java 프로젝트 전체에 적용한다.
2. `Item` 클래스와 기존 공개 API를 보호한다.
3. `updateQuality()` 관련 테스트 작성, 결함 수정, 리팩토링 작업에 적용한다.
4. 신규 요구사항인 `Conjured` 아이템 구현 및 검증에도 동일하게 적용한다.

## 3. CursorRules 설계 내용

```text
# Gilded Rose Java Project Rules

You are a senior Java engineer helping with QA and refactoring of a legacy Gilded Rose codebase.

## Tech Stack

- Use Java 21.
- Use Maven for build and dependency management.
- Use JUnit 5 for tests.
- Use JaCoCo for test coverage checks when available.

## Absolute Domain Rules

- Never modify the `Item` class.
- Treat `Item` as legacy/public API code.
- Do not change public behavior unless tests explicitly define the intended change.
- `quality` must always stay between `0` and `50`, except for `Sulfuras`.
- `Sulfuras` quality does not change.
- `sellIn` may become negative. Do not prevent or clamp negative `sellIn` values.
- Preserve existing Gilded Rose rules unless the task explicitly asks to add or change behavior.

## Testing Rules

- Write tests before or alongside refactoring.
- Use clear Given-When-Then structure in test names or test body comments.
- Prefer `@ParameterizedTest` for repeated item rule cases.
- Include boundary values in tests:
  - `quality = 0`
  - `quality = 50`
  - `sellIn = 0`
  - `sellIn = -1`
- Cover normal, expired, and boundary behavior for each item type.
- Tests should verify observable behavior through item name, sellIn, and quality.

## Refactoring Rules

- Refactor only while the test suite is Green.
- Do not refactor blindly without characterization tests for existing behavior.
- Keep changes small and behavior-preserving.
- Prefer simple, readable Java over clever abstractions.
- Extract constants for magic numbers where it improves clarity:
  - minimum quality
  - maximum quality
  - quality change amounts
  - sellIn threshold values
- Avoid over-engineering. Add abstractions only when they make item rules easier to understand and test.
- After each meaningful refactor, run the relevant Maven tests.

## Code Style

- Keep business rules explicit and easy to audit.
- Use descriptive method and constant names.
- Avoid duplicating quality-bound checks.
- Do not introduce unrelated formatting or structural churn.
```

## 4. 기대 효과

1. 레거시 코드의 핵심 제약인 `Item` 클래스 수정 금지를 명확히 한다.
2. `quality`, `sellIn`, `Sulfuras` 예외 조건을 일관되게 유지한다.
3. 테스트 Green 상태에서만 리팩토링하도록 하여 회귀 위험을 줄인다.
4. 경계값 테스트와 Parameterized Test 작성을 유도해 QA 관점의 검증 범위를 넓힌다.
