package task6.gomelCourses.io;

import task6.gomelCourses.beans.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public final class InConsoleProcessor implements InputProcessor {

    private final static String NAME_DEFAULT = "Thing № ";
    private final static Scanner IN = new Scanner(System.in);


    public InConsoleProcessor() {
    }


    @Override
    public Backpack getBackpack() {
        System.out.print("\nEnter the carrying capacity of the backpack, kg: ");
        Backpack backpack = new Backpack(IN.nextInt());
        return backpack;
    }

    @Override
    public List<Thing> getThings() {
        System.out.print("Enter the number of things: ");
        int capacity = IN.nextInt();
        List<Thing> thingList = new ArrayList<>(capacity);
        for (int i = 1; i <= capacity; i++) {
            String name = NAME_DEFAULT + i;
            System.out.print("Enter the weight of item # " + i + ": ");
            int weight = IN.nextInt();
            System.out.print("Enter the price of item #  ");
            int cost = IN.nextInt();
            thingList.add(new Thing(name, weight, cost));
        }
        return thingList;
    }

}
