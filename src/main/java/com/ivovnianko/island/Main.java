package com.ivovnianko.island;


import com.ivovnianko.island.classes.base.Animal;
import com.ivovnianko.island.classes.base.Plant;
import com.ivovnianko.island.classes.base.Printer;
import com.ivovnianko.island.classes.base.Item;
import com.ivovnianko.island.classes.board.Board;
import com.ivovnianko.island.classes.utils.LiveSimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main {
    public static void main(String[] args) throws InterruptedException {

        // Создание симулятора
        LiveSimulator simulator = new LiveSimulator();
        if (!simulator.isReady()) {
            System.out.println("simulator is not ready! error occurred!");
        }

        // Получение доски из симулятора
        Board board = simulator.getBoard();
        // Создание планировщика задач
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        // Запуск принтера для вывода состояния доски с определенной периодичностью
        executorService.scheduleAtFixedRate(new Printer(board), 0, 1, TimeUnit.SECONDS);

        // Получение списков всех живых элементов с доски
        HashMap<String, ArrayList<? extends Item>> itemsMap = board.getItemsMap();
        ArrayList<Animal> predators = (ArrayList<Animal>) itemsMap.get("predators");
        ArrayList<Animal> herbivores = (ArrayList<Animal>) itemsMap.get("herbivores");
        ArrayList<Plant> plants = (ArrayList<Plant>) itemsMap.get("plants");

        int ticks = 0;

        // Основной цикл симуляции
        while (predators.size() > 0 || herbivores.size() > 0) {
            // Запуск процесса роста растений на один такт
            plants = simulator.startPlants(plants);

            // Запуск процесса жизни хищников на один такт
            predators = simulator.startAnimals(predators);

            // Запуск процесса жизни травоядных на один такт
            herbivores = simulator.startAnimals(herbivores);

            // Вывод количества тактов
            System.out.println("\n" + ticks +" second(s) have passed since life on the island began");
            // Задержка для визуализации
            Thread.sleep(1000);
            ticks++;
        }

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        executorService.shutdown();
    }
}