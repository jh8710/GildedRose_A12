# CursorRules 설계보고서

## 1. 아이템 타입별 비즈니스 규칙

| 아이템 타입 | sellIn 변화 | quality 변화 | sellIn 만료 후 규칙 | quality 상한/하한 |
| --- | --- | --- | --- | --- |
| Normal | 매일 1 감소 | 매일 1 감소 | quality가 하루에 2 감소 | 0 이상 50 이하 |
| Aged Brie | 매일 1 감소 | 매일 1 증가 | quality가 하루에 2 증가 | 0 이상 50 이하 |
| Backstage Pass | 매일 1 감소 | sellIn 11일 이상: 1 증가<br>sellIn 10일 이하: 2 증가<br>sellIn 5일 이하: 3 증가 | 콘서트 이후 quality가 0이 됨 | 0 이상 50 이하 |
| Sulfuras | 변하지 않음 | 변하지 않음 | 만료 개념 없음 | 예외적으로 quality 80 유지 |
| Conjured | 매일 1 감소 | Normal 아이템보다 2배 빠르게 감소 | quality가 Normal 만료 후 감소율의 2배로 감소 | 0 이상 50 이하 |

## 2. 예외 및 경계값 조건

1. 모든 일반 아이템의 `quality`는 `0`보다 작아질 수 없다.
2. 모든 일반 아이템의 `quality`는 `50`보다 커질 수 없다.
3. `Sulfuras`는 전설 아이템이므로 `quality`가 `80`이며, `quality`와 `sellIn`이 모두 변하지 않는다.
4. `sellIn`은 음수가 될 수 있다. 만료 후 상태를 표현하기 위해 `sellIn = -1` 이하를 허용해야 한다.
5. `sellIn = 0`은 업데이트 시점에서 만료 경계값이다. 하루 업데이트 후 `sellIn`은 `-1`이 되며, 아이템별 만료 후 규칙이 적용되어야 한다.
6. `sellIn = -1` 또는 더 작은 음수 값은 이미 만료된 상태로 취급한다.
7. `quality = 0`인 감소형 아이템은 업데이트 후에도 `0`을 유지해야 한다.
8. `quality = 50`인 증가형 아이템은 업데이트 후에도 `50`을 초과하면 안 된다.
9. `Backstage Pass`는 콘서트 이후 `quality`가 즉시 `0`이 되어야 하며, 기존 quality 값과 관계없이 0으로 떨어진다.

## 3. Conjured 신규 요구사항 명세

1. `Conjured` 아이템은 새로 추가되는 아이템 카테고리다.
2. `Conjured` 아이템의 `quality`는 Normal 아이템보다 2배 빠르게 감소한다.
3. 판매 기한 전에는 하루에 `quality`가 2 감소한다.
4. 판매 기한이 지난 후에는 Normal 아이템이 하루에 2 감소하므로, `Conjured` 아이템은 하루에 4 감소한다.
5. `Conjured` 아이템도 `quality` 하한 `0`과 상한 `50` 규칙을 따른다.
6. `Conjured` 아이템의 `sellIn`은 Normal 아이템처럼 매일 1 감소하며, 음수가 될 수 있다.

## 4. 테스트해야 할 주요 시나리오

1. Normal 아이템은 하루가 지나면 `sellIn`이 1 감소하고 `quality`가 1 감소한다.
2. Normal 아이템은 `sellIn = 0`에서 업데이트 후 `sellIn = -1`이 되고 `quality`가 2 감소한다.
3. Normal 아이템은 이미 만료된 `sellIn = -1`에서 `quality`가 2 감소한다.
4. Normal 아이템의 `quality = 0`은 업데이트 후에도 0보다 작아지지 않는다.
5. Aged Brie는 하루가 지나면 `sellIn`이 1 감소하고 `quality`가 1 증가한다.
6. Aged Brie는 `sellIn = 0`에서 업데이트 후 `quality`가 2 증가한다.
7. Aged Brie는 `quality = 50`에서 업데이트 후에도 50을 초과하지 않는다.
8. Backstage Pass는 `sellIn >= 11`일 때 `quality`가 1 증가한다.
9. Backstage Pass는 `sellIn = 10`일 때 `quality`가 2 증가한다.
10. Backstage Pass는 `sellIn = 5`일 때 `quality`가 3 증가한다.
11. Backstage Pass는 `sellIn = 0`에서 업데이트 후 콘서트가 지나면 `quality`가 0이 된다.
12. Backstage Pass는 `quality = 50` 근처에서도 증가 후 50을 초과하지 않는다.
13. Sulfuras는 하루가 지나도 `sellIn`과 `quality`가 모두 변하지 않는다.
14. Sulfuras는 `quality = 80`을 유지하며 일반 quality 상한 50 규칙의 예외로 취급된다.
15. Conjured 아이템은 판매 기한 전 하루 업데이트 후 `quality`가 2 감소한다.
16. Conjured 아이템은 `sellIn = 0`에서 업데이트 후 `quality`가 4 감소한다.
17. Conjured 아이템은 `sellIn = -1`에서 업데이트 후 `quality`가 4 감소한다.
18. Conjured 아이템은 `quality = 0` 또는 감소량보다 낮은 quality에서 업데이트 후에도 0보다 작아지지 않는다.
19. 모든 비-Sulfuras 아이템은 업데이트 후 `quality`가 0 이상 50 이하 범위에 있어야 한다.
20. 모든 비-Sulfuras 아이템은 업데이트 후 `sellIn`이 1 감소하며, 음수 값도 허용되어야 한다.
