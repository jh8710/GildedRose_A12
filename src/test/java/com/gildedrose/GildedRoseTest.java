package com.gildedrose;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GildedRoseTest {

    @DisplayName("Normal 아이템은 판매 기한과 품질 경계값에 따라 품질이 감소한다")
    @ParameterizedTest(name = "[{index}] sellIn={0}, quality={1} -> sellIn={2}, quality={3}")
    @CsvSource({
            "10, 20, 9, 19",
            "1, 1, 0, 0",
            "0, 10, -1, 8",
            "-1, 10, -2, 8",
            "5, 0, 4, 0",
            "0, 1, -1, 0",
            "3, 50, 2, 49"
    })
    void normalItemDecreasesQuality(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
        // Given
        Item item = new Item("+5 Dexterity Vest", sellIn, quality);

        // When
        updateQuality(item);

        // Then
        assertItem(item, "+5 Dexterity Vest", expectedSellIn, expectedQuality);
    }

    @DisplayName("Aged Brie는 판매 기한과 품질 경계값에 따라 품질이 증가한다")
    @ParameterizedTest(name = "[{index}] sellIn={0}, quality={1} -> sellIn={2}, quality={3}")
    @CsvSource({
            "10, 20, 9, 21",
            "1, 49, 0, 50",
            "0, 20, -1, 22",
            "-1, 20, -2, 22",
            "5, 50, 4, 50",
            "0, 49, -1, 50"
    })
    void agedBrieIncreasesQuality(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
        // Given
        Item item = new Item("Aged Brie", sellIn, quality);

        // When
        updateQuality(item);

        // Then
        assertItem(item, "Aged Brie", expectedSellIn, expectedQuality);
    }

    @DisplayName("Backstage Pass는 공연일 접근과 경계값에 따라 품질이 증가하거나 0이 된다")
    @ParameterizedTest(name = "[{index}] sellIn={0}, quality={1} -> sellIn={2}, quality={3}")
    @CsvSource({
            "11, 20, 10, 21",
            "10, 20, 9, 22",
            "6, 20, 5, 22",
            "5, 20, 4, 23",
            "1, 20, 0, 23",
            "0, 20, -1, 0",
            "-1, 20, -2, 0",
            "5, 49, 4, 50",
            "10, 50, 9, 50"
    })
    void backstagePassUpdatesByConcertDate(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
        // Given
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", sellIn, quality);

        // When
        updateQuality(item);

        // Then
        assertItem(item, "Backstage passes to a TAFKAL80ETC concert", expectedSellIn, expectedQuality);
    }

    @DisplayName("Sulfuras는 판매 기한과 품질 경계값에서도 값이 변하지 않는다")
    @ParameterizedTest(name = "[{index}] sellIn={0}, quality={1}")
    @CsvSource({
            "10, 80",
            "1, 80",
            "0, 80",
            "-1, 80",
            "5, 50"
    })
    void sulfurasNeverChanges(int sellIn, int quality) {
        // Given
        Item item = new Item("Sulfuras, Hand of Ragnaros", sellIn, quality);

        // When
        updateQuality(item);

        // Then
        assertItem(item, "Sulfuras, Hand of Ragnaros", sellIn, quality);
    }

    @DisplayName("Conjured 아이템은 Normal보다 두 배 빠르게 품질이 감소한다")
    @ParameterizedTest(name = "[{index}] sellIn={0}, quality={1} -> sellIn={2}, quality={3}")
    @CsvSource({
            "10, 20, 9, 18",
            "1, 2, 0, 0",
            "0, 20, -1, 16",
            "-1, 20, -2, 16",
            "5, 0, 4, 0",
            "0, 3, -1, 0",
            "3, 50, 2, 48"
    })
    void conjuredItemDegradesTwiceAsFast(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
        // Given
        Item item = new Item("Conjured Mana Cake", sellIn, quality);

        // When
        updateQuality(item);

        // Then
        assertItem(item, "Conjured Mana Cake", expectedSellIn, expectedQuality);
    }

    private void updateQuality(Item item) {
        Item[] items = new Item[] { item };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
    }

    private void assertItem(Item item, String expectedName, int expectedSellIn, int expectedQuality) {
        assertEquals(expectedName, item.name);
        assertEquals(expectedSellIn, item.sellIn);
        assertEquals(expectedQuality, item.quality);
    }

}
