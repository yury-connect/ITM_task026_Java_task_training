package com.example.demo._2024_07_04_Yury_Senla.task2;

import com.example.demo._2024_07_04_Yury_Senla.task2.com.senlainc.courses.constants.Constants;
import com.example.demo._2024_07_04_Yury_Senla.task2.com.senlainc.courses.interfaces.ImplIoProcessor;
import com.example.demo._2024_07_04_Yury_Senla.task2.com.senlainc.courses.ioProcessors.ConsoleProcessor;
import com.example.demo._2024_07_04_Yury_Senla.task2.com.senlainc.courses.logic.ArithmeticProcessor;

import java.util.InputMismatchException;


/**
 * @author Yury Lapitskiy
 */
public class Runner2 {

    public static void main(String[] args) {

        ImplIoProcessor ioProcessor = new ConsoleProcessor();
        try {
            int first = ioProcessor.get(Constants.INPUT_MESSAGE_FIRST);
            int second = ioProcessor.get(Constants.INPUT_MESSAGE_SECONDE);

            ioProcessor.out(Constants.GCD_OUT_MESSAGE + ArithmeticProcessor.greatestCommonDivisor(first, second)); // NOD
            ioProcessor.out(Constants.SCM_OUT_MESSAGE + ArithmeticProcessor.smallestCommonMultiple(first, second)); // NOK

        } catch (InputMismatchException e) {
            ioProcessor.out(Constants.ERROR_MESSAGE);
        } finally {
            ioProcessor.closeResource();
        }
    }

}

