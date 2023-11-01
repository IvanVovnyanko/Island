package com.ivovnianko.island.classes.utils;

import com.ivovnianko.island.classes.base.Animal;
import com.ivovnianko.island.classes.base.Item;
import com.ivovnianko.island.classes.board.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MoveRules {
    Board board;

    // Метод для связывания с доской
    public void linkBoard(Board board) {
        this.board = board;
    }

    // Методы для получения границ доски
    public int getLeftBoundX() {
        return board.getLeftBorderX();
    }
    public int getRightBoundX() {
        return board.getRightBorderX();
    }
    public int getUpBoundY() {
        return board.getUpBorderY();
    }
    public int getDownBoundY() {
        return board.getDownBorderY();
    }

    // Перечисление для направлений движения
    enum Directions {
        Up, Right, Down, Left
    }

    // Метод для получения начального направления движения
    private Directions getInitialDirectionToStartFrom() {
        int randomInt = new Random().nextInt(0, 100);
        if (randomInt >= 0 && randomInt < 25) {
            return Directions.Up;
        } else if (randomInt >= 25 && randomInt < 50) {
            return Directions.Right;
        } else if (randomInt >= 50 && randomInt < 75) {
            return Directions.Down;
        }
        return Directions.Left;
    }

    // Метод для получения следующего направления по часовой стрелке
    private Directions getNextDirectionClockWise(Directions currDirection) {
        ArrayList<Directions> directionsArrayList = new ArrayList<>(Arrays.asList(Directions.values()));
        int currPosIndex = directionsArrayList.indexOf(currDirection);
        int nextPosIndex = currPosIndex + 1 >= directionsArrayList.size() ? 0 : currPosIndex + 1;
        return directionsArrayList.get(nextPosIndex);
    }

    // Метод для определения, можно ли двигаться в данном направлении
    private boolean canMoveThisDirection(Animal animal, Directions desirableDirection) {
        Coordinate currCoords = animal.getCell().getCoords();
        int currX = currCoords.getX();
        int currY = currCoords.getY();

        if ((desirableDirection == Directions.Up && currY == getUpBoundY()) ||
                (desirableDirection == Directions.Right && currX == getRightBoundX()) ||
                (desirableDirection == Directions.Down && currY == getDownBoundY()) ||
                (desirableDirection == Directions.Left && currX == getLeftBoundX())
        ) {
            return false;
        }

        Board.Cell desirableCell = board.getCellByCoords(getShiftedByDirectionCoords(currCoords, desirableDirection));
        return desirableCell.getSimilarAnimalCount(animal) < animal.getMaxItemsPerCell();
    }

    // Метод для получения сдвинутых координат в зависимости от направления
    private static Coordinate getShiftedByDirectionCoords(Coordinate curr, Directions direction) {
        if (direction == Directions.Up) {
            return new Coordinate(curr.getX(), curr.getY() - 1);
        } else if (direction == Directions.Right) {
            return new Coordinate(curr.getX() + 1, curr.getY());
        } else if (direction == Directions.Down) {
            return new Coordinate(curr.getX(), curr.getY() + 1);
        } else if (direction == Directions.Left) {
            return new Coordinate(curr.getX() - 1, curr.getY());
        } else return curr;
    }

    // Метод для получения следующих координат
    private Coordinate getNextCoords(Animal animal) {
        Coordinate currCoords = animal.getCell().getCoords();
        Directions currDesirableDirection = getInitialDirectionToStartFrom();
        Directions resultDirection = null;

        for (int i = 0; i < Directions.values().length - 1; i++) {
            if (canMoveThisDirection(animal, currDesirableDirection)) {
                resultDirection = currDesirableDirection;
                break;
            } else {
                currDesirableDirection = getNextDirectionClockWise(currDesirableDirection);
            }
        }

        if (resultDirection == null) {
            return currCoords;
        }

        return getShiftedByDirectionCoords(currCoords, resultDirection);
    }

    // Метод для перемещения животного
    public void move(Animal animal) {
        Coordinate animalNextCoords = getNextCoords(animal);
        board.moveSimulationItem(animal, animalNextCoords);
    }

    public void moveByCoords(Item item, Coordinate coords) {
        board.moveSimulationItem(item, coords);
    }
}
