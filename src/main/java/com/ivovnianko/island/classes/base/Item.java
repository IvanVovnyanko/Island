package com.ivovnianko.island.classes.base;

import com.ivovnianko.island.classes.board.Board;
import com.ivovnianko.island.classes.utils.Coordinate;
import com.ivovnianko.island.classes.utils.SimulationItemsFactory;

public abstract class Item {
    private Board.Cell currentCell;
    private static int globalId = 0;
    private int id;
    public static SimulationItemsFactory factory = new SimulationItemsFactory();
    public boolean died = false;
    public Coordinate coordinate;

    abstract public String getImage();

    abstract public double getWeight();

    abstract public int getMaxItemsPerCell();

    public int getId() {
        return id;
    }

    public Item() {
        id = globalId++;
    }

    public Board.Cell getCell() {
        return currentCell;
    }

    public void setCell(Board.Cell cell) {
        currentCell = cell;
    }

    public void die() {
        Board.Cell currCell = this.getCell();
        currCell.removeSimulationItem(this);
    }

    public boolean isAlive() {
        return this.getCell() != null;
    }
}
