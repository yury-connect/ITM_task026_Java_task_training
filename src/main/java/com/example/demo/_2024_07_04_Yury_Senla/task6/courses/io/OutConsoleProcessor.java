package task6.gomelCourses.io;

import task6.gomelCourses.beans.*;
import java.util.List;


public final class OutConsoleProcessor {


    private OutConsoleProcessor() {
    }


    // shows the contents of the backpack
    public static void showBackpack(Backpack backpack) {
        System.out.printf("\nCapacity of Backpack =  %-6d kg.;\n", backpack.getPayload());
        System.out.printf("Number of items in the backpack = %,-3d things;\n", backpack.getResultSet().size());
        System.out.printf("Cost of a set of things in a backpack =  %-6d cents;\n", backpack.getTotalCost());
        System.out.printf("Free space in the backpack =  %-6d kg;\n", backpack.getPayload() - backpack.getTotalMass());
        System.out.println("List of items in the backpack:");
        showThings(backpack.getResultSet());
    }

    // shows the basic set of items available
    public static void showSetThings(List<Thing> things) {
        System.out.printf("\nThere is a set of %d items;\n", things.size());
        System.out.println("We display a full list of available items");
        showThings(things);
    }

    // table output
    private final static String TABLE_DELIMITER = "+-----+--------------------+----------+----------+";
    private final static String TABLE_NAMES     = "| №  |        Name        |  Weight  |   Cost   |";
    private static void showThings(List<Thing> things) {
        System.out.println(TABLE_DELIMITER);
        System.out.println(TABLE_NAMES);
        System.out.println(TABLE_DELIMITER);
        int count = 1;
        for (Thing thing: things) {
            System.out.printf("| %3d | %-17s |  %6d  |  %6d  |\n"
                    , count++
                    , thing.getName()
                    , thing.getWeight()
                    , thing.getCost());
        }
        System.out.println(TABLE_DELIMITER);
    }

}
