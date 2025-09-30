package task6;

import task6.gomelCourses.beans.*;
import task6.gomelCourses.io.*;
import java.util.List;


/**
 * @author Yury Lapitskiy
 */
public class Runner6 {

//    private final static InputProcessor INPUT_PROCESSOR = new InConsoleProcessor();   // using console input:
    private final static InputProcessor INPUT_PROCESSOR = new InFileProcssor();   // using input from a file .txt
//    private final static InputProcessor INPUT_PROCESSOR = new InRandomProcessor();   // using random numbers:


    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        List<Thing> things = INPUT_PROCESSOR.getThings();
        OutConsoleProcessor.showSetThings(things);
        Backpack backpack = INPUT_PROCESSOR.getBackpack();
        backpack.fillBackpack(things);
        OutConsoleProcessor.showBackpack(backpack);
        long timeSpent = System.currentTimeMillis() - startTime;
        System.out.printf("\tTime spent: %,d milliseconds.\n", timeSpent);
    }

}