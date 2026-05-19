# 테스트계획 보고서 Prompting

## 사용 프롬프트

### 1. 테스트 계획서 작성 프롬프트

```text
@GildedRose.java @GildedRoseRequirements.txt @requirements_analysis.md

[P] 시니어 QA 리드입니다.
[C] Java 21, JUnit 5, Maven, (가능하면 JaCoCo)
[T] 테스트 계획서를 작성해줘.
    - 단위 테스트 범위/우선순위
    - 경계값 테스트 (quality 0,1,49,50 / sellIn 0,-1)
    - 예외/특이 케이스 목록
    - 커버리지 목표 (예: 90%+)와 JaCoCo 달성 전략
[F] Markdown 문서. test_plan.md로 저장
```

### 2. 보고서 내보내기 프롬프트

```text
이번에 한 내용을 report 폴더에 04_테스트계획_보고서.md 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더에 04_테스트계획_보고서-Prompting.md 파일로 내보내줘
```

## 산출물

# Gilded Rose 테스트 계획 보고서

## 1. 목적과 범위

본 문서는 `GildedRose.updateQuality()`의 비즈니스 규칙을 Java 21, JUnit 5, Maven 환경에서 검증하기 위한 테스트 계획이다. 테스트 대상은 `Item`의 공개 필드(`name`, `sellIn`, `quality`)를 입력으로 받아 하루 경과 후 상태를 갱신하는 도메인 로직이며, 요구사항상 `Item` 클래스와 `items` 속성의 외부 계약은 변경하지 않는 것을 전제로 한다.

현재 기본 테스트는 아이템 이름이 유지되는지만 확인하므로, 품질 변화, 판매 기한 변화, 상하한 보정, 특수 아이템 규칙을 보호하는 단위 테스트 보강이 최우선이다.

## 2. 테스트 대상 규칙 요약

| 아이템 유형 | 식별 기준 | 핵심 검증 규칙 |
| --- | --- | --- |
| Normal | 특수 이름이 아닌 일반 아이템 | 매일 `sellIn` 1 감소, `quality` 1 감소, 만료 후 `quality` 2 감소 |
| Aged Brie | `Aged Brie` | 매일 `sellIn` 1 감소, `quality` 증가, 만료 후 2 증가, 최대 50 |
| Backstage Pass | `Backstage passes to a TAFKAL80ETC concert` | `sellIn` 구간별 1/2/3 증가, 콘서트 이후 `quality` 0, 최대 50 |
| Sulfuras | `Sulfuras, Hand of Ragnaros` | `sellIn`과 `quality` 모두 불변, `quality` 80 예외 허용 |
| Conjured | 이름에 `Conjured` 포함 | Normal 대비 2배 빠르게 감소, 만료 후 4 감소, 최소 0 |

## 3. 단위 테스트 범위와 우선순위

### P0: 핵심 회귀 방지

1. Normal 아이템의 기본 감소 규칙
   - `sellIn > 0`에서 `sellIn - 1`, `quality - 1`
   - `sellIn = 0`에서 업데이트 후 `sellIn = -1`, `quality - 2`
   - `sellIn < 0`에서 `quality - 2`

2. 품질 하한과 상한 불변식
   - 비-`Sulfuras` 아이템은 업데이트 후 `0 <= quality <= 50`
   - 감소형 아이템은 `quality = 0`에서 음수가 되지 않음
   - 증가형 아이템은 `quality = 49`, `quality = 50`에서 50을 초과하지 않음

3. 특수 아이템 규칙
   - `Aged Brie`의 증가 및 만료 후 2배 증가
   - `Backstage Pass`의 `sellIn` 구간별 증가와 콘서트 이후 0 처리
   - `Sulfuras`의 `sellIn`, `quality` 불변

4. Conjured 신규 요구사항
   - 만료 전 `quality - 2`
   - `sellIn = 0` 및 만료 후 `quality - 4`
   - 감소량보다 낮은 품질값은 0으로 보정

### P1: 경계값과 조합 검증

