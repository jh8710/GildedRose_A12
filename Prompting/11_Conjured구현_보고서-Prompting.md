# Conjured 구현 보고서 Prompting

## 사용 프롬프트

### 1. Conjured 구현 요청 프롬프트

```text
@GildedRose.java @GildedRoseTest.java @GildedRoseRequirements.txt

Conjured 아이템 기능을 추가해줘.
- 규칙: 매일 quality -2, 기한(sellIn < 0) 후 -4, 최소 0
- Item 클래스 수정 없이 구현
- 구현 후 mvn test 실행해서 Green 확인
  (실패하면 자동 수정)
```

### 2. 보고서 내보내기 프롬프트

```text
이번에 한 내용을 report 폴더에 11_QA_Conjured구현_보고서.md 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더에 11_Conjured구현_보고서-Prompting.md 파일로 내보내줘
```

## 산출물 요약

### 구현 확인

`GildedRose.java`에서 `Conjured` 아이템은 이름이 `Conjured`로 시작하는지 확인해 판별한다.

```java
private static final String CONJURED_PREFIX = "Conjured";

private boolean isConjured(Item item) {
    return item.name != null && item.name.startsWith(CONJURED_PREFIX);
}
```

`Conjured` 아이템은 판매 기한 전에는 2, 판매 기한 경계 및 이후에는 4만큼 `quality`를 감소시킨다.

```java
} else if (isConjured(item)) {
    decreaseQuality(item, item.sellIn <= 0 ? 4 : 2);
}
```

`quality` 최소값 0은 공통 감소 메서드에서 보장한다.

```java
private void decreaseQuality(Item item, int amount) {
    item.quality = Math.max(MIN_QUALITY, item.quality - amount);
}
```

`Item` 클래스는 수정하지 않았다.

### 테스트 확인

`GildedRoseTest.java`에는 `Conjured Mana Cake`에 대한 파라미터화 테스트가 포함되어 있다.

검증 범위는 다음과 같다.

1. 판매 기한 전 `quality -2`
2. 판매 기한 경계 및 이후 `quality -4`
3. `quality` 최소값 0 보장
4. `sellIn` 1 감소

### Maven 검증 결과

```bash
mvn test
```

```text
Tests run: 35, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 생성 파일

1. `report/11_QA_Conjured구현_보고서.md`
2. `Prompting/11_Conjured구현_보고서-Prompting.md`
