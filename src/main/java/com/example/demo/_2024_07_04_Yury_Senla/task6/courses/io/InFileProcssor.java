package task6.gomelCourses.io;

import task6.gomelCourses.beans.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


public final class InFileProcssor implements InputProcessor {

    private final static String FILE_NAME = "src/task6/input/in000.txt";
    private final static String DELIMITER_INPUT_FILE = ";";
    private Backpack backpack;
    public List<Thing> things;


    public InFileProcssor() {
        readAll();
    }


    @Override
    public Backpack getBackpack() {
        return backpack;
    }

    @Override
    public List<Thing> getThings() {
        return things;
    }

    private void readAll() {
        Scanner scLine = null;
        try {
            FileReader f = new FileReader(FILE_NAME);
            scLine = new Scanner(f);
            scLine.useLocale(Locale.ENGLISH);
            scLine.useDelimiter(DELIMITER_INPUT_FILE);
            this.backpack = new Backpack(scLine.nextInt());
            int nmmThings = new Scanner(scLine.next()).nextInt();
            scLine.nextLine();
            this.things = new ArrayList<>(nmmThings);
            for (int i = 1; i <= nmmThings; i++) {
                String name = scLine.next().trim();
                int weight = scLine.nextInt();
                int cost = scLine.nextInt();
                Thing thing = new Thing(name, weight, cost);
                things.add(thing);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scLine != null) {
                scLine.close();
            }
        }
    }

}
