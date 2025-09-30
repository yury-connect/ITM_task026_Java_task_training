package task2;

import task2.com.senlainc.gomelCourses.constants.Constants;
import task2.com.senlainc.gomelCourses.interfaces.ImplIoProcessor;
import task2.com.senlainc.gomelCourses.ioProcessors.ConsoleProcessor;
import task2.com.senlainc.gomelCourses.logic.ArithmeticProcessor;

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

