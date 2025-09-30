package task5;

import task5.com.senlainc.gomelCourses.constants.Constants;
import task5.com.senlainc.gomelCourses.inputData.*;
import task5.com.senlainc.gomelCourses.interfaces.*;
import task5.com.senlainc.gomelCourses.ioProcessors.*;
import java.util.Arrays;


/**
 * @author Yury Lapitskiy
 */
public class Runner5 {

    public static void main(String[] args) {

        ImplIoProcessor ioProcessor = new ConsoleProcessor();
        int count = ioProcessor.get(Constants.INPUT_MESSAGE);
        ioProcessor.out(Constants.OUTPUT_MESSAGE_NUM_CONT + count);
        if (count > Constants.MAX_COUNT) {
            throw new IllegalArgumentException(Constants.OUTPUT_MESSAGE_ERROR);
        }

        /*
        Т.к. в условии задачи не указан источник данных, то мною выбрано произвольно 3 источника данных:
        1. источник данных - java файл   SrcJavaFileNum()
        2. источник данных - текстовый файл   SrcTxtFileNum()
        3. исочник данных - генератор случайных чисел   RndGeneratorNum()
         */

//        ImplInputData source = new SrcJavaFileNum(); // источник данных - java файл
//        ImplInputData source = new SrcTxtFileNum(); // источник данных - текстовый файл
        ImplInputData source = new RndGeneratorNum(); // исочник данных - генератор случайных чисел
        ioProcessor.out(Constants.OUTPUT_MESSAGE_NUM_SOURCE + source.getClass().getSimpleName());

        int[] sequence = source.getData(count);
        ioProcessor.out(Constants.OUTPUT_MESSAGE_SEQUENCE + Arrays.toString(sequence));

        ioProcessor.out(Constants.OUTPUT_MESSAGE_LIST_POLY);
        int palindromeCount = 0;
        for (int num: sequence) {
            if (isPalindromic(num)) {
                palindromeCount++;
                ioProcessor.out(num + Constants.OUTPUT_MESSAGE_OUT_DELIMITER);
            }
        }
        ioProcessor.out(Constants.OUTPUT_MESSAGE_POLY + palindromeCount);
        ioProcessor.closeResource();
    }


    public static boolean isPalindromic(int number) {
        char[] digits = String.valueOf(number).toCharArray();
        boolean isPalindrom = digits.length > 1 ? true : false;
        for (int i=0, j=digits.length-1;   isPalindrom && i<digits.length/2;    i++, j--) {
            if (digits[i] != digits[j]) {
                isPalindrom = false;
            }
        }
        return isPalindrom;
    }

}
