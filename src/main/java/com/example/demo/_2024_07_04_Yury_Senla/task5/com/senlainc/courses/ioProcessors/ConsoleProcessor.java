package task5.com.senlainc.gomelCourses.ioProcessors;

import task5.com.senlainc.gomelCourses.interfaces.ImplIoProcessor;

import java.util.Scanner;


public final class ConsoleProcessor implements ImplIoProcessor {

    private final static Scanner IN = new Scanner(System.in);


    public ConsoleProcessor() {
        // NOP
    }


    @Override
    public int get(String message){
        out(message);
        int result = IN.nextInt();
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
