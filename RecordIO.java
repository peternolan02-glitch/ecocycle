/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - file input and output
 *
 * Author  : Jamie Stackpoole (25130340)
 * Package : ecocycle  (shared helper)
 * Purpose : Joins and splits the lines in the three text files
 *           (waste.txt) so every saved line uses the same format.
 *
 * WHY A PIPE AND NOT A COMMA
 *   The fields are typed in by a user, and somebody will type "Books, set of
 *   6" as a title - that would break a comma separated file. A pipe is far
 *   rarer in ordinary text, and clean() below replaces any pipe that does
 *   turn up, so the file can never be corrupted by user input.
 */
package ecocycle;

/**
 * Joins and splits the records stored in the text files.
 */
public final class RecordIO {

    /** The character that separates the fields of one record. */
    public static final String SEPARATOR = "|";

    /** This class only offers static helpers, so it is never instantiated. */
    private RecordIO() {
    }

    /**
     * Joins the fields of one record into a single line of text.
     *
     * @param fields the values to store, in order
     * @return one line of text, with no trailing separator
     */
    public static String join(String... fields) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                line.append(SEPARATOR);
            }
            line.append(clean(fields[i]));
        }
        return line.toString();
    }

    /**
     * Splits one line of text back into its fields.
     *
     * @param line one line read from the file
     * @return the separate fields (the limit keeps empty fields at the end)
     */
    public static String[] split(String line) {
        return line.split("\\" + SEPARATOR, -1);
    }

    /**
     * Removes anything that would break the one-record-per-line format.
     *
     * @param text the raw value
     * @return the value with pipes and line breaks replaced
     */
    public static String clean(String text) {
        if (text == null) {
            return "";
        }
        return text.replace(SEPARATOR, "/")
                .replace("\r", " ")
                .replace("\n", " ")
                .trim();
    }

    /**
     * Reads a boolean that was stored as text.
     *
     * @param text "true" or anything else
     * @return true only if the text is "true", ignoring case
     */
    public static boolean toBoolean(String text) {
        return "true".equalsIgnoreCase(text.trim());
    }

    /**
     * Reads a number that was stored as text.
     *
     * @param text the stored number
     * @return the value
     * @throws NumberFormatException if the text is not a number, which is how
     *         the managers spot a damaged line
     */
    public static double toDouble(String text) {
        return Double.parseDouble(text.trim());
    }
}
