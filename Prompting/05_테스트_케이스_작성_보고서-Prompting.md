# 테스트 케이스 작성 보고서 Prompting

## 사용 프롬프트

### 1. 테스트 케이스 작성 프롬프트

```text
@GildedRoseTest.java @GildedRose.java @requirements_analysis.md

[P] 테스트 설계에 강한 시니어 Java QA입니다.
[C] Java 21, JUnit 5
[T] 아이템 타입별 최소 5개 테스트를 작성해줘.
    - Normal / Aged Brie / Backstage Pass / Sulfuras / Conjured
    - @DisplayName + Given-When-Then
    - 경계값 (quality 0/50, sellIn 0/-1) 반드시 포함
    - 가능한 경우 @ParameterizedTest + @CsvSource 사용
[F] 완성된 테스트 코드 (파일 단위 수정 포함). mvn test가 Green이 되게 작성
```

### 2. 보고서 내보내기 프롬프트

```text
이번에 한 내용을 report 폴더에 05_테스트_케이스_작성_보고서.md 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더에 05_테스트_케이스_작성_보고서-Prompting.md 파일로 내보내줘
```

## 산출물

# Gilded Rose 테스트 케이스 작성 보고서

## 1. 목적과 범위

본 문서는 `GildedRose.updateQuality()`의 아이템 타입별 비즈니스 규칙을 Java 21, JUnit 5 환경에서 검증하기 위해 작성한 테스트 케이스와 구현 보완 내용을 정리한다.

테스트 대상은 다음 5개 아이템 유형이다.

- Normal
- Aged Brie
- Backstage Pass
- Sulfuras
- Conjured

요구사항에 따라 각 아이템 타입별 최소 5개 이상의 테스트 데이터를 구성했으며, `@DisplayName`, Given-When-Then 구조, `@ParameterizedTest`, `@CsvSource`를 적용했다.

## 2. 테스트 작성 기준

1. 아이템 타입별 규칙을 독립적으로 검증한다.
2. 반복 가능한 입력/기대값은 `@ParameterizedTest`와 `@CsvSource`로 관리한다.
3. 각 테스트 메서드는 Given-When-Then 주석 구조로 Arrange, Act, Assert 단계를 분리한다.
4. `quality` 경계값 `0`, `50`을 포함한다.
5. `sellIn` 경계값 `0`, `-1`을 포함한다.
6. 증가형 아이템은 `quality`가 50을 초과하지 않는지 검증한다.
7. 감소형 아이템은 `quality`가 0 미만으로 내려가지 않는지 검증한다.
8. `Sulfuras`는 일반 상한 50의 예외와 값 불변성을 검증한다.
9. `Conjured`는 Normal 대비 2배 빠른 감소 규칙을 검증한다.

## 3. 작성된 테스트 케이스 요약

### Normal

`+5 Dexterity Vest`를 대표 Normal 아이템으로 사용했다.

- `sellIn > 0`에서 `sellIn`은 1 감소하고 `quality`는 1 감소한다.
- `sellIn = 0`에서 업데이트 후 `sellIn = -1`, `quality`는 2 감소한다.
- `sellIn = -1`에서 업데이트 후 `quality`는 2 감소한다.
- `quality = 0`에서는 업데이트 후에도 0을 유지한다.
- `quality = 1`, `sellIn = 0`에서는 감소량이 현재 품질보다 커도 0으로 보정된다.
- `quality = 50` 입력도 업데이트 후 정상 범위 내로 감소한다.

### Aged Brie

`Aged Brie`는 시간이 지날수록 품질이 증가하는 특수 아이템으로 검증했다.

- `sellIn > 0`에서 `quality`가 1 증가한다.
- `sellIn = 0`에서 업데이트 후 `quality`가 2 증가한다.
- `sellIn = -1`에서 업데이트 후 `quality`가 2 증가한다.
- `quality = 49`에서 증가해도 최종값은 50을 초과하지 않는다.
- `quality = 50`에서 업데이트 후에도 50을 유지한다.
- `sellIn`은 일반 아이템과 동일하게 매일 1 감소한다.

### Backstage Pass

`Backstage passes to a TAFKAL80ETC concert`는 공연일까지 남은 기간에 따른 증가율과 공연 이후 0 처리 규칙을 검증했다.

- `sellIn = 11`에서는 `quality`가 1 증가한다.
- `sellIn = 10`에서는 `quality`가 2 증가한다.
- `sellIn = 6`에서는 `quality`가 2 증가한다.
- `sellIn = 5`에서는 `quality`가 3 증가한다.
- `sellIn = 1`에서는 `quality`가 3 증가하고 `sellIn = 0`이 된다.
- `sellIn = 0`에서는 업데이트 후 `quality = 0`이 된다.
- `sellIn = -1`에서도 `quality = 0`이 된다.
- `quality = 49`, `quality = 50` 근처에서 증가해도 50을 초과하지 않는다.

