package com.gildedrose;

class GildedRose {

    private static final String AGED_BRIE = "Aged Brie";
    private static final String BACKSTAGE = "Backstage passes to a TAFKAL80ETC concert";
    private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";
    private static final String CONJURED = "Conjured Mana Cake";
    private static final int MAX_QUALITY = 50;

    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    private GildedRoseItem getItem(Item item) {
        if (item.name.equals(AGED_BRIE))
            return new AgedBrieItem(item);
        if (item.name.equals(BACKSTAGE))
            return new BackStageItem(item);
        if (item.name.equals(SULFURAS))
            return new SulfurasItem(item);
        if (item.name.equals(CONJURED))
            return new ConjuredItem(item);
        if (item.name.contains("[F&B]"))
            return new FoodBeverageItem(item);
        return new NormalItem(item);

    }

    public void updateQuality() {
        for (Item item : items) {
            updateQuality(item);
            updateSellIn(item);
        }
    }

    private void updateQuality(Item item) {
        GildedRoseItem gi = getItem(item);
        gi.updateQuality();
    }

    private void updateSellIn(Item item) {
        if (!item.name.equals(SULFURAS))
            item.sellIn--;
    }

}