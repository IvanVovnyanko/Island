package com.ivovnianko.island.classes.base;

import com.ivovnianko.island.classes.board.Board;
import com.ivovnianko.island.classes.utils.EatingManager;
import com.ivovnianko.island.classes.utils.MoveRules;

import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class Animal extends Item {

    private int anInt = 0; // Текущее количество шагов за цикл
    private double currSaturationAmountByTick = 0; // Текущее количество питательности за цикл

    // Абстрактный метод для получения количества тактов, которое животное может прожить без насыщения
    abstract public int getHowMuchTickCouldLiveWithoutSaturation();

    // Абстрактный метод для уменьшения количества тактов, которое животное может прожить без насыщения
    abstract public void decrementHowMuchTickCouldLiveWithoutSaturation();

    // Абстрактный метод для получения количества шагов в клетке за цикл
    abstract public int getCellMovesPerCycle();

    // Абстрактный метод для получения уровня насыщенности
    abstract public double getSaturationAmount();

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int count) {
        anInt = count;
    }

    // Увеличивает текущее количество шагов за цикл на единицу
    public void incrementCurrMovesByTick() {
        anInt++;
    }

    public double getCurrSaturationAmountByTick() {
        return currSaturationAmountByTick;
    }

    public void setCurrSaturationAmountByTick(double amount) {
        currSaturationAmountByTick = amount;
    }

    // Метод для питания животного
    public boolean eat() {
        boolean saturation = false;
        EatingManager eatingManager = new EatingManager();
        Board.Cell currCell = this.getCell();
        ArrayList<Item> allItemsOnCell = currCell.getCurrentSimulationItems();
        ArrayList<String> possibleVictimsClassNames = eatingManager.getVictimClassNamesByAttackerName(this.getClass().getName());

        ArrayList<Item> victims = (ArrayList<Item>) allItemsOnCell
                .stream()
                .filter(item -> possibleVictimsClassNames.contains(item.getClass().getName()))
                .collect(Collectors.toList());


        for (int i = 0; i < victims.size(); i++) {
            Item victim = victims.get(i);
            if (!victim.isAlive()) {
                continue;
            }
            double victimWeight = victim.getWeight();
            double currSaturationAmount = this.getCurrSaturationAmountByTick();
            if (currSaturationAmount != 0 && currSaturationAmount >= this.getSaturationAmount()) {
                return true;
            }

            if (eatingManager.isAttackerEatsVictim(this, victim)) {
                this.setCurrSaturationAmountByTick(currSaturationAmount + victimWeight);
                victim.die();
            }
        }


        return saturation;
    }

    // Метод для передвижения животного
    public boolean move(MoveRules movementManager) {
        boolean canMove = this.getAnInt() < getCellMovesPerCycle();
        if (!canMove) {
            return false;
        }

        movementManager.move(this);
        this.incrementCurrMovesByTick();
        return true;
    }

    // Метод для размножения животного
    public Animal reproduce() {
        try {
            return factory.createAnimalByType(this.getClass());
        } catch (Exception ignored) {
        }
        return null;

    }

    public void resetMovesAndSaturation() {
        setAnInt(0);
        setCurrSaturationAmountByTick(0);
    }
}
