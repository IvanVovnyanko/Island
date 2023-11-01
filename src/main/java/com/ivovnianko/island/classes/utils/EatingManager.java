package com.ivovnianko.island.classes.utils;

import com.ivovnianko.island.classes.base.Animal;
import com.ivovnianko.island.classes.base.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class EatingManager {
    // Хранит карту шансов быть съеденным
    private final HashMap<String, HashMap<String, Integer>> chanceToBeEatenMap = ChanceToBeEatenConfig.getConfig();

    // Метод для определения, съедает ли атакующий объект жертву или нет
    public boolean isAttackerEatsVictim(Animal attacker, Item victim) {
        String attackerClassName = attacker.getClass().getName();

        if (!chanceToBeEatenMap.containsKey(attackerClassName)) {
            return false;
        }

        HashMap<String, Integer> attackersPossibleVictimsMap = chanceToBeEatenMap.get(attackerClassName);
        String victimClassName = victim.getClass().getName();

        if (!attackersPossibleVictimsMap.containsKey(victimClassName)) {
            return false;
        }

        int randomChanceToEatVictim = new Random().nextInt(0, 101);
        int configurationChanceToEatVictim = attackersPossibleVictimsMap.get(victimClassName);
        int resultChanceToEatVictim = randomChanceToEatVictim + configurationChanceToEatVictim;
        int randomChanceNotToBeEatenByAttacker = new Random().nextInt(0, 101);
        int resultChanceNotToBeEatenByAttacker = randomChanceNotToBeEatenByAttacker + (100 - configurationChanceToEatVictim);


        return resultChanceToEatVictim >= resultChanceNotToBeEatenByAttacker;
    }

    // Метод для получения имен классов жертв по имени атакующего класса
    public ArrayList<String> getVictimClassNamesByAttackerName(String attackerClassName) {
        ArrayList<String> victims = new ArrayList<>();

        if (!chanceToBeEatenMap.containsKey(attackerClassName)) {
            return victims;
        }

        victims.addAll(chanceToBeEatenMap.get(attackerClassName).keySet());

        return victims;
    }
}