1. 모든 아이템 유형에 대해 `sellIn = 0`, `sellIn = -1` 경계 검증
2. 모든 관련 아이템 유형에 대해 `quality = 0`, `1`, `49`, `50` 경계 검증
3. Backstage Pass 전환점 검증
   - `sellIn = 11`: 1 증가
   - `sellIn = 10`: 2 증가
   - `sellIn = 6`: 2 증가
   - `sellIn = 5`: 3 증가
   - `sellIn = 1`: 3 증가 후 `sellIn = 0`
   - `sellIn = 0`: `quality = 0`

### P2: 구조와 유지보수성 보강

1. Parameterized Test로 아이템 유형별 반복 케이스를 표 형태로 관리
2. 테스트 이름은 `given_when_then` 또는 `should...when...` 형태로 기대 동작을 드러내도록 작성
3. 여러 아이템을 한 배열에 넣었을 때 각 아이템이 독립적으로 갱신되는지 확인
4. 장기 시뮬레이션은 최소화하되, 2~3일 연속 업데이트가 필요한 회귀 케이스만 선별

## 4. 경계값 테스트 매트릭스

### Quality 경계값

| `quality` | 대상 | 기대 결과 |
| --- | --- | --- |
| 0 | Normal, Conjured | 업데이트 후에도 0 미만으로 내려가지 않음 |
| 0 | Backstage Pass, sellIn 0 | 콘서트 이후 규칙으로 0 유지 |
| 1 | Normal, sellIn 0 또는 -1 | 감소량 2 적용 후 0으로 보정 |
| 1 | Conjured, sellIn > 0 | 감소량 2 적용 후 0으로 보정 |
| 1 | Conjured, sellIn 0 또는 -1 | 감소량 4 적용 후 0으로 보정 |
| 49 | Aged Brie | 증가 후 최대 50 |
| 49 | Backstage Pass | 구간별 증가 후 최대 50 |
| 50 | Aged Brie, Backstage Pass | 업데이트 후에도 50 유지 |
| 80 | Sulfuras | 80 유지, 일반 상한 50 예외 |

### SellIn 경계값

| `sellIn` | 대상 | 기대 결과 |
| --- | --- | --- |
| 0 | Normal | 업데이트 후 `sellIn = -1`, `quality` 2 감소 |
| 0 | Aged Brie | 업데이트 후 `sellIn = -1`, `quality` 2 증가 |
| 0 | Backstage Pass | 업데이트 후 `sellIn = -1`, `quality = 0` |
| 0 | Conjured | 업데이트 후 `sellIn = -1`, `quality` 4 감소 |
| 0 | Sulfuras | `sellIn = 0`, `quality = 80` 유지 |
| -1 | Normal | 업데이트 후 `sellIn = -2`, `quality` 2 감소 |
| -1 | Aged Brie | 업데이트 후 `sellIn = -2`, `quality` 2 증가 |
| -1 | Backstage Pass | 업데이트 후 `sellIn = -2`, `quality = 0` |
| -1 | Conjured | 업데이트 후 `sellIn = -2`, `quality` 4 감소 |
| -1 | Sulfuras | `sellIn = -1`, `quality = 80` 유지 |

## 5. 예외 및 특이 케이스 목록

1. `Sulfuras`는 일반 품질 상한 50의 예외이며, `quality = 80`을 유지해야 한다.
2. `Sulfuras`는 판매 기한 만료 개념이 없으므로 `sellIn = 0`, `sellIn = -1`에서도 값이 변하지 않아야 한다.
3. `sellIn`은 음수가 될 수 있으며, 업데이트 로직이 0으로 보정해서는 안 된다.
4. `Backstage Pass`는 업데이트 전 `sellIn` 값 기준으로 증가량을 결정하고, 감소 후 `sellIn < 0`이면 `quality = 0`이 되어야 한다.
5. `Backstage Pass`는 콘서트가 지난 뒤 기존 `quality`가 0, 1, 49, 50 중 무엇이든 최종 `quality = 0`이어야 한다.
6. Conjured는 신규 요구사항이므로 기존 Normal 분기로 처리되면 결함이다. 이름에 `Conjured`가 포함된 대표 케이스(`Conjured Mana Cake`)를 반드시 포함한다.
7. 감소량이 현재 `quality`보다 큰 경우에도 최종값은 0이어야 하며 음수가 되면 안 된다.
8. 증가형 아이템은 증가량이 2 또는 3이어도 50을 초과하면 안 된다.
9. 빈 아이템 배열은 예외 없이 처리되어야 한다.
10. `items` 배열에 여러 아이템이 섞여 있어도 한 아이템의 규칙이 다른 아이템에 영향을 주면 안 된다.

