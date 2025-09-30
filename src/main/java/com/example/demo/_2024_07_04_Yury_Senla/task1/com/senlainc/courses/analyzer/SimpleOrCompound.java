package task1.com.senlainc.gomelCourses.analyzer;

import task1.com.senlainc.gomelCourses.interfaces.ImplAnalyzer;
import task1.com.senlainc.gomelCourses.сonstants.Constants;


public final class SimpleOrCompound implements ImplAnalyzer {

    // простое или составное
    @Override
    public String check(int number) {
        boolean isSimple = true;
        for(int i = number - 1; i > 1; i--) {
            if(number % i == 0) {
                isSimple = false;
                break;
            };
        };
        String result = isSimple ? Constants.SIMPLE_OR_COMPOUND_TRUE : Constants.SIMPLE_OR_COMPOUND_FALSE;
        return result;
    }

}
