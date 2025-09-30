package task2.com.senlainc.gomelCourses.logic;


public final class ArithmeticProcessor {


    private ArithmeticProcessor() {
        // NOP
    }


    // SCM (NOK)   - наименьшее общее кратное
    public static int smallestCommonMultiple(int first, int second) {
        int result = first * second / greatestCommonDivisor(first, second);
        return result;
    }

    // GCD (NOD)   - наибольший общий делитель
    public static int greatestCommonDivisor(int first, int second) {
        while (second != 0) {
            int tmp = first % second;
            first = second;
            second = tmp;
        }
        return first;
    }

}
