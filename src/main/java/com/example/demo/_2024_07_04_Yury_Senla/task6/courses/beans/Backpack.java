package com.example.demo._2024_07_04_Yury_Senla.task6.courses.beans;

import java.util.ArrayList;
import java.util.List;


public final class Backpack {

    private int p;   // payload -> грузоподъемность рюкзака, kg.;
    private int n;   // number of things -> количество переданных предметов, шт.;
    private int[][] costTable;   // динамическй массив [n=количества X p=масса], в кот. сумарная ст-ть унес-х предметов;

    private String[] names;   // наименования предметов;
    private int[] m;   // mass -> веса предметов из базового набора, kg. ;
    private int[] c;   // cost -> ценности предметов из базового набора, cent. ;

    private List<Thing> resultSet;   // набор предметов, которые попали в рюкзак;
    private int totalCost;   // сумарная ст-ть набора предметов в рюкзаке;
    private int totalMass;   // сумарная масса набора предметов в рюкзаке;


    public Backpack(int payload) {
        this.p = payload;
        this.resultSet = new ArrayList<>();
    }


    public int getPayload() {
        return p;
    }

    public List<Thing> getResultSet() {
        return resultSet;
    }

    public int getTotalMass() {
        return totalMass;
    }

    public int getTotalCost() {
        return totalCost;
    }


    // Решим задачу методом динамического программирования
    public void fillBackpack(List<Thing> things) {
        thingsToArrays(things);
        this.costTable = new int[n+1][p+1];   // n -> number, p -> payload;
        for (int i = 1; i <= n; i++) {   // Перебор по предметам, i -> кол-во предметов (первых) в базовом наборе;
            for (int j = 1; j <= p; j++) {   // Перебор по массе рюкзака, j -> максимум возможной суммарной массы уносимых предметов;
                int costNotToTake = costTable[i-1][j];   // Если не берем  предмет i, тогда стоимость рюкзака остается прежней;
                int costToTake = 0;   // и соответственно ст-ть варианта "берем" = 0;
                if (m[i-1] <= j) {   //  Если предмет i подходит по массе в оставшееся свободное место в рюкзаке, то расмотрим его;
                    costToTake = costTable[i-1][j-m[i-1]] + c[i-1]; // Если берем, то ст-ть рюкз. = (ст-ть рюкзака без предмета) + (ст-ть предмета);
                }
                costTable[i][j] = Math.max(costNotToTake, costToTake);   // Возмем лучший из вариантов;
//                System.out.print(costTable[i][j] + "\t");   // Выведем динамич. массив: Сумар-я ст-ть унес-х предм. [кол-во X масса];
            }
            System.out.println();
        }
        this.totalCost = costTable[n][p];   // Суммарная стоимость оптиального набора будет в последней ячейке;

        // Построение списка уносимых предметов  (будем формировать "с конца");
        for (int i = n, j = p; i > 0; i--) {
            if (costTable[i][j] != 0   &&
                    costTable[i][j] != costTable[i - 1][j]) {   // Если знач-я равны с преведущим, то можно набрать ту же стоимость без последнего предм.;
                resultSet.add(new Thing(names[i-1], m[i-1], c[i-1]));   // Если знач-я с преведущим разние, то обязательно добавляем;
                totalMass += m[i-1];   // Увелич. сумарную массу набора предметов;
                j -= m[i-1];
            }
        }

    }

    
    private void thingsToArrays(List<Thing> things) {
        this.n = things.size();
        names = new String[n];
        m = new int[n + 1];
        c = new int[n + 1];
        for (int i = 0; i < n; i++) {
            this.names[i] = things.get(i).getName();
            this.m[i] = things.get(i).getWeight();
            this.c[i] = things.get(i).getCost();
        }
    }

}
