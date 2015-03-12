package fi.helsinki.cs.tmc.langs.util;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            printHelp();
        }
        run(args);
    }

    private static void printHelp() {
        System.out.println("Usage: TODO: Write instructions here.");
        System.exit(0);
    }

    private static void run(String[] args) {
        Map<String, Integer> commands = getCommands();
        String command = args[0];
        Integer argsCount = commands.get(command);

        if (argsCount == null) {
            printHelp();
        } else if (!argsCount.equals(args.length - 1)) {
            System.out.println("ERROR: wrong argument count for " + command);
            printHelp();
        }

        switch(command) {
            case "--help" :
                printHelp();
                break;
            case "--checkstyle" :
                runCheckStyle(args);
                break;
        }
    }

    private static Map<String, Integer> getCommands() {
        //Command name and required argument count
        Map<String, Integer> commands = new HashMap<>();
        commands.put("--help", 0);
        commands.put("--checkstyle", 1);
        return commands;
    }

    private static boolean runCheckStyle(String[] args) {
        Paths.get(args[1]);

        return true;
    }
}
