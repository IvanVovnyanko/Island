package com.ivovnianko.island.classes.board;

import com.ivovnianko.island.classes.base.*;
import com.ivovnianko.island.classes.utils.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Board {
    private final int leftBorderX;
    private final int rightBorderX;
    private final int upBorderY;
    private final int downBorderY;
    private final ArrayList<ArrayList<Board.Cell>> scheme = new ArrayList<>();

    public Board(int width, int height) {
        leftBorderX = 0;
        rightBorderX = width - 1;
        upBorderY = 0;
        downBorderY = height - 1;

        for (int i = leftBorderX; i <= rightBorderX; i++) {
            scheme.add(new ArrayList<>());
            for (int j = upBorderY; j <= downBorderY; j++) {
                scheme.get(i).add(new Cell(i, j));
            }
        }

    }

    public int getLeftBorderX() {
        return leftBorderX;
    }

    public int getRightBorderX() {
        return rightBorderX;
    }

    public int getDownBorderY() {
        return downBorderY;
    }

    public int getUpBorderY() {
        return upBorderY;
    }

    public ArrayList<ArrayList<Board.Cell>> getScheme() {
        return scheme;
    }

    public void moveSimulationItem(Item item, Coordinate moveToCoords) {
        Cell itemCurrCell = item.getCell();
        if (itemCurrCell != null) {
            itemCurrCell.removeSimulationItem(item);

        }

        Cell targetCell = scheme.get(moveToCoords.getX()).get(moveToCoords.getY());


        targetCell.addSimulationItem(item);
    }

    public HashMap<String, ArrayList<? extends Item>> getItemsMap() {
        ArrayList<Animal> alivePredators = new ArrayList<>();
        ArrayList<Animal> aliveHerbivores = new ArrayList<>();
        ArrayList<Plant> alivePlants = new ArrayList<>();
        HashMap<String, ArrayList<? extends Item>> map = new HashMap<>() {{
            put("predators", alivePredators);
            put("herbivores", aliveHerbivores);
            put("plants", alivePlants);
        }};

        for (int i = 0; i <= getRightBorderX(); i++) {
            for (int j = 0; j <= getDownBorderY(); j++) {
                Board.Cell cell = getScheme().get(i).get(j);
                alivePredators.addAll(cell.getAllAlivePredators());
                aliveHerbivores.addAll(cell.getAllAliveHerbivores());
                alivePlants.addAll(cell.getAllAlivePlants());
            }
        }
        return map;
    }


    public Cell getCellByCoords(Coordinate coords) {
        return scheme.get(coords.getX()).get(coords.getY());
    }

    public static class Cell {
        private final ArrayList<Item> simulationItems = new ArrayList<>();
        private final Coordinate coords;

        public Cell(int x, int y) {
            coords = new Coordinate(x, y);
        }

        public Coordinate getCoords() {
            return coords;
        }


        public ArrayList<Item> getCurrentSimulationItems() {
            return simulationItems;
        }

        private void addSimulationItem(Item item) {
            simulationItems.add(item);
            item.setCell(this);
        }

        public void removeSimulationItem(Item item) {
            ArrayList<Integer> simulationItemsIds = (ArrayList<Integer>) this.simulationItems.stream()
                    .map(Item::getId)
                    .collect(Collectors.toList());

            int currItemIndex = simulationItemsIds.indexOf(item.getId());

            if (currItemIndex != -1) {
                this.simulationItems.remove(currItemIndex);
                item.setCell(null);
            }
        }

        public ArrayList<Animal> getAllPredators() {
            ArrayList<Animal> predators = new ArrayList<>();
            for (var item : this.getCurrentSimulationItems()) {
                if (item instanceof Predator) {
                    predators.add((Animal) item);
                }
            }
            return predators;
        }

        public ArrayList<Animal> getAllAlivePredators() {
            ArrayList<Animal> predators = getAllPredators();
            return (ArrayList<Animal>) predators
                    .stream()
                    .filter(Animal::isAlive)
                    .collect(Collectors.toList());
        }


        public ArrayList<Animal> getAllHerbivores() {
            ArrayList<Animal> herbivores = new ArrayList<>();
            for (var item : this.getCurrentSimulationItems()) {
                if (item instanceof Herbivore) {
                    herbivores.add((Animal) item);
                }
            }
            return herbivores;
        }

        public ArrayList<Animal> getAllAliveHerbivores() {
            ArrayList<Animal> herbivores = getAllHerbivores();
            return (ArrayList<Animal>) herbivores
                    .stream()
                    .filter(Animal::isAlive)
                    .collect(Collectors.toList());
        }

        public ArrayList<Plant> getAllPlants() {
            ArrayList<Plant> plants = new ArrayList<>();
            for (var item : this.getCurrentSimulationItems()) {
                if (item instanceof Plant) {
                    plants.add((Plant) item);
                }
            }
            return plants;
        }

        public ArrayList<Plant> getAllAlivePlants() {
            ArrayList<Plant> plants = getAllPlants();
            return (ArrayList<Plant>) plants
                    .stream()
                    .filter(Plant::isAlive)
                    .collect(Collectors.toList());
        }

        public boolean hasSimilarAnimal(Animal animal) {
            int similarAnimalsCount = getSimilarAnimalCount(animal);
            return similarAnimalsCount > 0;
        }

        public int getSimilarAnimalCount(Animal animal) {
            Board.Cell currCell = animal.getCell();
            List<Item> similarAnimals = currCell.getCurrentSimulationItems()
                    .stream()
                    .filter(item -> item.getId() != animal.getId() && item.getClass().isInstance(animal))
                    .collect(Collectors.toList());
            return similarAnimals.size();
        }


        @Override
        public String toString() {
            ArrayList<String> simulationItems = (ArrayList<String>) this.simulationItems.stream()
                    .map(item -> item.getImage() + "-id-" + item.getId())
                    .collect(Collectors.toList());

            return "Cell(" + coords.getX() + "," + coords.getY() + "),items:" + simulationItems;
        }
    }
}