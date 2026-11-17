/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - working section
 *
 * Author  : Valentin Gabriel Luchian (24318230)
 * Package : footprint  (Section B)
 * Purpose : The whole carbon-activity family in one file - the abstract
 *           parent first, then its two kinds. Only the parent is public; the
 *           subclasses below are package-private because nothing outside
 *           this package ever builds them. That is how our file count stays
 *           small.
 */
package footprint;

import ecocycle.ImpactCalculable;
import ecocycle.RecordIO;

/**
 * Abstract parent of every logged carbon producing activity.
 */
public abstract class CarbonActivity implements ImpactCalculable {

    /** Unique id, e.g. C001. */
    private final String id;

    /** Day the activity happened, yyyy-mm-dd. */
    private final String date;

    /** The amount logged - kilometres travelled, or kWh used. */
    private final double amount;

    /** Running number used to build the ids. */
    private static int idCounter = 1;

    /**
     * Builds an activity with an automatically generated id.
     *
     * @throws IllegalArgumentException if the amount is not positive or the
     *         date is not in yyyy-mm-dd form
     */
    protected CarbonActivity(String date, double amount) {
        this(null, date, amount);
    }

    /**
     * Rebuilds an activity from a known id, used when loading a file so an
     * activity keeps the id it had before the application was closed.
     */
    CarbonActivity(String id, String date, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (!isValidDate(date)) {
            throw new IllegalArgumentException("Date must be in yyyy-mm-dd form.");
        }
        if (id == null || id.trim().isEmpty()) {
            this.id = String.format("C%03d", idCounter++);
        } else {
            this.id = id.trim();
            moveCounterPast(this.id);
        }
        this.date = date.trim();
        this.amount = amount;
    }

    /**
     * Moves the id counter past an id just read from a file, so a new
     * activity can never be given an id that is already in the file.
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

    /**
     * Checks that a date really looks like yyyy-mm-dd: exactly ten
     * characters, a dash in the two right places, digits everywhere else.
     */
    private static boolean isValidDate(String date) {
        if (date == null) {
            return false;
        }
        String trimmed = date.trim();
        if (trimmed.length() != 10) {
            return false;
        }
        for (int i = 0; i < 10; i++) {
            char ch = trimmed.charAt(i);
            if (i == 4 || i == 7) {
                if (ch != '-') {
                    return false;
                }
            } else if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }

    /** The unique id, for example C001. */
    public String getId() {
        return id;
    }

    /** The day the activity happened, yyyy-mm-dd. */
    public String getDate() {
        return date;
    }

    /** The amount logged: km travelled, or kWh used. */
    public double getAmount() {
        return amount;
    }

    /** Which kind of activity this is, for example "Transport". */
    public abstract String getType();

    /** The unit getAmount() is measured in: "km" or "kWh". */
    public abstract String getUnit();

    /** The carbon produced, in kg CO2e. Each subclass uses its own factor. */
    public abstract double calculateCO2e();

    /**
     * The impact of a carbon activity simply is its CO2e, so this method
     * hands the work to calculateCO2e(). That is polymorphism.
     */
    @Override
    public double calculateImpact() {
        return calculateCO2e();
    }

    /** One readable line for the lists in the GUI. The id comes first. */
    @Override
    public String toString() {
        return String.format("%s - %s - %.1f %s - %s - %.2f kg CO2e",
                id, getType(), amount, getUnit(), date, calculateCO2e());
    }

    /** Puts the id counter back to 1, so ids start at C001 again. */
    public static void resetIdCounter() {
        idCounter = 1;
    }

    /**
     * Returns this activity as one line for the text file. The parent writes
     * the shared fields; each subclass appends its own extra fields.
     */
    public String toFileRecord() {
        return RecordIO.join(id, getType(), date, String.valueOf(amount));
    }

    /**
     * Rebuilds an activity from one line of the text file. It reads the type
     * field and builds a TransportActivity or an EnergyActivity. Returning
     * null means "this line is damaged".
     */
    public static CarbonActivity fromFileRecord(String line) {
        String[] parts = RecordIO.split(line);
        if (parts.length < 4) {
            return null;
        }
        try {
            double amount = RecordIO.toDouble(parts[3]);
            if ("Transport".equalsIgnoreCase(parts[1]) && parts.length >= 6) {
                int passengers = Integer.parseInt(parts[5].trim());
                return new TransportActivity(parts[0], parts[4], amount,
                        passengers, parts[2]);
            }
            if ("Energy".equalsIgnoreCase(parts[1]) && parts.length >= 5) {
                return new EnergyActivity(parts[0], parts[4], amount, parts[2]);
            }
            return null;
        } catch (IllegalArgumentException ex) {
            /* bad number or bad date in the file - treat the line as damaged.
               NumberFormatException is a subclass of IllegalArgumentException,
               so this one catch covers both. */
            return null;
        }
    }
}

// TransportActivity - package-private: kept in the same file as its parent above.
/**
 * A transport activity, measured in kilometres travelled.
 */
class TransportActivity extends CarbonActivity {

