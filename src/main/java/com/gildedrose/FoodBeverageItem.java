package com.gildedrose;

public class FoodBeverageItem extends GildedRoseItem {

    public FoodBeverageItem(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        if (item.quality > 0)

        {
            item.quality--;
            if (item.quality > 0)
                item.quality--;
        }
        // sellIn 지나면 추가 2배
        if (item.sellIn < 1) {
            if (item.quality > 0) {
                item.quality--;
                if (item.quality > 0)
                    item.quality--;
            }
        }
    }

}