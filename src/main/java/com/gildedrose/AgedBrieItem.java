package com.gildedrose;

public class AgedBrieItem extends GildedRoseItem {
    
    public AgedBrieItem(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        if (item.quality < MAX_QUALITY)
            item.quality++;
        if (item.sellIn < 1)
            if (item.quality < MAX_QUALITY)
                item.quality++;
    }
}