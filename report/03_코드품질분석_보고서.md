# GildedRose updateQuality() Code Quality Report

| 문제점 | 위반 원칙/스멜 | 영향 | 개선 방향 | 우선순위 |
|---|---|---|---|---|
| `updateQuality()` 하나가 모든 아이템 규칙, 품질 변경, 판매기한 감소, 만료 후 처리를 모두 담당한다. | SRP 위반, Long Method | 메서드 변경 이유가 아이템 종류별로 계속 늘어난다. 신규 규칙 추가 시 기존 메서드를 직접 수정해야 하며 회귀 위험이 증가한다. | 아이템 타입별 품질 갱신 정책을 별도 클래스로 분리한다. 예: `ItemUpdater`, `AgedBrieUpdater`, `BackstagePassUpdater`, `SulfurasUpdater` | 1 |
| 아이템 이름 문자열 비교로 분기하며 신규 아이템 추가 시 기존 조건문을 수정해야 한다. | OCP 위반, Primitive Obsession | `Conjured` 같은 신규 아이템 추가 시 `updateQuality()` 내부 조건문이 더 복잡해진다. | 이름 기반 조건문을 전략 패턴과 팩토리로 이동한다. `Item.name`에 맞는 updater를 선택하도록 구조화한다. | 2 |
| `"Aged Brie"`, `"Backstage passes to a TAFKAL80ETC concert"`, `"Sulfuras, Hand of Ragnaros"`, `50`, `0`, `11`, `6` 등이 메서드 내부에 직접 노출되어 있다. | Magic Number, Magic String | 규칙의 의미가 코드에서 바로 드러나지 않고, 같은 값 변경 시 누락 가능성이 증가한다. | 도메인 상수로 분리한다. 예: `MAX_QUALITY = 50`, `MIN_QUALITY = 0`, `BACKSTAGE_FIRST_THRESHOLD = 10`, `BACKSTAGE_SECOND_THRESHOLD = 5` | 3 |
| 품질 상한/하한 검사와 품질 증감 로직이 여러 곳에 반복된다. | Duplicated Code | `quality < 50`, `quality > 0`, `quality + 1`, `quality - 1` 패턴이 반복되어 수정 시 실수 가능성이 높다. | `increaseQuality(item)`, `decreaseQuality(item)`, `dropQualityToZero(item)` 같은 헬퍼 메서드로 중복을 제거한다. | 4 |
| 중첩 `if`가 깊고 부정 조건이 많아 흐름을 따라가기 어렵다. | 높은 복잡도, Nested Conditionals, Readability 저하 | Cyclomatic complexity가 대략 18~19 수준이며 cognitive complexity도 높다. 작은 변경도 전체 분기를 다시 검증해야 한다. | 가드 절, 타입별 updater 분리, 명확한 도메인 메서드 도입으로 조건 분기를 줄인다. | 5 |

## Summary

`updateQuality()`의 가장 큰 문제는 아이템별 비즈니스 규칙과 상태 변경 절차가 하나의 메서드에 집중되어 있다는 점이다. 이 구조는 SRP와 OCP를 동시에 위반하며, 새로운 아이템 규칙이 추가될수록 조건문과 중복 코드가 계속 증가한다.

가장 먼저 아이템별 업데이트 전략을 분리하고, 이후 품질 상한/하한 처리와 도메인 상수를 정리하는 순서로 리팩토링하는 것이 효과적이다.
