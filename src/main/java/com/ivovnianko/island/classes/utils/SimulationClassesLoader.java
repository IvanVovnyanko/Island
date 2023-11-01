package com.ivovnianko.island.classes.utils;

import com.ivovnianko.island.classes.base.*;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static java.util.Arrays.asList;

public class SimulationClassesLoader {

    private final Path pathToUserDirectory = Path.of(System.getProperty("user.dir"));
    private final Path pathToClasses = pathToUserDirectory.resolve("src/main/java/com/ivovnianko/island/classes");
    private final String classesPackageName = "com.ivovnianko.island.classes";
    private final ArrayList<String> classesToLoadDirNames = new ArrayList<>(asList("predators", "plants", "herbivores"));


    // Метод для получения загруженных классов
    public ArrayList<Item> getLoadedClasses() {
        ArrayList<Item> loadedClasses = new ArrayList<>();
        SimulationItemsFactory simulationItemsFactory = new SimulationItemsFactory();

        // Перебор всех директорий для загрузки классов
        classesToLoadDirNames.forEach(dirName -> {
            Path pathToLoadClassDir = pathToClasses.resolve(dirName);
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(pathToLoadClassDir)) {
                for (Path path : directoryStream) {
                    if (Files.isRegularFile(path)) {
                        String className = path.getFileName().toString().split(".java")[0];
                        String fullClassName = classesPackageName + "." + dirName + "." + className;
                        Class clazz = Class.forName(fullClassName);
                        Class clazzSuperclassName = clazz.getSuperclass();

                        // Создание экземпляра класса в зависимости от его родительского класса
                        Item simulationItemInstance = null;
                        if (clazzSuperclassName.equals(Predator.class) || clazzSuperclassName.equals(Herbivore.class)) {
                            simulationItemInstance = simulationItemsFactory.createAnimalByType(clazz);
                        } else if (clazzSuperclassName.equals(Plant.class)) {
                            simulationItemInstance = simulationItemsFactory.createPlantByType(clazz);
                        }

                        // Добавление загруженного экземпляра класса в список
                        loadedClasses.add(simulationItemInstance);
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not load classes from directory " + dirName);
            }
        });

        return loadedClasses;
    }
}
