/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - individual section (Section A)
 *
 * Author  : Jamie Stackpoole (25130340)
 * Package : recycling  (Section A)
 * Purpose : The whole waste family in one file - the abstract parent first,
 *           then its two kinds. Only the parent is public; the subclasses
 *           below are package-private because nothing outside this package
 *           ever builds them.
 */
package recycling;

import ecocycle.ImpactCalculable;
import ecocycle.RecordIO;

/**
 * Abstract parent of every waste log entry.
 */
public abstract class Waste implements ImpactCalculable {

    /** Unique id given to every entry, e.g. W001. */
    private final String id;

    /** Short description of the item, e.g. "Glass jar". */
    private final String description;

    /** Weight in kilograms. Must be greater than zero. */
    private final double weightKg;

    /** Day the item was logged, as text in yyyy-mm-dd form. */
    private final String date;

    /** Builds the next id. Static because the counter belongs to the class. */
    private static int idCounter = 1;

    /**
     * Builds a waste entry with an automatically generated id.
     *
     * @param description what the item was
     * @param weightKg    weight in kilograms, must be above zero
     * @param date        day it was logged, yyyy-mm-dd
     * @throws IllegalArgumentException if any value is invalid
     */
    protected Waste(String description, double weightKg, String date) {
        this(null, description, weightKg, date);
    }

    /**
     * Builds a waste entry from a known id. Used when loading a file, so an
     * entry keeps the id it had before the application was closed.
     *
     * @param id          the id from the file, or null to generate a new one
     * @param description what the item was
     * @param weightKg    weight in kilograms
     * @param date        day it was logged, yyyy-mm-dd
     * @throws IllegalArgumentException if any value is invalid
     */
    Waste(String id, String description, double weightKg, String date) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be blank.");
        }
        if (weightKg <= 0) {
            throw new IllegalArgumentException("Weight must be greater than zero.");
        }
        if (!isValidDate(date)) {
            throw new IllegalArgumentException("Date must be in yyyy-mm-dd format.");
        }
        if (id == null || id.trim().isEmpty()) {
            this.id = String.format("W%03d", idCounter++);
        } else {
            this.id = id.trim();
            moveCounterPast(this.id);
        }
        this.description = description.trim();
        this.weightKg = weightKg;
        this.date = date.trim();
    }

    /**
     * Checks that a date really looks like yyyy-mm-dd.
     *
     * @param date the text to test
     * @return true if the text is a correctly shaped date
     */
    private static boolean isValidDate(String date) {
        return date != null && date.trim().matches("\\d{4}-\\d{2}-\\d{2}");
    }

    /**
     * Moves the id counter past an id just read from a file, so a new entry
     * can never be given an id that is already in the file.
     *
     * @param id the id that has just been read
     */
    private static void moveCounterPast(String id) {
        String digits = id.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return;
        }
        try {
            int number = Integer.parseInt(digits);
            if (number >= idCounter) {
                idCounter = number + 1;
            }
        } catch (NumberFormatException ex) {
            // an id we cannot read as a number is simply left alone
        }
    }

    /** The unique id of this entry, e.g. W001. */
    public String getId() {
        return id;
    }

    /** The description of the item. */
    public String getDescription() {
        return description;
    }

    /** The weight of the item in kilograms. */
    public double getWeightKg() {
        return weightKg;
    }

    /** The day the entry was logged, yyyy-mm-dd. */
    public String getDate() {
        return date;
    }

    /**
     * Which kind of waste this is, e.g. "Recyclable". Each subclass answers
     * for itself - that is polymorphism.
     *
     * @return the type name
     */
    public abstract String getType();

    /**
     * Advice on how to dispose of this item properly.
     *
     * @return a sentence telling the user what to do with the item
     */
    public abstract String getDisposalAdvice();

    /**
     * The kg of CO2e this item avoided by being recycled or composted instead
     * of sent to landfill. Each type of waste avoids a different amount.
     *
     * @return the impact in kg CO2e
     */
    @Override
    public abstract double calculateImpact();

    /**
     * One readable line for the lists in the GUI.
     *
     * <p>The id comes first, and the screens rely on that: deleting a row
     * splits this line on " - " and takes the first piece as the id.</p>
     *
     * @return a formatted description of the entry
     */
    @Override
    public String toString() {
        return String.format("%s - %s - %s - %.2f kg - %s - %.2f kg CO2e",
                id, description, getType(), weightKg, date, calculateImpact());
    }

    /** Puts the id counter back to 1, so ids start at W001 again. */
    public static void resetIdCounter() {
        idCounter = 1;
    }

    /**
     * Returns this entry as one line for the text file.
     *
     * <p>The parent writes the fields every waste entry has. Each subclass
     * calls super.toFileRecord() and appends its own extra fields, so nothing
     * is ever left out.</p>
     *
     * @return the line to write, without a line break
     */
    public String toFileRecord() {
        return RecordIO.join(id, getType(), description,
                String.valueOf(weightKg), date);
    }

    /**
     * Rebuilds an entry from one line of the text file.
     *
     * <p>This is the only method that knows how to turn text back into the
     * right subclass: it reads the type field and builds a RecyclableWaste or
     * an OrganicWaste. Returning null means "this line is damaged" - the
     * manager then counts it and carries on with the rest of the file.</p>
     *
     * @param line one line read from the file
     * @return the rebuilt entry, or null if the line cannot be understood
     */
    public static Waste fromFileRecord(String line) {
        String[] parts = RecordIO.split(line);
        if (parts.length < 5) {
            return null;
        }
        try {
            double weight = RecordIO.toDouble(parts[3]);
            if ("Recyclable".equalsIgnoreCase(parts[1]) && parts.length >= 7) {
                return new RecyclableWaste(parts[0], parts[2], parts[5], weight,
                        parts[4], RecordIO.toBoolean(parts[6]));
            }
            if ("Organic".equalsIgnoreCase(parts[1]) && parts.length >= 6) {
                return new OrganicWaste(parts[0], parts[2], weight, parts[4],
                        RecordIO.toBoolean(parts[5]));
            }
            return null;
        } catch (IllegalArgumentException ex) {
            /* Bad number or date in the file: treat the line as damaged.
               NumberFormatException extends IllegalArgumentException, so this
               one catch covers both. */
            return null;
        }
    }
}
