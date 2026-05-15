package com.gildedrose;

public abstract class GildedRoseItem {

    protected final int MAX_QUALITY = 50;
    protected final int MIN_QUALITY = 0;

    protected Item item;

    public GildedRoseItem(Item item) {
        this.item = item;
    }

    // Template Method
    public abstract void updateQuality();
}
