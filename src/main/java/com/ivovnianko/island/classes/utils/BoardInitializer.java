package com.ivovnianko.island.classes.utils;

import com.ivovnianko.island.classes.base.*;
import com.ivovnianko.island.classes.board.Board;
import com.ivovnianko.island.classes.herbivores.Caterpillar;
import com.ivovnianko.island.classes.plants.Grass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BoardInitializer {

    // Метод для получения случайного количества элементов для создания
    public int getRandomItemsCreate(Item simulationItem, Random random) {
        int itemMaxItemsPerCell = simulationItem.getMaxItemsPerCell();

        if (simulationItem instanceof Caterpillar) {
            return random.nextInt(1, 1000);
        }

        if (simulationItem instanceof Grass) {
            return random.nextInt(1, itemMaxItemsPerCell);
        }

        if (simulationItem instanceof Predator) {
            return random.nextInt(itemMaxItemsPerCell, itemMaxItemsPerCell + 1);
        }

        if (simulationItem instanceof Herbivore) {
            return random.nextInt(itemMaxItemsPerCell / 2, itemMaxItemsPerCell * 10);
        }
        return itemMaxItemsPerCell;
    }

    // Метод для случайного перемешивания элементов на доске
    public void randomlyShuffleItemsOnBoard(
            MoveRules movementManagerUtils,
            ArrayList<? extends Item> simulationItems
    ) {
        Random random = new Random();
        Board board = movementManagerUtils.board;
        simulationItems.forEach(item -> {
            Coordinate coords = new Coordinate(
                    random.nextInt(0, board.getRightBorderX() + 1),
                    random.nextInt(0, board.getDownBorderY() + 1)
            );

            movementManagerUtils.moveByCoords(item, coords);
        });
        for (Item item : simulationItems) {

            for (int i = 0; i < 10; i++) {
                Coordinate possibleCoords = new Coordinate(
                        random.nextInt(0, board.getRightBorderX() + 1),
                        random.nextInt(0, board.getDownBorderY() + 1)
                );
                Board.Cell possibleCell = board.getCellByCoords(possibleCoords);
                List<Item> similarItems = possibleCell.getCurrentSimulationItems()
                        .stream()
                        .filter(curr -> curr.getId() != item.getId())
                        .collect(Collectors.toList());

                boolean limitIsNotReached = item.getMaxItemsPerCell() > similarItems.size();
                if (limitIsNotReached) {
                    movementManagerUtils.moveByCoords(item, possibleCoords);
                    break;
                }
            }
        }
    }


    // Метод для инициализации доски
    public boolean init(MoveRules movementManagerUtils) {
        ArrayList<Item> simulationItems = new ArrayList<>();
        SimulationClassesLoader simulationClassesLoader = new SimulationClassesLoader();
        SimulationItemsFactory simulationItemsFactory = new SimulationItemsFactory();
        Random random = new Random();

        ArrayList<Item> simulationClasses = simulationClassesLoader.getLoadedClasses();
        try {
            for (int i = 0; i < simulationClasses.size(); i++) {
                Item simulationItem = simulationClasses.get(i);
                int countOfItemsToCreate = getRandomItemsCreate(simulationItem, random);

                if (simulationItem instanceof Animal) {
                    Class animalClazz = simulationItem.getClass();
                    ArrayList<Animal> animals = simulationItemsFactory.createAnimalsByType(animalClazz, countOfItemsToCreate);
                    simulationItems.addAll(animals);
                } else if (simulationItem instanceof Plant) {
                    Class plantClazz = simulationItem.getClass();
                    ArrayList<Plant> plants = simulationItemsFactory.createPlantsByType(plantClazz, countOfItemsToCreate);
                    simulationItems.addAll(plants);
                }
            }
        } catch (Exception e) {
            System.out.println("Board init fail\\n" + Arrays.toString(e.getStackTrace()));
            return false;
        }

        randomlyShuffleItemsOnBoard(movementManagerUtils, simulationItems);

        return true;
    }
}
