package com.example.demo._2024_07_04_Yury_Senla.task1.com.senlainc.courses.analyzer;

import com.example.demo._2024_07_04_Yury_Senla.task1.com.senlainc.courses.interfaces.ImplAnalyzer;
import com.example.demo._2024_07_04_Yury_Senla.task1.com.senlainc.courses.сonstants.Constants;


public final class EvenOrOdd implements ImplAnalyzer {

    // чет или нечет
    @Override
    public String check(int number) {
        String result;
        if(number % 2 == 0) {
            result = Constants.EVEN_OR_ODD_TRUE;
        } else {
            result = Constants.EVEN_OR_ODD_FALSE;
        };
        return result;
    }

}
