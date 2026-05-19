# Gilded Rose Conjured 구현 QA 보고서

## 1. 작업 개요

본 보고서는 `GildedRoseRequirements.txt`의 신규 요구사항인 `Conjured` 아이템 규칙이 코드와 테스트에 반영되어 있는지 확인하고, Maven 테스트 결과를 정리한다.

- 대상 기능: `Conjured` 아이템 품질 감소 규칙
- 대상 파일: `src/main/java/com/gildedrose/GildedRose.java`, `src/test/java/com/gildedrose/GildedRoseTest.java`
- 제약 조건: `Item` 클래스 수정 금지
- 검증 명령: `mvn test`

## 2. 요구사항

`Conjured` 아이템은 일반 아이템보다 두 배 빠르게 `quality`가 감소해야 한다.

1. 판매 기한 전(`sellIn > 0`)에는 하루에 `quality`가 2 감소한다.
2. 판매 기한 경계 및 이후(`sellIn <= 0`)에는 하루에 `quality`가 4 감소한다.
3. `quality`는 최소 0 미만으로 내려가면 안 된다.
4. `sellIn`은 일반 아이템처럼 매일 1 감소한다.
5. `Item` 클래스는 수정하지 않는다.

## 3. 구현 확인 결과

`GildedRose.java`에는 `Conjured` 아이템을 이름 prefix로 식별하는 상수와 판별 메서드가 적용되어 있다.

```java
private static final String CONJURED_PREFIX = "Conjured";

private boolean isConjured(Item item) {
    return item.name != null && item.name.startsWith(CONJURED_PREFIX);
}
```

아이템 업데이트 분기에서는 `Conjured` 아이템에 대해 판매 기한 상태에 따라 감소량을 다르게 적용한다.

```java
} else if (isConjured(item)) {
    decreaseQuality(item, item.sellIn <= 0 ? 4 : 2);
}
```

품질 하한은 공통 감소 메서드에서 보장한다.

```java
private void decreaseQuality(Item item, int amount) {
    item.quality = Math.max(MIN_QUALITY, item.quality - amount);
}
```

`Item` 클래스는 변경하지 않았으며, 기존 `name`, `sellIn`, `quality` 필드 구조를 그대로 사용한다.

## 4. 테스트 확인 결과

`GildedRoseTest.java`에는 `Conjured Mana Cake`를 대상으로 한 파라미터화 테스트가 포함되어 있다.

검증된 주요 케이스는 다음과 같다.

1. `sellIn > 0`에서 `quality`가 2 감소한다.
2. `sellIn = 0`에서 업데이트 후 `sellIn = -1`, `quality`가 4 감소한다.
3. `sellIn < 0`에서 `quality`가 4 감소한다.
4. `quality = 0`이면 업데이트 후에도 0을 유지한다.
5. 감소량보다 작은 `quality`는 음수가 되지 않고 0으로 보정된다.

## 5. Maven 테스트 결과

다음 명령으로 전체 테스트를 실행했다.

```bash
mvn test
```

실행 결과는 Green이다.

```text
Tests run: 35, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 6. 결론

`Conjured` 아이템 기능은 요구사항에 맞게 반영되어 있으며, `Item` 클래스 수정 없이 구현되어 있다. 판매 기한 전후 감소량과 품질 하한 조건이 테스트로 검증되었고, 전체 Maven 테스트가 성공했다.
