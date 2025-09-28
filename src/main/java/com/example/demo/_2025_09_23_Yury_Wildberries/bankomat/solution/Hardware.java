package com.example.demo._2025_09_23_Yury_Wildberries.bankomat.solution;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Интерфейс для взаимодействия с аппаратной частью банкомата.
 */
interface Hardware {
    /**
     * Текущее количество купюр, заряженное в банкомате на данный момент.
     * Возвращает массив с количеством купюр по номиналам 50, 100, 500, 1000, 5000.
     * Метод работает медленно и создает шум.
     *
     * @return массив, где каждый элемент соответствует количеству купюр определенного номинала.
     *         Например, [10, 20, 30, 40, 50] означает:
     *         - 10 купюр номиналом 50 рублей
     *         - 20 купюр номиналом 100 рублей
     *         - 30 купюр номиналом 500 рублей
     *         - 40 купюр номиналом 1000 рублей
     *         - 50 купюр номиналом 5000 рублей
     */
    int[] getBillsCounts();

    /**
     * Загружает в бокс выдачи указанные купюры // выдает пользователю деньги.
     *
     * @param billsCounts массив с количеством купюр по номиналам [50, 100, 500, 1000, 5000].
     *                    Например, [0, 1, 0, 2, 0] означает:
     *                    - 0 купюр номиналом 50 рублей
     *                    - 1 купюру номиналом 100 рублей
     *                    - 0 купюр номиналом 500 рублей
     *                    - 2 купюры номиналом 1000 рублей
     *                    - 0 купюр номиналом 5000 рублей
     */
    void giveBills(int[] billsCounts);
}



class MyATM{

    private final int[] nominalValues = {50, 100, 500, 1000, 5000};   // Существующие номиналы купюр
    private final int[] currentBillsCounts = {2, 2, 2, 2, 2};   // текущее количество купюр в банкомате

    Hardware hardware = new Hardware() {
        @Override
        public int[] getBillsCounts() {   // Возвращает массив с количеством купюр по номиналам 50, 100, 500, 1000, 5000.
            return currentBillsCounts;
        }

        @Override
        public void giveBills(int[] billsCounts) {
            int counter = 0;
            for (int i = 0; i < billsCounts.length; i++) {
                System.out.println("кол-во купюр, номиналом " + nominalValues[i] + "\t = \t" + billsCounts[i]);
                counter += nominalValues[i] * billsCounts[i];
            }
            System.out.println("Общая выдаваемая сумма составляер: " + counter + " рублей.");
        }
    };

    /*
    Метод принимает сумму денег для выдачи и отдает какое именно количество каких купюр нужно отдать пользователю.
     */
    public int[] calculateBanknotes(int amount) {
        int[] banknotes = new int[nominalValues.length];
        int restOfMoney = amount; // сколько еще осталось денег преобразовать в купюры

        for (int i = nominalValues.length - 1; i >= 0; i--) {
            int needBanknotes = restOfMoney / nominalValues[i];   // нужно выдать столько банкнот
            int availableBanknotes = hardware.getBillsCounts()[i];   // в банкомате сейчас столько банкнот такого-то номинала
            int extraditeBanknotes = Math.min(needBanknotes, availableBanknotes);   // выдать такое-то количество банкнот
            banknotes[i] = extraditeBanknotes;
            currentBillsCounts[i] -= extraditeBanknotes;
            restOfMoney -= extraditeBanknotes * nominalValues[i];
        }

        if (restOfMoney > 0) {
            throw new RuntimeException("\n\nВ банкомате не хватает купюр, чтобы обналичить всю сумму, равную " + amount + " рублей. \n" +
                    "Необналичено останется сумма, равная " + restOfMoney + " рублей.\n" +
                    "Cформированный набор купюр: " + Arrays.toString(banknotes) + " штук.\n" +
                    "При этом в банкомате остается набор купюр: " + Arrays.toString(hardware.getBillsCounts()) + " штук.\n" +
                    "Номиналы купюр: " + Arrays.toString(nominalValues) + " рублей.\n\n");
        }

        return banknotes;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MyATM atm = new MyATM();

        while (true) {
            System.out.print("\nВведите сумму к выдаче (целое число, в рублях): ");
            int amount = scanner.nextInt();
            System.out.println("Вы ввели: " + amount + " рублей.\n");
            if (amount <= 0) break;

            int[] banknotes = atm.calculateBanknotes(amount);
            atm.hardware.giveBills(banknotes);
        }

        scanner.close();
    }
}
