package com.ivovnianko.island.classes.base;

import com.ivovnianko.island.classes.board.Board;

import java.util.ArrayList;
import java.util.HashMap;

public class Printer implements Runnable {
    Board board;
    boolean start;

    public Printer(Board board) {
        this.board = board;
    }

    public void getStatistics() {
        HashMap<String, ArrayList<? extends Item>> allAliveItemsMap = board.getItemsMap();
        ArrayList<Animal> predators = (ArrayList<Animal>) allAliveItemsMap.get("predators");
        ArrayList<Animal> herbivores = (ArrayList<Animal>) allAliveItemsMap.get("herbivores");
        ArrayList<Plant> plants = (ArrayList<Plant>) allAliveItemsMap.get("plants");

        if (!start) {
            System.out.println("*************************************************");
            System.out.println("Starting number of animals, herbivores count: " + herbivores.size());
            System.out.println("Starting number of animals, predators count: " + predators.size());
            System.out.println("Starting number of plants, grass count: " + plants.size());
            System.out.println("*************************************************");
            start = true;
        }

        System.out.println("*************************************************");
        System.out.println("There are some animals left on the island - herbivores count: " + herbivores.size());
        System.out.println("There are some animals left on the island - predators count: " + predators.size());
        System.out.println("There are some plants left on the island - grass count: " + plants.size());
        System.out.println("*************************************************");
    }

    @Override
    public void run() {
        getStatistics();
    }
}
