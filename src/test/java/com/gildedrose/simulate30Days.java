package com.gildedrose;

public class simulate30Days {
    
public String simulate30Days(Item[] startItems) {
GildedRose gr = new GildedRose(startItems);
StringBuilder sb = new StringBuilder();
for (int d = 0; d <= 30; d++) {
sb.append("--- day ").append(d).append(" --");

for (Item i : gr.items)
sb.append(i.name).append(", ")
.append(i.sellIn).append(", ")
.append(i.quality).append("");

if (d < 30) gr.updateQuality();
}
return sb.toString();
}

}
