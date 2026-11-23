/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - Shared File I/O Utilities
 *
 * Package : ecocycle
 * Purpose : Helper class for saving and reading lines of text separated by "|"
 */
package ecocycle; // allows all 3 sections to use it

import java.util.regex.Pattern; // imports Pattern so we can split strings cleanly using "|"

// Helper class for reading and writing text file records
public final class RecordIO { // open to any package in the project, and final so it cannot be subclassed

    // constant separator character used between fields in our text files
    public static final String SEPARATOR = "|";

    // Java treats a bare "|" as "OR" in regex, so we escape it with "\\"
    // declared static final so the pattern is only built once instead of on every line
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\" + SEPARATOR);

    private RecordIO() {
        // private constructor so nobody creates an object of this class - it is just static helper methods
    }

    // Takes a single line of text from a file (e.g. L001|Peter|Book) and splits it into an array of strings
    public static String[] split(String line) {
        // if the line is null, return an empty array instead of crashing
        if (line == null) {
            return new String[0];
        }
        // split by "|" and use -1 so empty fields at the end of a line are kept
        return SPLIT_PATTERN.split(line, -1);
    }

    // Takes multiple strings and joins them into one line separated by "|"
    public static String join(String... fields) {
        // if nothing was passed in, return an empty string
        if (fields == null || fields.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            // add "|" between fields, but not before the first one
            if (i > 0) {
                sb.append(SEPARATOR);
            }
            // only add the field if it is not null
            if (fields[i] != null) {
                sb.append(fields[i]);
            }
        }
        return sb.toString();
    }

    // Safely turns text from a file into a double (decimal number)
    public static double toDouble(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Cannot parse null as double.");
        }
        try {
            // trim spaces and convert the text to a number
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException ex) {
            // if the text is not a valid number, throw a clear error instead of crashing unpredictably
            throw new IllegalArgumentException("Invalid number format: " + text, ex);
        }
    }
}
