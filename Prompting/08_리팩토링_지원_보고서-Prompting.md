# 리팩토링 지원 보고서 Prompting

## 사용 프롬프트

### 1. 리팩토링 계획 요청 프롬프트

```text
@GildedRose.java @code_quality_report.md @requirements_analysis.md

[P] Martin Fowler 스타일 리팩토링 코치입니다.
[C] 제약: Item 클래스 수정 금지, quality 0~50, 테스트 Green에서만 진행
[T] updateQuality()를 점진적으로 리팩토링하는 계획을 제안해줘.
    - 작은 단계 (커밋 단위)로 쪼개기
    - 전략 패턴/ItemUpdater 인터페이스 등 적용 가능성
    - 매직 넘버 상수화, 중복 제거, 가독성 개선
[F] 단계별 체크리스트 + 각 단계 검증 방법 (mvn test)
```

### 2. 보고서 내보내기 프롬프트

```text
이번에 한 내용을 report 폴더에 08_리팩토링_지원_보고서.md 파일로 내보내주고, 
프롬프트를 포함해서 Prompting 폴더에 08_리팩토링_지원_보고서-Prompting.md 파일로 내보내줘
```

## 산출물

# Gilded Rose 리팩토링 지원 보고서

## 1. 목적

본 보고서는 `GildedRose.updateQuality()`를 테스트 Green 상태를 유지하면서 점진적으로 리팩토링하기 위한 실행 계획을 정리한다.

- 역할 관점: Martin Fowler 스타일 리팩토링 코치
- 핵심 제약: `Item` 클래스 수정 금지
- 품질 불변식: `Sulfuras`를 제외한 `quality`는 항상 `0` 이상 `50` 이하
- 진행 원칙: 작은 단계로 변경하고 각 단계마다 `mvn test`로 검증

## 2. 현재 코드 상태 요약

현재 `GildedRose.java`는 이미 일부 리팩토링이 적용되어 있다.

- `MIN_QUALITY`, `MAX_QUALITY`, 주요 아이템명은 상수로 분리되어 있다.
- `updateQuality()`는 아이템 순회만 담당하고, 개별 처리는 `updateItem()`으로 위임한다.
- `increaseQuality()`, `decreaseQuality()`로 품질 상한과 하한 처리가 일부 중복 제거되어 있다.
- `Backstage Pass` 규칙은 `updateBackstagePass()`로 분리되어 있다.

다만 다음 개선 여지는 남아 있다.

- `updateItem()`이 여전히 모든 아이템 타입 분기와 규칙 선택을 알고 있다.
- `sellIn <= 0`, `5`, `10`, `1`, `2`, `3`, `4` 같은 규칙 값의 의미가 더 명확해질 수 있다.
- 신규 아이템이 추가될 때 `GildedRose` 내부 조건문을 수정해야 하므로 OCP 관점에서 취약하다.
- 전략 패턴 또는 `ItemUpdater` 구조를 적용하면 아이템별 규칙 변경 범위를 줄일 수 있다.

## 3. 리팩토링 원칙

1. 테스트가 통과하는 상태에서만 다음 단계로 이동한다.
2. 한 커밋에는 하나의 리팩토링 의도만 담는다.
3. `Item` 클래스는 수정하지 않는다.
4. 동작 변경과 구조 변경을 섞지 않는다.
5. 먼저 이름 붙이기와 메서드 추출을 수행한 뒤, 조건문을 다형성으로 대체한다.

## 4. 단계별 체크리스트

### 0단계. 안전망 고정

목표: 현재 동작을 기준선으로 고정한다.

체크리스트:

- 기존 단위 테스트와 Golden Master 테스트가 모두 통과하는지 확인한다.
- `Sulfuras`는 `quality = 80` 예외를 유지해야 한다.
- `sellIn = 0`, `sellIn = -1`, `quality = 0`, `quality = 49`, `quality = 50` 경계값을 테스트로 보호한다.
- 현재 작업 트리의 테스트 결과를 리팩토링 기준선으로 삼는다.

