package com.example.demo._2024_07_04_Yury_Senla.task5.com.senlainc.courses.inputData;

import com.example.demo._2024_07_04_Yury_Senla.task5.com.senlainc.courses.interfaces.ImplInputData;
import java.util.Random;


public class RndGeneratorNum implements ImplInputData {

    private final static int NUM_DIGITS_MULTIPLIER = 100; // множитель 100 => числа от 0 до 100
    private final static Random RANDOM = new Random();


    @Override
    public int[] getData(int count) {
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = RANDOM.nextInt(NUM_DIGITS_MULTIPLIER);
        }
        return result;
    }
}
