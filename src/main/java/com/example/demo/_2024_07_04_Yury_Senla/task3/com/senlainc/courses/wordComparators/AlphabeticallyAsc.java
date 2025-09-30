package task3.com.senlainc.gomelCourses.wordComparators;

import java.util.Comparator;


public class AlphabeticallyAsc implements Comparator<String> {


    @Override
    public int compare(String first, String second) {
        return first.compareTo(second);
    }

}
