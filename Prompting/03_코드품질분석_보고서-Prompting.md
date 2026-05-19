# 코드품질분석 보고서 Prompting

## 사용 프롬프트

### 1. 요구사항 분석 프롬프트

```text
@GildedRoseRequirements.txt @README.md

[P] 시니어 Java QA 엔지니어 관점에서
[C] Gilded Rose Java 프로젝트 (Java 21, Maven, JUnit 5)
[T] 요구사항을 분석해서 아래를 정리해줘.
    1) 아이템 타입별 비즈니스 규칙 표
       - Normal / Aged Brie / Backstage Pass / Sulfuras / Conjured
    2) 예외/경계값 조건 (quality 0~50, sellIn 0/-1/음수, Sulfuras 예외)
    3) Conjured 신규 요구사항 (감소율 2배) 명세
    4) 테스트해야 할 주요 시나리오 목록 (번호)
[F] Markdown (표 + 번호 목록). requirements_analysis.md로 저장
```

### 2. 보고서 내보내기 프롬프트

```text
지금까지 한 내용을 report 폴더에 03_코드품질분석_보고서.md 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더에 03_코드품질분석_보고서-Prompting.md 파일로 내보내줘
```

## 산출물

# Gilded Rose 코드 품질 분석 보고서

## 1. 분석 범위

본 보고서는 `GildedRoseRequirements.txt`와 `README.md`의 요구사항을 기준으로, Gilded Rose Java 프로젝트의 핵심 비즈니스 규칙과 테스트 품질 기준을 정리한다.

- 기술 환경: Java 21, Maven, JUnit 5
- 분석 관점: 시니어 Java QA 엔지니어
- 품질 초점: 아이템 타입별 규칙 명확성, 경계값 처리, 예외 규칙, 신규 `Conjured` 요구사항, 테스트 시나리오 도출

## 2. 아이템 타입별 비즈니스 규칙

| 아이템 타입 | 식별 기준 | `sellIn` 일일 변화 | `quality` 일일 변화 | 만료 후 규칙 | `quality` 범위 |
| --- | --- | --- | --- | --- | --- |
| Normal | 특수 이름이 아닌 일반 아이템 | 매일 1 감소 | 매일 1 감소 | 판매 기한이 지나면 하루 2 감소 | 0 이상 50 이하 |
| Aged Brie | `Aged Brie` | 매일 1 감소 | 매일 1 증가 | 판매 기한이 지나면 하루 2 증가 | 0 이상 50 이하 |
| Backstage Pass | `Backstage passes` | 매일 1 감소 | `sellIn >= 11`: 1 증가<br>`6 <= sellIn <= 10`: 2 증가<br>`1 <= sellIn <= 5`: 3 증가 | 콘서트가 지나면 `quality`는 0 | 0 이상 50 이하 |
| Sulfuras | `Sulfuras` | 변하지 않음 | 변하지 않음 | 판매 기한 만료 개념 없음 | 예외적으로 80 유지 |
| Conjured | `Conjured` 아이템 | 매일 1 감소 | Normal 대비 2배 빠르게 감소, 즉 하루 2 감소 | 판매 기한이 지나면 하루 4 감소 | 0 이상 50 이하 |

## 3. 예외 및 경계값 조건

1. `quality`는 `Sulfuras`를 제외하고 항상 `0` 이상 `50` 이하로 유지되어야 한다.
2. `quality = 0`인 감소형 아이템은 업데이트 후에도 음수가 되면 안 된다.
3. `quality = 49` 또는 `quality = 50`인 증가형 아이템은 업데이트 후에도 `50`을 초과하면 안 된다.
4. `sellIn`은 일반 아이템, `Aged Brie`, `Backstage Pass`, `Conjured`에서 매일 1 감소한다.
5. `sellIn`은 음수가 될 수 있으며, 음수 값을 막거나 0으로 보정하면 안 된다.
6. `sellIn = 0`은 업데이트 후 `sellIn = -1`이 되는 만료 경계값이며, 최종 결과에는 만료 후 규칙이 반영되어야 한다.
7. `sellIn = -1` 또는 그보다 작은 값은 이미 판매 기한이 지난 상태로 취급한다.
8. `Backstage Pass`는 `sellIn = 10`, `sellIn = 5`, `sellIn = 0`에서 증가율 또는 만료 처리가 바뀌므로 반드시 경계값으로 검증해야 한다.
9. `Backstage Pass`는 콘서트가 지난 뒤 기존 `quality`와 관계없이 `quality = 0`이 되어야 한다.
10. `Sulfuras`는 일반 `quality` 상한 50의 예외이며, 요구사항상 `quality = 80`을 유지한다.
11. `Sulfuras`는 `sellIn`과 `quality`가 모두 변하지 않아야 한다.
12. `Sulfuras`는 만료 후 2배 감소 규칙, `quality` 상하한 보정 규칙, `sellIn` 감소 규칙을 적용하지 않는다.

## 4. Conjured 신규 요구사항 명세

