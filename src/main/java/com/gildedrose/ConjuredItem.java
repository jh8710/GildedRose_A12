package com.gildedrose;

public class ConjuredItem extends GildedRoseItem {

    public ConjuredItem(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        if (item.quality > MIN_QUALITY) {
            if (item.quality - 2 <= MIN_QUALITY)
                item.quality = MIN_QUALITY;
            else
                item.quality = item.quality - 2;
        }
        if (item.sellIn < 1 && item.quality > MIN_QUALITY) {
            if (item.quality - 2 <= MIN_QUALITY)
                item.quality = MIN_QUALITY;
            else
                item.quality = item.quality - 2;
        }
    }
}
