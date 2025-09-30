package com.example.demo._2024_07_04_Yury_Senla.task6.courses.beans;

public final class Thing {

    private String name;
    private int weight;   // kg.
    private int cost;   // cent.


    public Thing(String name, int weight, int cost) {
        this.name = name;
        this.weight = weight;
        this.cost = cost;
    }


    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public int getCost() {
        return cost;
    }


    @Override
    public String toString() {
        String delimiter = ";";
        return name + delimiter + weight + delimiter + cost;
    }

}
