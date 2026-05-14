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

    // public void updateQuality() {
    // for (Item item : items) {
    // if(item.name.equals(AGED_BRIE))
    // new AgedBrieItem(item).updateQuality();
    // else if(item.name.equals(BACKSTAGE))
    // new BackStageItem(item).updateQuality();
    // else if(item.name.equals(SULFURAS))
    // new SulfurasItem(item).updateQuality();
    // else if(item.name.equals(CONJURED))
    // new ConjuredItem(item).updateQuality();
    // else
    // new NormalItem(item).updateQuality();
    // updateSellIn(item);
    // }
    // }

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

    // public void updateQuality() {
    //     for (Item item : items) {
    //         GildedRoseItem gi = getItem(item);
    //         gi.updateQuality(); // 다형성!
    //         updateSellIn(item);
    //     }
    // }

    // private void updateAgedBrie(Item item){
    // if(item.quality < 50) item.quality++;
    // if(item.sellIn < 1)
    // if(item.quality < 50) item.quality++;
    // }

    // private void updateBackstage(Item item){
    // if(item.quality < 50) item.quality++;
    // if(item.sellIn<11 && item.quality<50) item.quality++;
    // if(item.sellIn<6 && item.quality<50) item.quality++;
    // if(item.sellIn<1) item.quality = 0;

    // }
    // private void updateSulfuras(Item item){}

    // private void updateNormalItem(Item item){
    // if(item.quality > 0) item.quality--;
    // if(item.sellIn<1 && item.quality>0)
    // item.quality--;
    // }
    private void updateSellIn(Item item) {
        if (!item.name.equals(SULFURAS))
            item.sellIn--;
    }

    // private void updateConjured(Item item){
    // if(item.quality > 0) {
    // if(item.quality - 2 <= 0) item.quality = 0;
    // else item.quality = item.quality-2;
    // }
    // if(item.sellIn<1 && item.quality>0) {
    // if(item.quality - 2 <= 0) item.quality = 0;
    // else item.quality = item.quality-2;
    // }
    // }

    // public void updateQuality() {
    // for (int i = 0; i < items.length; i++) {
    // Item item = items[i];
    // if (!item.name.equals(AGED_BRIE)
    // && !item.name.equals(BACKSTAGE)) {
    // if (item.quality > 0) {
    // if (!item.name.equals(SULFURAS)) {
    // if(item.name.equals(CONJURED)){
    // if(item.quality - 2 <= 0){
    // item.quality = 0;
    // }
    // else
    // item.quality = iitem.quality - 2;
    // }
    // else
    // item.quality = item.quality - 1;
    // }
    // }
    // } else {
    // if (item.quality < 50) {
    // item.quality = item.quality + 1;

    // if (item.name.equals(BACKSTAGE)) {
    // if (item.sellIn < 11) {
    // if (item.quality < 50) {
    // item.quality = item.quality + 1;
    // }
    // }

    // if (item.sellIn < 6) {
    // if (item.quality < 50) {
    // item.quality = item.quality + 1;
    // }
    // }
    // }
    // }
    // }

    // if (!item.name.equals(SULFURAS)) {
    // item.sellIn = item.sellIn - 1;
    // }

    // if (item.sellIn < 0) {
    // if (!item.name.equals(AGED_BRIE)) {
    // if (!item.name.equals(BACKSTAGE)) {
    // if (item.quality > 0) {
    // if (!item.name.equals(SULFURAS)) {
    // if(item.name.equals(CONJURED)){
    // if(item.quality - 2 < 0) {
    // item.quality = 0;
    // }
    // else
    // item.quality = item.quality - 2;
    // }
    // else
    // item.quality = item.quality - 1;
    // }
    // }
    // } else {
    // item.quality = item.quality - item.quality;
    // }
    // } else {
    // if (item.quality < 50) {
    // item.quality = item.quality + 1;
    // }
    // }
    // }
    // }
    // }
}