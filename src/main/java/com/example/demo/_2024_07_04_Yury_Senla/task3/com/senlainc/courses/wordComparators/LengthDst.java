package task3.com.senlainc.gomelCourses.wordComparators;

import java.util.Comparator;


public class LengthDst implements Comparator<String> {


    @Override
    public int compare(String first, String second) {
        return second.length() - first.length();
    }

}
