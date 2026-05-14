package com.gildedrose;

public class ConjuredItem extends GildedRoseItem {

    public ConjuredItem(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        if (item.quality > 0) {
            if (item.quality - 2 <= 0)
                item.quality = 0;
            else
                item.quality = item.quality - 2;
        }
        if (item.sellIn < 1 && item.quality > 0) {
            if (item.quality - 2 <= 0)
                item.quality = 0;
            else
                item.quality = item.quality - 2;
        }
    }
}
