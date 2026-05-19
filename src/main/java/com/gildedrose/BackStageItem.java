package com.gildedrose;

public class BackStageItem extends GildedRoseItem {

    public BackStageItem(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        if (item.quality < MAX_QUALITY)
            item.quality++;
        if (item.sellIn < 11 && item.quality < MAX_QUALITY)
            item.quality++;
        if (item.sellIn < 6 && item.quality < MAX_QUALITY)
            item.quality++;
        if (item.sellIn < 1)
            item.quality = MIN_QUALITY;
    }

}