검증 방법:

```powershell
mvn test
```

권장 커밋 메시지:

```text
test: confirm current gilded rose behavior
```

### 1단계. 매직 넘버 의미 드러내기

목표: 규칙 값에 도메인 이름을 부여한다.

체크리스트:

- `BACKSTAGE_EXPIRED_SELL_IN = 0` 추가를 검토한다.
- `BACKSTAGE_CLOSE_THRESHOLD = 5` 추가를 검토한다.
- `BACKSTAGE_MEDIUM_THRESHOLD = 10` 추가를 검토한다.
- `NORMAL_DEGRADE_AMOUNT = 1` 추가를 검토한다.
- `EXPIRED_DEGRADE_AMOUNT = 2` 추가를 검토한다.
- `BACKSTAGE_CLOSE_INCREASE = 3` 추가를 검토한다.
- `CONJURED_DEGRADE_AMOUNT = 2` 추가를 검토한다.
- `CONJURED_EXPIRED_DEGRADE_AMOUNT = 4` 추가를 검토한다.
- 동작 변경 없이 숫자의 의미만 드러낸다.

검증 방법:

```powershell
mvn test
```

권장 커밋 메시지:

```text
refactor: name quality and backstage rule constants
```

### 2단계. 판매 기한 감소 규칙 분리

목표: `Sulfuras`를 제외한 공통 `sellIn` 감소 규칙을 명시한다.

체크리스트:

- `decreaseSellIn(Item item)` 메서드를 추출한다.
- `item.sellIn = item.sellIn - 1` 직접 대입을 헬퍼 호출로 바꾼다.
- `Sulfuras`는 기존처럼 조기 반환하여 `sellIn`이 감소하지 않게 유지한다.

검증 방법:

```powershell
mvn test
```

권장 커밋 메시지:

```text
refactor: extract sell-in decrement
```

### 3단계. 만료 여부 표현 개선

목표: 반복되는 `sellIn <= 0` 조건을 도메인 언어로 바꾼다.

체크리스트:

- `isExpired(Item item)` 메서드를 추출한다.
- Normal, Aged Brie, Conjured, Backstage Pass 규칙에서 직접 비교를 제거한다.
- `sellIn = 0`은 업데이트 전 기준으로 만료 처리된다는 기존 동작을 유지한다.

검증 방법:

```powershell
mvn test
```

권장 커밋 메시지:

```text
refactor: express expired item rule
```

### 4단계. 아이템별 업데이트 메서드 분리

목표: 전략 패턴 적용 전에 조건문 내부의 규칙 단위를 작게 만든다.

체크리스트:

- `updateNormalItem(Item item)`을 추출한다.
- `updateAgedBrie(Item item)`을 추출한다.
- `updateConjured(Item item)`을 추출한다.
- 기존 `updateBackstagePass(Item item)`는 유지하되 상수와 `isExpired()`를 활용한다.
- `updateItem()`은 타입 판별, 해당 메서드 호출, `sellIn` 감소만 담당하게 줄인다.

검증 방법:

```powershell
mvn test
```

권장 커밋 메시지:

```text
refactor: extract item-specific update methods
```

### 5단계. `ItemUpdater` 인터페이스 도입

목표: 아이템별 갱신 규칙을 전략 객체로 분리한다.

체크리스트:

- `ItemUpdater` 인터페이스를 추가한다.
- `void update(Item item)` 메서드를 정의한다.
- `NormalItemUpdater`를 추가한다.
- `AgedBrieUpdater`를 추가한다.
- `BackstagePassUpdater`를 추가한다.
- `ConjuredItemUpdater`를 추가한다.
- `SulfurasUpdater`를 추가한다.
- 품질 상한/하한 보정 로직은 공통 헬퍼 또는 추상 기반 클래스에서 제공해 중복을 막는다.
- `Item` 클래스는 수정하지 않는다.