    /** The travel mode, e.g. "Car", "Bus", "Train", "Bike", "Walk". */
    private final String mode;

    /** How many passengers the vehicle was carrying. */
    private final int passengers;

    /**
     * Emission factors in kg CO2e per kilometre. A car carrying more people
     * spreads its carbon over more of them, so we divide by the passenger
     * count later on.
     */
    private static final String[][] FACTORS = {
        {"Car", "0.192"},
        {"Bus", "0.105"},
        {"Train", "0.041"},
        {"Bike", "0.000"},
        {"Walk", "0.000"}
    };

    /**
     * Builds a transport activity with an automatically generated id.
     *
     * @throws IllegalArgumentException if the mode is blank, the distance is
     *         not positive, or the passenger count is below 1
     */
    public TransportActivity(String mode, double distanceKm,
                             int passengers, String date) {
        this(null, mode, distanceKm, passengers, date);
    }

    /** Rebuilds an activity from a known id, used when loading a file. */
    TransportActivity(String id, String mode, double distanceKm,
                      int passengers, String date) {
        super(id, date, distanceKm);
        if (mode == null || mode.trim().isEmpty()) {
            throw new IllegalArgumentException("Travel mode cannot be blank.");
        }
        if (passengers < 1) {
            throw new IllegalArgumentException("There must be at least 1 passenger.");
        }
        this.mode = mode.trim();
        this.passengers = passengers;
    }

    /** The travel mode. */
    public String getMode() {
        return mode;
    }

    /** The number of passengers. */
    public int getPassengers() {
        return passengers;
    }

    /** The modes the GUI should offer in its drop-down. */
    public static String[] getSupportedModes() {
        String[] modes = new String[FACTORS.length];
        for (int i = 0; i < FACTORS.length; i++) {
            modes[i] = FACTORS[i][0];
        }
        return modes;
    }

    /** Called "Transport" everywhere in the app and the file. */
    @Override
    public String getType() {
        return "Transport";
    }

    /** Transport is measured in kilometres. */
    @Override
    public String getUnit() {
        return "km";
    }

    /**
     * Looks the factor up in the table above. An unknown mode falls back to
     * the car factor rather than crashing.
     */
    @Override
    public double calculateCO2e() {
        double factor = 0.192;
        for (String[] row : FACTORS) {
            if (row[0].equalsIgnoreCase(mode)) {
                factor = Double.parseDouble(row[1]);
                break;
            }
        }
        return (getAmount() * factor) / passengers;
    }

    /** Adds the travel mode and the passenger count to the file line. */
    @Override
    public String toFileRecord() {
        return super.toFileRecord()
                + RecordIO.SEPARATOR + mode
                + RecordIO.SEPARATOR + passengers;
    }
}

// EnergyActivity - package-private: kept in the same file as its parent above.
/**
 * An energy activity, measured in kilowatt hours used.
 */
class EnergyActivity extends CarbonActivity {

    /** The energy source, e.g. "Electricity", "Gas", "Heating Oil". */
    private final String source;

    /** Emission factors in kg CO2e per kWh. */
    private static final String[][] FACTORS = {
        {"Electricity", "0.386"},
        {"Gas", "0.203"},
        {"Heating Oil", "0.267"}
    };

    /**
     * Builds an energy activity with an automatically generated id.
     *
     * @throws IllegalArgumentException if the source is blank or the kWh
     *         value is not positive
     */
    public EnergyActivity(String source, double kWh, String date) {
        this(null, source, kWh, date);
    }

    /** Rebuilds an activity from a known id, used when loading a file. */
    EnergyActivity(String id, String source, double kWh, String date) {
        super(id, date, kWh);
        if (source == null || source.trim().isEmpty()) {
            throw new IllegalArgumentException("Energy source cannot be blank.");
        }
        this.source = source.trim();
    }

    /** The energy source. */
    public String getSource() {
        return source;
    }

    /** The sources the GUI should offer in its drop-down. */
    public static String[] getSupportedSources() {
        String[] sources = new String[FACTORS.length];
        for (int i = 0; i < FACTORS.length; i++) {
            sources[i] = FACTORS[i][0];
        }
        return sources;
    }

    /** Called "Energy" everywhere in the app and the file. */
    @Override
    public String getType() {
        return "Energy";
    }

    /** Energy is measured in kilowatt hours. */
    @Override
    public String getUnit() {
        return "kWh";
    }

    /**
     * Looks the factor up in the table above. An unknown source falls back
     * to the electricity factor rather than crashing.
     */
    @Override
    public double calculateCO2e() {
        double factor = 0.386;
        for (String[] row : FACTORS) {
            if (row[0].equalsIgnoreCase(source)) {
                factor = Double.parseDouble(row[1]);
                break;
            }
        }
        return getAmount() * factor;
    }

    /** Adds the energy source to the file line. */
    @Override
    public String toFileRecord() {
        return super.toFileRecord()
                + RecordIO.SEPARATOR + source;
    }
}