### Sulfuras

`Sulfuras, Hand of Ragnaros`는 모든 일반 규칙의 예외로 검증했다.

- `sellIn > 0`에서도 `sellIn`과 `quality`가 변하지 않는다.
- `sellIn = 0`에서도 값이 변하지 않는다.
- `sellIn = -1`에서도 값이 변하지 않는다.
- `quality = 80`을 유지하며 일반 품질 상한 50의 예외임을 확인한다.
- `quality = 50` 입력에서도 구현상 `Sulfuras` 값 불변 규칙이 적용됨을 확인한다.

### Conjured

`Conjured Mana Cake`를 대표 Conjured 아이템으로 사용했다.

- `sellIn > 0`에서 `quality`가 2 감소한다.
- `sellIn = 0`에서 업데이트 후 `sellIn = -1`, `quality`가 4 감소한다.
- `sellIn = -1`에서 업데이트 후 `quality`가 4 감소한다.
- `quality = 0`에서는 업데이트 후에도 0을 유지한다.
- `quality = 2`, `quality = 3`처럼 감소량보다 낮은 값은 0으로 보정된다.
- `quality = 50` 입력도 업데이트 후 정상 범위 내로 감소한다.

## 4. 테스트 코드 구조

테스트 파일은 `src/test/java/com/gildedrose/GildedRoseTest.java`에 작성했다.

적용한 주요 JUnit 5 요소는 다음과 같다.

- `@DisplayName`: 테스트 목적을 한글 문장으로 명확히 표현
- `@ParameterizedTest`: 동일 규칙에 대한 여러 입력값 검증
- `@CsvSource`: `sellIn`, `quality`, 기대 `sellIn`, 기대 `quality`를 표 형태로 관리
- `assertEquals`: 아이템 이름, 판매 기한, 품질 결과 검증

공통 실행과 검증 중복은 다음 헬퍼 메서드로 줄였다.

- `updateQuality(Item item)`: 단일 아이템을 `GildedRose`에 전달하고 하루 업데이트 실행
- `assertItem(Item item, String expectedName, int expectedSellIn, int expectedQuality)`: 최종 상태 검증

## 5. 구현 보완 내용

`Conjured` 테스트까지 통과시키기 위해 `src/main/java/com/gildedrose/GildedRose.java`를 함께 보완했다.

주요 변경 내용은 다음과 같다.

1. 아이템 이름과 품질 경계값을 상수로 분리했다.
2. `updateQuality()`의 반복문은 각 아이템을 `updateItem()`에 위임하도록 정리했다.
3. `Aged Brie`, `Backstage Pass`, `Sulfuras`, `Conjured`, Normal 분기를 명확히 분리했다.
4. `increaseQuality()`와 `decreaseQuality()` 헬퍼로 `quality` 상한 50, 하한 0 보정을 공통화했다.
5. `Conjured` 이름 접두어를 기준으로 신규 아이템 규칙을 적용했다.
6. `Sulfuras`는 조기 반환으로 `sellIn`과 `quality`가 모두 변하지 않도록 했다.

## 6. 테스트 실행 결과

검증 명령은 다음과 같다.

```bash
mvn test
```

실행 결과는 성공이다.

```text
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

테스트 실행 중 Maven 컴파일 경고가 출력되었다.

- 현재 `pom.xml`은 `maven.compiler.source`를 `1.8`로 설정하고 있다.
- Java 21 환경에서 Java 8 source/target 옵션이 오래된 값이라는 경고가 발생한다.
- 테스트 실패 원인은 아니며, 빌드는 성공했다.

## 7. 완료 기준 충족 여부

1. Normal, Aged Brie, Backstage Pass, Sulfuras, Conjured 타입별 최소 5개 이상의 테스트 데이터를 작성했다.
2. 모든 테스트 메서드에 `@DisplayName`을 적용했다.
3. 테스트 본문에 Given-When-Then 구조를 적용했다.
4. 가능한 케이스는 모두 `@ParameterizedTest`와 `@CsvSource`로 작성했다.
5. `quality 0/50`, `sellIn 0/-1` 경계값을 포함했다.
6. `Conjured` 신규 규칙을 구현하고 테스트로 보호했다.
7. `mvn test` 결과 Green을 확인했다.

## 8. 산출물

- 테스트 코드: `src/test/java/com/gildedrose/GildedRoseTest.java`
- 구현 보완 코드: `src/main/java/com/gildedrose/GildedRose.java`
- 테스트 실행 명령: `mvn test`
- 최종 결과: 34개 테스트 통과