검증 방법:

```powershell
mvn test
```

권장 커밋 메시지:

```text
refactor: introduce item updater strategies
```

### 6단계. Updater 선택 책임 분리

목표: 이름 기반 분기를 `GildedRose`에서 팩토리 또는 레지스트리로 이동한다.

체크리스트:

- `ItemUpdaterFactory` 또는 `ItemUpdaterRegistry`를 추가한다.
- `updaterFor(Item item)` 같은 선택 메서드를 둔다.
- `"Aged Brie"`, `"Backstage passes..."`, `"Sulfuras..."`, `"Conjured"` 식별 규칙을 factory 내부로 격리한다.
- `Conjured`는 기존처럼 이름이 `Conjured`로 시작하는 정책을 유지한다.
- 신규 아이템 추가 시 `GildedRose` 본문 수정이 필요 없는 구조인지 확인한다.

검증 방법:

```powershell
mvn test
```

권장 커밋 메시지:

```text
refactor: move item updater selection to factory
```

### 7단계. `GildedRose` 최종 단순화

목표: `GildedRose`가 아이템 순회와 updater 호출만 담당하게 만든다.

체크리스트:

- `updateQuality()`는 루프와 updater 호출만 수행한다.
- 품질 상한/하한은 updater 공통 로직에서만 처리한다.
- `Sulfuras`의 `sellIn`과 `quality` 불변성을 유지한다.
- 모든 비-`Sulfuras` 아이템의 `quality`가 `0..50` 범위에 머무는지 확인한다.

예상 형태:

```java
public void updateQuality() {
    for (Item item : items) {
        itemUpdaterFactory.updaterFor(item).update(item);
    }
}
```

검증 방법:

```powershell
mvn test
```

권장 커밋 메시지:

```text
refactor: simplify gilded rose update loop
```

## 5. 전략 패턴 적용 가능성

`ItemUpdater` 전략 패턴은 이 코드베이스에 적합하다. 이유는 아이템 종류마다 변경 이유가 다르고, 신규 아이템이 추가될 가능성이 있으며, 현재 구조에서는 이름 기반 조건문이 `GildedRose`에 집중되어 있기 때문이다.

권장 구조:

```text
GildedRose
 └─ ItemUpdaterFactory
     ├─ NormalItemUpdater
     ├─ AgedBrieUpdater
     ├─ BackstagePassUpdater
     ├─ SulfurasUpdater
     └─ ConjuredItemUpdater
```

적용 시 주의점:

- `Item`에 타입 필드를 추가하지 않는다.
- 이름 비교는 완전히 제거하기보다 factory 내부로 격리한다.
- 너무 이른 클래스 분리는 피하고, 먼저 메서드 추출로 규칙 경계를 확인한다.
- 공통 품질 보정 로직은 중복되지 않게 한 곳에서 관리한다.

## 6. 전체 실행 순서 요약

1. `mvn test`로 현재 Green 상태를 확인한다.
2. 매직 넘버를 상수화한다.
3. `sellIn` 감소 헬퍼를 추출한다.
4. `isExpired()`를 추출한다.
5. 아이템별 업데이트 메서드를 분리한다.
6. `ItemUpdater` 인터페이스와 전략 클래스를 도입한다.
7. Updater 선택 책임을 factory로 이동한다.
8. `GildedRose.updateQuality()`를 루프와 위임만 남도록 단순화한다.
9. 각 단계마다 `mvn test`를 실행하고 Green 상태에서만 커밋한다.

## 7. 결론

가장 안전한 리팩토링 흐름은 이름 붙이기와 함수 추출로 의도를 먼저 드러낸 뒤, 조건문을 다형성으로 바꾸는 것이다. 각 단계는 작고 독립적인 커밋으로 나누며, 모든 변경은 `mvn test` 통과를 기준으로 진행해야 한다.
