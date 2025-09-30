package com.example.demo.Alex_2025_09_23.MultiThread;

import java.util.Arrays;
import java.util.Scanner;

public class Bank {

    public int[] nominals = {5000, 2000, 1000, 500, 200, 100};
    public int[] quontityNominals = {5, 5, 5, 5, 5, 0};


    public int[] getNominals() {


        Scanner sc = new Scanner(System.in);
        System.out.println("Введите сумму для снятия ");
        int originalTake = sc.nextInt();


        int totalSum = 0;
        for (int i = 0; i < nominals.length; i++) {
            totalSum += nominals[i] * quontityNominals[i];

        }
        System.out.println("Всего денег " + totalSum);


        if (originalTake > totalSum) {
            System.out.println(" Запрошена слишком большая сумма");
            return null;
        }

        int[] issuedNominals = new int[nominals.length];
        boolean success = false;
        int[] currentIssued = new int[nominals.length];
        int take = originalTake;
        int[] currentQuantity = quontityNominals.clone();

        for (int j = 0; j < nominals.length; j++) {

//            System.out.println("j= " + j);
//
//            System.out.println("take= " + take);



            originalTake = take;

            for (int i = j; i < nominals.length; i++) {
                while (take >= nominals[i] && currentQuantity[i] > 0) {
                    take -= nominals[i];
                    currentQuantity[i]--;
                    currentIssued[i]++;
                }
            //    System.out.println(Arrays.toString(currentIssued));

            }

            int x = currentIssued[j] - 1;
        //    System.out.println("После внутреннего цикла " + x);

            if (take == 0) {

                quontityNominals = currentQuantity;
                issuedNominals = currentIssued;
                success = true;
            //    System.out.println("Успех с j = " + j);
                break;
            } else {
                if(x>0) {
                    currentIssued[j] = x;
                } else {
                    currentIssued[j] = 0;
                }
                for (int k = j + 1; k < nominals.length; k++) {
                    currentIssued[k] = 0;
                }
                for (int i = 0; i < nominals.length; i++) {
                    currentQuantity[j]=quontityNominals[j]-currentIssued[j];
                }
                take = originalTake - currentIssued[j] * nominals[j];
              //  System.out.println(Arrays.toString(currentIssued));
               // System.out.println(take);
            }
        }

        if (!success) {
            System.out.println("Не удалось выдать сумму " + originalTake);
            return new int[nominals.length];
        }


        for (int i = 0; i < nominals.length; i++) {
            System.out.println("Выдано " + nominals[i] + "  " + issuedNominals[i] + "  " + "шт");
        }

        for (int i = 0; i < issuedNominals.length; i++) {
            System.out.println("Осталось номиналов " + nominals[i]+ "  " + quontityNominals[i]+ "  " + "шт");
        }

        return issuedNominals;

    }

    public static void main(String[] args) {

        Bank bank = new Bank();

        bank.getNominals();


    }

}
