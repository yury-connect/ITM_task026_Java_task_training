package task4;

import task4.com.senlainc.gomelCourses.interfaces.ImplIoProcessor;
import task4.com.senlainc.gomelCourses.ioProcessors.ConsoleProcessor;
import task4.com.senlainc.gomelCourses.constants.Constants;


/**
 * @author Yury Lapitskiy
 */
public class Runner4 {

    public static void main(String[] args) {

        ImplIoProcessor ioProcessor = new ConsoleProcessor();

        String sentence = ioProcessor.get(Constants.INPUT_MESSAGE_SENTENCE);
        String example = ioProcessor.get(Constants.INPUT_MESSAGE_EXAMPLE).toLowerCase();

        String[] words = sentence.replaceAll("\\p{Punct}", "").split("\\s+"); // уберем пунктуацию и перегоним в массив
        int count = 0;
        for (String word : words) {
            if (word.toLowerCase().equals(example)) {
                count++;
            }
        }

        ioProcessor.out(Constants.OUTPUT_MESSAGE_RESULT_HEAD + count + Constants.OUTPUT_MESSAGE_RESULT_TEIL);
        ioProcessor.closeResource();
    }

}
