package mily.utils;

import java.util.*;

public class ArgParser {

    protected String flagPrefix;
    protected String positionalArgument;
    protected List<String> flags = new ArrayList<>();
    protected Map<String, ArgTypes> flagsTypes = new HashMap<>();
    protected Map<String, String> flagDescription = new HashMap<>();
    protected Map<String, Boolean> flagValuesBoolean = new HashMap<>();
    protected Map<String, String> flagValuesString = new HashMap<>();
    protected Map<String, Double> flagValuesDouble = new HashMap<>();
    protected Map<String, Integer> flagValuesInt = new HashMap<>();

    public ArgParser(String flagPrefix) {
        this.flagPrefix = flagPrefix;
    }

    public boolean hasStringFlag(String flag) {
        return flagValuesString.containsKey(flag);
    }

    public boolean hasIntFlag(String flag) {
        return flagValuesInt.containsKey(flag);
    }

    public boolean hasDoubleFlag(String flag) {
        return flagValuesDouble.containsKey(flag);
    }

    public boolean hasBooleanFlag(String flag) {
        return flagValuesBoolean.containsKey(flag);
    }

    public boolean getBoolean(String flag) {
        return flagValuesBoolean.get(flag);
    }

    @SuppressWarnings("unused")
    public String getString(String flag) {
        return flagValuesString.get(flag);
    }

    @SuppressWarnings("unused")
    public int getInteger(String flag) {
        return flagValuesInt.get(flag);
    }

    @SuppressWarnings("unused")
    public double getDouble(String flag) {
        return flagValuesDouble.get(flag);
    }

    public void addFlag(String flag, ArgTypes type, String description) {
        if (flags.contains(flag)) {
            throw new IllegalArgumentException("ArgParses already contains flag \"" + flag + "\"");
        }
        flags.add(flag);
        flagsTypes.put(flag, type);
        flagDescription.put(flag, description);

        if (type == ArgTypes.BOOLEAN) {
            flagValuesBoolean.put(flag, false);
        }
    }

    public String getPositionalArgument() {
        return positionalArgument;
    }

    public void processFlags(String[] strings) {
        boolean expectingValue = false;
        String onFlag = null;
        ArgTypes nextType = null;

        for (String str : strings) {
            if (str.startsWith(flagPrefix) && !expectingValue) {
                ArgTypes type = flagsTypes.get(str);

                if (!flags.contains(str)) {
                    throw new IllegalArgumentException("Illegal flag \"" + str + "\"");
                }

                if (type == ArgTypes.BOOLEAN) {
                    flagValuesBoolean.put(str, true);

                } else {
                    expectingValue = true;
                    nextType = type;
                    onFlag = str;
                }

            } else if (!str.startsWith(flagPrefix) && positionalArgument == null) {
                positionalArgument = str;

            } else if (expectingValue) {
                if (nextType == null) {
                    throw new IllegalArgumentException("Argument flag type is null");

                } else if (nextType == ArgTypes.STRING) {
                    flagValuesString.put(onFlag, str);

                } else if (nextType == ArgTypes.DOUBLE) {
                    flagValuesDouble.put(onFlag, Double.parseDouble(str));

                } else if (nextType == ArgTypes.INTEGER) {
                    flagValuesInt.put(onFlag, Integer.parseInt(str));
                }
                expectingValue = false;

            } else {
                throw new IllegalArgumentException("You have made some illegal arguments!");
            }
        }
    }

    public void printHelp() {

        int longest = 0;
        for (String key : flags) {
            int c = key.length();
            if (c > longest) {
                longest = c;
            }
        }
        System.out.println("\t\tMily Compiler");
        System.out.println();
        System.out.println("Usage: [main file] [options...]");
        System.out.println("Options:");
        for (String key : flags) {
            System.out.printf("   %s%s   %s", key, " ".repeat(longest - key.length()), flagDescription.get(key));
            System.out.println();
        }
    }

    public enum ArgTypes {
        DOUBLE,
        STRING,
        INTEGER,
        BOOLEAN
    }
}
