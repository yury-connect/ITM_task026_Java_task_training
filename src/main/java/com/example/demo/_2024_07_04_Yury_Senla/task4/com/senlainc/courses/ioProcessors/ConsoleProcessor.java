package task4.com.senlainc.gomelCourses.ioProcessors;

import task4.com.senlainc.gomelCourses.interfaces.ImplIoProcessor;

import java.util.Scanner;


public final class ConsoleProcessor implements ImplIoProcessor {

    private final static Scanner IN = new Scanner(System.in);


    public ConsoleProcessor() {
        // NOP
    }


    @Override
    public String get(String message){
        out(message);
        String result = IN.nextLine().trim();
        return result;
    };

    @Override
    public void out(String message) {
        System.out.println(message);
    };

    @Override
    public void closeResource(){
        if(this.IN != null) {
            this.IN.close();
        }
    };

}