## 6. 권장 테스트 구조

테스트 클래스는 다음처럼 관심사별로 분리한다.

- `GildedRoseNormalItemTest`: 일반 아이템 감소와 만료 후 감소
- `GildedRoseAgedBrieTest`: 증가 규칙과 상한
- `GildedRoseBackstagePassTest`: 구간별 증가, 콘서트 이후 0 처리
- `GildedRoseSulfurasTest`: 불변 규칙
- `GildedRoseConjuredTest`: 신규 요구사항
- `GildedRoseInvariantTest`: 전체 아이템 공통 불변식과 혼합 배열

단순 케이스는 JUnit 5 `@ParameterizedTest`와 `@CsvSource`를 사용해 중복을 줄인다. 단, Backstage Pass처럼 전환점이 많은 규칙은 입력과 기대값을 명확히 읽을 수 있도록 별도 테스트 또는 `@MethodSource`를 사용한다.

## 7. 커버리지 목표와 JaCoCo 달성 전략

### 목표

- 라인 커버리지: 90% 이상
- 브랜치 커버리지: 90% 이상
- 신규 또는 수정된 도메인 로직: 95% 이상
- `GildedRose.updateQuality()`의 주요 분기: 100% 실행 목표

### Maven/JaCoCo 전략

1. `pom.xml`의 컴파일 설정을 Java 21로 맞춘다.
   - `maven.compiler.release`를 `21`로 설정하는 방식을 권장한다.

2. `jacoco-maven-plugin`을 추가한다.
   - `prepare-agent`로 테스트 실행 시 커버리지 데이터를 수집한다.
   - `report`로 HTML/XML 리포트를 생성한다.
   - `check`로 최소 커버리지 기준을 빌드 게이트로 둔다.

3. 커버리지 기준은 단계적으로 적용한다.
   - 1단계: 라인 90%, 브랜치 85%로 시작해 기존 레거시 분기를 빠르게 보호한다.
   - 2단계: 리팩터링 또는 Conjured 구현 완료 후 라인/브랜치 90% 이상으로 상향한다.
   - 3단계: `GildedRose` 클래스 단위 기준을 별도로 두어 핵심 로직의 누락을 방지한다.

4. 단순 커버리지 숫자보다 요구사항 매핑을 우선한다.
   - Normal, Aged Brie, Backstage Pass, Sulfuras, Conjured의 대표 규칙이 모두 테스트와 연결되어야 한다.
   - `quality` 상하한, `sellIn` 0/-1, Backstage Pass 전환점은 커버리지와 별개로 필수 통과 조건으로 관리한다.

5. CI 또는 로컬 검증 명령은 다음을 기준으로 한다.

```bash
mvn clean test
mvn clean verify
```

JaCoCo 리포트는 일반적으로 `target/site/jacoco/index.html`에서 확인한다.

## 8. 완료 기준

1. P0 테스트가 모두 작성되고 통과한다.
2. `Conjured` 요구사항 테스트가 실패한다면 구현 결함으로 분류하고 수정 전까지 명시적으로 추적한다.
3. `quality` 경계값 0, 1, 49, 50과 `sellIn` 경계값 0, -1이 테스트에 포함된다.
4. `mvn clean test`가 성공한다.
5. JaCoCo 도입 후 `mvn clean verify`가 설정된 커버리지 기준을 만족한다.
6. 요구사항 문서의 각 주요 규칙이 최소 하나 이상의 테스트 케이스와 연결된다.
