package com.example.demo._2024_07_04_Yury_Senla.task3;

import task3.com.senlainc.gomelCourses.wordComparators.LengthInc;
import task3.com.senlainc.gomelCourses.сonstants.Constants;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;


/**
 * @author Yury Lapitskiy
 */
public class Runner3 {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print(Constants.INPUT_MESSAGE);
        String sentence = in.nextLine().trim();
        if (!sentence.isEmpty()) {
            System.out.println(Constants.OUTPUT_MESSAGE_SRC + sentence);

            String[] words = sentence.replaceAll("\\p{Punct}", "").split("\\s+"); // уберем пунктуацию и перегоним в массив
            System.out.println(Constants.OUTPUT_MESSAGE_NUM + words.length);

            Comparator<String> comp = new LengthInc(); // выбираем нужный компаратор
            Arrays.sort(words, comp);
            System.out.println(Constants.OUTPUT_MESSAGE_COMP_NAME + comp.getClass().getSimpleName());
            System.out.println(Constants.OUTPUT_MESSAGE_SORT + Arrays.toString(words));

            StringBuilder result = new StringBuilder();
            for (String word : words) {
                result.append(word.substring(0, 1).toUpperCase() + word.substring(1));
                result.append(Constants.DELIMITER);
            }
            System.out.println(Constants.OUTPUT_MESSAGE_UP + result);

        } else {
            System.out.println(Constants.OUTPUT_MESSAGE_ERROR);
        }

        in.close();
    }

}