1. `Conjured`는 신규 아이템 카테고리이며, 기존 Normal 아이템의 감소 규칙을 기준으로 2배 빠르게 `quality`가 감소한다.
2. 판매 기한 전(`sellIn > 0`)에는 하루 업데이트 시 `quality`가 2 감소한다.
3. 판매 기한 경계 및 이후(`sellIn <= 0`)에는 하루 업데이트 시 `quality`가 4 감소한다.
4. `Conjured`도 `sellIn`은 매일 1 감소하며, 업데이트 후 음수가 될 수 있다.
5. `Conjured`도 `quality` 하한 `0`을 반드시 지켜야 하므로 감소량이 현재 `quality`보다 커도 최종값은 `0`이어야 한다.
6. `Conjured`는 증가형 아이템이 아니므로 일반적인 상한 50 규칙을 위반하지 않아야 하며, 입력값 검증 또는 업데이트 결과 기준으로 50 초과 상태를 만들면 안 된다.

## 5. 테스트해야 할 주요 시나리오

1. Normal 아이템은 `sellIn > 0`에서 하루가 지나면 `sellIn`이 1 감소하고 `quality`가 1 감소한다.
2. Normal 아이템은 `sellIn = 0`에서 업데이트 후 `sellIn = -1`이 되고 `quality`가 2 감소한다.
3. Normal 아이템은 `sellIn = -1` 또는 더 작은 음수에서 업데이트 후 `quality`가 2 감소한다.
4. Normal 아이템은 `quality = 0`에서 업데이트 후에도 `quality = 0`을 유지한다.
5. Normal 아이템은 `quality = 1`, `sellIn = 0`에서 업데이트 후 `quality = 0`으로 보정된다.
6. Aged Brie는 `sellIn > 0`에서 하루가 지나면 `sellIn`이 1 감소하고 `quality`가 1 증가한다.
7. Aged Brie는 `sellIn = 0`에서 업데이트 후 `quality`가 2 증가한다.
8. Aged Brie는 `sellIn = -1`에서 업데이트 후 `quality`가 2 증가한다.
9. Aged Brie는 `quality = 49`에서 증가해도 `quality = 50`을 초과하지 않는다.
10. Aged Brie는 `quality = 50`에서 업데이트 후에도 `quality = 50`을 유지한다.
11. Backstage Pass는 `sellIn = 11`에서 업데이트 후 `quality`가 1 증가한다.
12. Backstage Pass는 `sellIn = 10`에서 업데이트 후 `quality`가 2 증가한다.
13. Backstage Pass는 `sellIn = 6`에서 업데이트 후 `quality`가 2 증가한다.
14. Backstage Pass는 `sellIn = 5`에서 업데이트 후 `quality`가 3 증가한다.
15. Backstage Pass는 `sellIn = 1`에서 업데이트 후 `quality`가 3 증가한 뒤 `sellIn = 0`이 된다.
16. Backstage Pass는 `sellIn = 0`에서 업데이트 후 `sellIn = -1`, `quality = 0`이 된다.
17. Backstage Pass는 `quality = 49` 또는 `quality = 50` 근처에서 증가 후에도 `quality = 50`을 초과하지 않는다.
18. Sulfuras는 하루가 지나도 `sellIn`과 `quality`가 모두 변하지 않는다.
19. Sulfuras는 `quality = 80`을 유지하며 일반 `quality` 상한 50의 예외로 검증한다.
20. Sulfuras는 `sellIn = 0`, `sellIn = -1`, 음수 값에서도 값이 변하지 않는다.
21. Conjured 아이템은 `sellIn > 0`에서 하루 업데이트 후 `quality`가 2 감소한다.
22. Conjured 아이템은 `sellIn = 0`에서 업데이트 후 `sellIn = -1`이 되고 `quality`가 4 감소한다.
23. Conjured 아이템은 `sellIn = -1` 또는 더 작은 음수에서 업데이트 후 `quality`가 4 감소한다.
24. Conjured 아이템은 `quality = 0`에서 업데이트 후에도 `quality = 0`을 유지한다.
25. Conjured 아이템은 `quality = 1`, `quality = 2`, `quality = 3`처럼 감소량보다 낮은 값에서 업데이트 후 `quality = 0`으로 보정된다.
26. 모든 비-`Sulfuras` 아이템은 업데이트 후 `quality`가 0 이상 50 이하 범위에 있어야 한다.
27. 모든 비-`Sulfuras` 아이템은 업데이트 후 `sellIn`이 1 감소하며, 음수 값도 허용되어야 한다.

## 6. 코드 품질 관점의 테스트 기준

1. 아이템 타입별 규칙은 이름 기반 분기 오류가 발생하기 쉬우므로 타입별 단위 테스트를 독립적으로 구성해야 한다.
2. `quality` 하한과 상한은 모든 증가형, 감소형 규칙에서 반복 검증되어야 한다.
3. `sellIn = 0`과 `sellIn = -1`은 업데이트 순서에 따라 결과가 달라질 수 있으므로 핵심 회귀 테스트로 고정해야 한다.
4. `Sulfuras`는 모든 일반 규칙의 예외이므로 값 불변성을 별도 테스트로 보호해야 한다.
5. `Conjured`는 신규 기능이므로 Normal 규칙과 비교 가능한 테스트 데이터를 사용해 감소율 2배를 명확히 검증해야 한다.
