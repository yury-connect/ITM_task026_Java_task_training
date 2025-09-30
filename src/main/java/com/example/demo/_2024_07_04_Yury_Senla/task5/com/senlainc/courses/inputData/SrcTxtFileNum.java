package com.example.demo._2024_07_04_Yury_Senla.task5.com.senlainc.courses.inputData;

import com.example.demo._2024_07_04_Yury_Senla.task5.com.senlainc.courses.constants.Constants;
import com.example.demo._2024_07_04_Yury_Senla.task5.com.senlainc.courses.interfaces.ImplInputData;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Locale;
import java.util.Scanner;


public class SrcTxtFileNum implements ImplInputData {


    @Override
    public int[] getData(int count) {
        int[] result = new int[count];
        Scanner sc = null;
        try {
            sc = new Scanner(new FileReader(Constants.FILE_NAME));
            sc.useLocale(Locale.ENGLISH);
            sc.useDelimiter(Constants.DELIMITER_INPUT_FILE);

        for (int i = 0; i < count; i++) {
            result[i] = sc.nextInt();
        }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
    return result;
    }
}
