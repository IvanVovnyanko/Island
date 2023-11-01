package com.ivovnianko.island.classes.utils;

import com.ivovnianko.island.classes.base.Animal;
import com.ivovnianko.island.classes.base.Plant;
import com.ivovnianko.island.classes.board.Board;
import com.ivovnianko.island.classes.plants.Grass;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;
public class LiveSimulator { // Класс для симуляции жизни на острове
    private final Board board = new Board(100, 20); // Создание игровой доски
    private final BoardInitializer boardInitializer = new BoardInitializer(); // Инициализатор доски
    MoveRules movementManager = new MoveRules(); // Менеджер движения
    private boolean ready; // Флаг готовности

    public LiveSimulator() {
        movementManager.linkBoard(board); // Связывание менеджера движения с доской

        try {
            boardInitializer.init(movementManager);
            ready = true; // Отметка о готовности
            System.out.println("Board inited and ready for simulation");
        } catch (Exception e) {
            System.out.println("Board init failed");
        }
    }

    // Метод для проверки готовности симулятора
    public boolean isReady() {
        return ready;
    }

    // Метод для получения игровой доски
    public Board getBoard() {
        return board;
    }

    // Метод для получения менеджера движения
    public MoveRules getMovementManager() {
        return movementManager;
    }

    // Метод для получения инициализатора доски
    public BoardInitializer getBoardInitializer() {
        return boardInitializer;
    }

    // Метод для выполнения одного шага симуляции для списка животных
    public ArrayList<Animal> startAnimals(ArrayList<Animal> initialAnimals) {
        ArrayList<Animal> newlyBornAnimals = new ArrayList<>();

        for (Animal animal : initialAnimals) {
            boolean canMove = true;
            boolean saturationReached = false;

            if (!animal.isAlive()) {
                continue;
            }

            do {
                canMove = animal.move(movementManager);
                saturationReached = animal.eat();

            }
            while (canMove && !saturationReached);

            if (!canMove && !saturationReached) {
                if (animal.getHowMuchTickCouldLiveWithoutSaturation() > 0) {
                    animal.decrementHowMuchTickCouldLiveWithoutSaturation();
                    continue;
                }
                animal.die();
                continue;
            }

            if (saturationReached) {
                animal.resetMovesAndSaturation();
                boolean hasSimilarAnimal = animal.getCell().hasSimilarAnimal(animal);
                boolean limitIsNotReached = animal.getMaxItemsPerCell() > animal.getCell().getSimilarAnimalCount(animal);

                if (hasSimilarAnimal && limitIsNotReached) {
                    Animal newAnimal = animal.reproduce();
                    if (newAnimal != null) {
                        newlyBornAnimals.add(newAnimal);
                        movementManager.moveByCoords(newAnimal, animal.getCell().getCoords());
                    }
                }

            }

        }

        ArrayList<Animal> survivedAnimals = (ArrayList<Animal>) initialAnimals
                .stream()
                .filter(Animal::isAlive)
                .collect(Collectors.toList());

        survivedAnimals.addAll(newlyBornAnimals);

        return survivedAnimals;
    }

    // Метод для выполнения одного шага симуляции для списка растений
    public ArrayList<Plant> startPlants(ArrayList<Plant> initialPlants) {
        ArrayList<Plant> newlyGrowPlants = new ArrayList<>();
        ArrayList<Plant> survivedPlants = (ArrayList<Plant>) initialPlants
                .stream()
                .filter(Plant::isAlive)
                .collect(Collectors.toList());

        Plant plant = new Grass();
        Class plantClazz = plant.getClass();
        Random random = new Random();

        int countOfItemsToCreate = boardInitializer.getRandomItemsCreate(plant, random);
        try {
            newlyGrowPlants = plant.factory.createPlantsByType(plantClazz, countOfItemsToCreate);
        } catch (Exception e) {
            System.out.println("Could not grow plants");
        }


        boardInitializer.randomlyShuffleItemsOnBoard(movementManager, newlyGrowPlants);


        survivedPlants.addAll(newlyGrowPlants);
        return survivedPlants;


    }
}