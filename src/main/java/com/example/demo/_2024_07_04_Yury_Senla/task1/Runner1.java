package com.example.demo._2024_07_04_Yury_Senla.task1;

import com.example.demo._2024_07_04_Yury_Senla.task1.com.senlainc.courses.interfaces.ImplAnalyzer;
import com.example.demo._2024_07_04_Yury_Senla.task1.com.senlainc.courses.analyzer.*;
import com.example.demo._2024_07_04_Yury_Senla.task1.com.senlainc.courses.сonstants.Constants;

import java.util.InputMismatchException;
import java.util.Scanner;


/**
 * @author Yury Lapitskiy
 */
public class Runner1 {

    public static void main(String[] args) {
        Scanner in = null;
        try {
            in = new Scanner(System.in);
            System.out.print(Constants.INPUT_MESSAGE);
            int num = in.nextInt();
            if(num < 2) { throw new InputMismatchException();}

            ImplAnalyzer evenOrOdd = new EvenOrOdd();
            ImplAnalyzer simpleOrCompound = new SimpleOrCompound();

            System.out.println(Constants.OUTPUT_MESSAGE + num);
            System.out.println( evenOrOdd.check(num) + simpleOrCompound.check(num));

        } catch (InputMismatchException e) {
            System.out.println(Constants.ERROR_MESSAGE);
        } finally {
            if(in != null) {
                in.close();
            }
        }

    }






}
