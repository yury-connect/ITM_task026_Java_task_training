package com.example.demo._2024_07_04_Yury_Senla.task6.courses.io;

import com.example.demo._2024_07_04_Yury_Senla.task6.courses.beans.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public final class InRandomProcessor implements InputProcessor {

    private final static Random RANDOM = new Random();
    private final static String NAME_HEADER = "Thing № ";


    public InRandomProcessor() {
    }


    private static final int MIN_CAPACITY = 50;   // kg.
    private static final int MAX_CAPACITY = 80;   // kg.
    @Override
    public Backpack getBackpack() {
        Backpack backpack = new Backpack(MIN_CAPACITY
                + RANDOM.nextInt(MAX_CAPACITY - MIN_CAPACITY));
        return backpack;
    }


    private static final int MIN_NUMBER_THINGS = 10;   // things.
    private static final int MAX_NUMBER_THINGS = 100;   // things.
    private static final int MIN_WEIGHT = 10;   // kg.
    private static final int MAX_WEIGHT = 50;   // kg.
    private static final int MIN_COST = 10;   // cent.
    private static final int MAX_COST = 100;   // cent.
    @Override
    public List<Thing> getThings() {
        int numThings = MIN_NUMBER_THINGS
                + RANDOM.nextInt(MAX_NUMBER_THINGS - MIN_NUMBER_THINGS);
        List<Thing> thingList = new ArrayList<>(numThings);
        for (int i = 1; i <= numThings; i++) {
            String name = NAME_HEADER + i;
            int weight = MIN_WEIGHT + RANDOM.nextInt(MAX_WEIGHT - MIN_WEIGHT);
            int cost = MIN_COST + RANDOM.nextInt(MAX_COST - MIN_COST);
            Thing thing = new Thing(name, weight, cost);
            thingList.add(thing);
        }
        return thingList;
    }

}
