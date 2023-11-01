package com.ivovnianko.island.classes.plants;

import com.ivovnianko.island.classes.base.Plant;

public class Grass extends Plant {
    private final String image = "\uD83C\uDF31";
    private final double weight = 1;
    private final int maxItemsPerCell = 200;

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public int getMaxItemsPerCell() {
        return maxItemsPerCell;
    }
}
