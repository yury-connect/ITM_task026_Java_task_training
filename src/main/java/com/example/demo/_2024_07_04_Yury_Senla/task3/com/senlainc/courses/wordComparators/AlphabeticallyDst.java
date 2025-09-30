package com.example.demo._2024_07_04_Yury_Senla.task3.com.senlainc.courses.wordComparators;

import java.util.Comparator;


public class AlphabeticallyDst implements Comparator<String> {


    @Override
    public int compare(String first, String second) {
        return second.compareTo(first);
    }

}
