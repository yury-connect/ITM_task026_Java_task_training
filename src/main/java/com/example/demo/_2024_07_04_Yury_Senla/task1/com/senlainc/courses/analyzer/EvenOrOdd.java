package task1.com.senlainc.gomelCourses.analyzer;

import task1.com.senlainc.gomelCourses.interfaces.ImplAnalyzer;
import task1.com.senlainc.gomelCourses.сonstants.Constants;


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
