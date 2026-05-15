package com.gildedrose;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.approvaltests.Approvals;
import com.gildedrose.simulate30Days;

public class GildedRoseTest {

    @Test
    public void foo() {
        Item[] items = new Item[] { new Item("foo", 5, 3) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals("foo", app.items[0].name);
    }

    @Test
    void qualityNeverNegative() {
        Item[] items = {
                new Item("Normal", 5, 0) };
        GildedRose gr = new GildedRose(items);
        gr.updateQuality();
        assertTrue(items[0].quality >= 0);
    }

    @Test
    void agedBrieMax50() {
        Item[] items = {
                new Item("Aged Brie", 5, 50) };
        new GildedRose(items).updateQuality();
        assertTrue(items[0].quality <= 50);
    }

    @Test
    void normalDegradesTwice() {
        Item[] items = {
                new Item("Normal", 0, 10) };
        new GildedRose(items).updateQuality();
        assertEquals(8, items[0].quality);
    }

    @Test
    void nonameItemTest() {
        Item[] items = {
                new Item("noname", 0, 0), new Item("noname", 0, 5) };
        new GildedRose(items).updateQuality();
        assertEquals(-1, items[0].sellIn);
        assertEquals(0, items[0].quality);
        assertEquals(-1, items[1].sellIn);
        assertEquals(3, items[1].quality);
    }

    @Test
    void surfarasTest() {
        Item[] items = {
                new Item("Sulfuras, Hand of Ragnaros", 0, 5), new Item("Sulfuras, Hand of Ragnaros", -1, 5) };
        new GildedRose(items).updateQuality();
        assertEquals(0, items[0].sellIn);
        assertEquals(5, items[0].quality);
        assertEquals(-1, items[1].sellIn);
        assertEquals(5, items[1].quality);
    }

    @Test
    void brieTest() {
        Item[] items = {
                new Item("Aged Brie", 0, 0), new Item("Aged Brie", 0, 50) };
        new GildedRose(items).updateQuality();
        assertEquals(-1, items[0].sellIn);
        assertEquals(2, items[0].quality);
        assertEquals(-1, items[1].sellIn);
        assertEquals(50, items[1].quality);
    }

    @Test
    void backstageTest() {
        Item[] items = {
                new Item("Backstage passes to a TAFKAL80ETC concert",15, 0), 
                new Item("Backstage passes to a TAFKAL80ETC concert", 0, 0), 
                new Item("Backstage passes to a TAFKAL80ETC concert", 0, 51) };
        new GildedRose(items).updateQuality();
        assertEquals(14, items[0].sellIn);
        assertEquals(1, items[0].quality);
        assertEquals(-1, items[1].sellIn);
        assertEquals(0, items[1].quality);
        assertEquals(-1, items[2].sellIn);
        assertEquals(0, items[2].quality);
    }


    void backstageTest(
            int sellIn, int initQ, int exp) {
        Item[] items = { new Item(
                "Backstage passes...",
                sellIn, initQ) };
        new GildedRose(items).updateQuality();
        assertEquals(exp, items[0].quality);
    }

    // 승인 테스트 (한 줄!)
    @Test
    public void thirtyDaySimulation() {
        simulate30Days app = new simulate30Days();
        Item[] items = {
                new Item("+5 Dex Vest", 10, 20),
                new Item("Aged Brie", 2, 0),
                new Item("Sulfuras", 0, 80),
                // ...
        };
        Approvals.verify(
                app.simulate30Days(items));
        // ← .approved.txt와 자동 비교
    }

    // ① Red — 실패 테스트
    @Test
    void conjuredDegradesTwice() {
        Item[] items = {
                new Item("Conjured Mana Cake",
                        10, 20) };
        new GildedRose(items).updateQuality();
        assertEquals(18, items[0].quality);
    }

    @Test
    void conjuredAfterSellDate() {
        Item[] items = {
                new Item("Conjured Mana Cake",
                        0, 10) };
        new GildedRose(items).updateQuality();
        assertEquals(6, items[0].quality);
    }

    @Test
    void degradesTwice() {
        Item[] items = {
                new Item("[F&B] Bread", 5, 20) };
        new GildedRose(items).updateQuality();
        assertEquals(18, items[0].quality);
    }

    @Test
    void qualityNeverNegativeFood() {
        Item[] items = {
                new Item("[F&B] Milk", 0, 1) };
        new GildedRose(items).updateQuality();
        assertEquals(0, items[0].quality);
    }

}
