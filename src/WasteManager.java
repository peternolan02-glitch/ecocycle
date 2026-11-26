/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - individual section (Section A)
 *
 * Author  : Jamie Stackpoole (25130340)
 * Package : recycling  (Section A)
 * Purpose : Holds every Waste object in an ArrayList and provides the
 *           Add / Display / Search / Delete operations plus the two reports
 *           the GUI buttons call. All screens in this section share one
 *           manager, so data survives moving between them.
 */
package recycling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * The collection manager for Section A: it owns the ArrayList of waste logs.
 */
public class WasteManager {

    /** Where the logs are saved when the user does not choose a name. */
    public static final String DEFAULT_FILE =
            "data" + File.separator + "waste.txt";

    /** The in-memory store of waste logs. */
    private final ArrayList<Waste> logs = new ArrayList<Waste>();

    /** A few nearby recycling points, used by the advice report. */
    private final String[][] recyclingCentres = {
        {"Riverside Civic Centre", "Mon 09:00-17:00", "Glass, paper, cans, cardboard"},
        {"Northgate Bring Bank", "Open 24 hours", "Glass and plastic only"},
        {"Green Park Mobile Depot", "Wed 10:00-14:00", "Garden and food waste"}
    };

    /* ---------------------------------------------------------------------
       ADD, DISPLAY, SEARCH, DELETE  (the four operations every section has)
       --------------------------------------------------------------------- */

    /** Adds a waste entry to the collection. */
    public void addWaste(Waste waste) {
        logs.add(waste);
    }

    /**
     * Builds the text shown by the Display All button.
     *
     * @return one line per entry, or a message if there is nothing stored
     */
    public String displayAll() {
        if (logs.isEmpty()) {
            return "No waste logged yet.";
        }
        StringBuilder out = new StringBuilder();
        for (Waste waste : logs) {
            out.append(waste.toString()).append(System.lineSeparator());
        }
        return out.toString();
    }

    /**
     * Searches by type, ignoring case and surrounding spaces.
     *
     * @param type the type to look for, e.g. "Recyclable"
     * @return every matching entry, possibly an empty list
     */
    public ArrayList<Waste> searchByType(String type) {
        ArrayList<Waste> found = new ArrayList<Waste>();
        if (type == null) {
            return found;
        }
        String wanted = type.trim();
        for (Waste waste : logs) {
            if (waste.getType().equalsIgnoreCase(wanted)) {
                found.add(waste);
            }
        }
        return found;
    }

    /**
     * Searches the description for a word the user typed.
     *
     * @param keyword part of the description to look for
     * @return every matching entry, possibly an empty list
     */
    public ArrayList<Waste> searchByDescription(String keyword) {
        ArrayList<Waste> found = new ArrayList<Waste>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return found;
        }
        String wanted = keyword.trim().toLowerCase();
        for (Waste waste : logs) {
            if (waste.getDescription().toLowerCase().contains(wanted)) {
                found.add(waste);
            }
        }
        return found;
    }

    /**
     * Searches by the exact date the entry was logged.
     *
     * @param date the date in yyyy-mm-dd form
     * @return every matching entry, possibly an empty list
     */
    public ArrayList<Waste> searchByDate(String date) {
        ArrayList<Waste> found = new ArrayList<Waste>();
        if (date == null) {
            return found;
        }
        String wanted = date.trim();
        for (Waste waste : logs) {
            if (waste.getDate().equalsIgnoreCase(wanted)) {
                found.add(waste);
            }
        }
        return found;
    }

    /**
     * Looks for one entry by its id.
     *
     * @param id the id to look for, e.g. W001
     * @return the entry, or null if there is no match
     */
    public Waste findById(String id) {
        if (id == null) {
            return null;
        }
        String wanted = id.trim();
        for (Waste waste : logs) {
            if (waste.getId().equalsIgnoreCase(wanted)) {
                return waste;
            }
        }
        return null;
    }

    /**
     * Deletes one entry by its id.
     *
     * @param id the id to delete, e.g. W001
     * @return true if something was removed, false if no id matched
     */
    public boolean deleteById(String id) {
        Waste target = findById(id);
        return target != null && logs.remove(target);
    }

    /* ---------------------------------------------------------------------
       NUMBERS AND REPORTS
       --------------------------------------------------------------------- */

    /** The number of entries stored. */
    public int size() {
        return logs.size();
    }

    /**
     * A copy of every entry. A copy (not the real list) so the GUI cannot
     * change the collection without going through addWaste / deleteById.
     *
     * @return a new list holding the same entries
     */
    public ArrayList<Waste> getAllLogs() {
        return new ArrayList<Waste>(logs);
    }

    /** The total weight of every entry, in kilograms. */
    public double getTotalWeightKg() {
        double total = 0;
        for (Waste waste : logs) {
            total += waste.getWeightKg();
        }
        return total;
    }

    /** The weight of the recyclable entries only, in kilograms. */
    public double getRecycledWeightKg() {
        double total = 0;
        for (Waste waste : logs) {
            if (waste instanceof RecyclableWaste) {
                total += waste.getWeightKg();
            }
        }
        return total;
    }

    /** The recycled share of the total weight, as a percentage. */
    public double getRecyclingRate() {
        double total = getTotalWeightKg();
        if (total <= 0) {
            return 0;
        }
        return (getRecycledWeightKg() / total) * 100.0;
    }

    /**
     * Adds up the carbon impact of every entry.
     *
     * <p>There is deliberately no if statement in this loop: calculateImpact()
     * is called on a Waste and the right subclass answers on its own. That is
     * polymorphism, and the demo script points at this method.</p>
     *
     * @return the total impact in kg CO2e
     */
    public double getTotalImpact() {
        double total = 0;
        for (Waste waste : logs) {
            total += waste.calculateImpact();
        }
        return total;
    }

    /**
     * Builds the text shown by the Statistics buttons.
     *
     * @return a short report of the numbers
     */
    public String getStatsReport() {
        if (logs.isEmpty()) {
            return "Nothing logged yet, so there are no statistics to show.";
        }
        StringBuilder out = new StringBuilder();
        out.append("Entries logged      : ").append(logs.size()).append(System.lineSeparator());
        out.append(String.format("Total weight        : %.2f kg%n", getTotalWeightKg()));
        out.append(String.format("Recycled weight     : %.2f kg%n", getRecycledWeightKg()));
        out.append(String.format("Recycling rate      : %.1f%%%n", getRecyclingRate()));
        out.append(String.format("Carbon impact saved : %.2f kg CO2e", getTotalImpact()));
        return out.toString();
    }

    /**
     * Builds the text shown by the Disposal Tips button.
     *
     * @return advice for every entry plus the list of recycling centres
     */
    public String getAdviceReport() {
        StringBuilder out = new StringBuilder();
        out.append("DISPOSAL ADVICE").append(System.lineSeparator());
        if (logs.isEmpty()) {
            out.append("  Nothing logged yet.").append(System.lineSeparator());
        } else {
            for (Waste waste : logs) {
                out.append("  ").append(waste.getId()).append(" - ")
                        .append(waste.getDisposalAdvice())
                        .append(System.lineSeparator());
            }
        }
        out.append(System.lineSeparator()).append("RECYCLING CENTRES NEARBY")
                .append(System.lineSeparator());
        for (String[] centre : recyclingCentres) {
            out.append("  ").append(centre[0]).append(" - ").append(centre[1])
                    .append(" - ").append(centre[2])
                    .append(System.lineSeparator());
        }
        return out.toString();
    }

    /* ---------------------------------------------------------------------
       FILE INPUT AND OUTPUT
       --------------------------------------------------------------------- */

    /**
     * Writes every log to a text file, one entry per line.
     *
     * @param path where to write the file
     * @return how many entries were written
     * @throws IOException if the file cannot be written
     */
    public int saveToFile(String path) throws IOException {
        File file = new File(path);
        File folder = file.getParentFile();
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        try {
            out.println("# EcoCycle waste log - saved " + new Date());
            out.println("# id|type|description|weightKg|date|material|clean|compostable");
            for (Waste waste : logs) {
                out.println(waste.toFileRecord());
            }
        } finally {
            out.close();
        }
        return logs.size();
    }

    /**
     * Replaces the collection with the entries from a file.
     *
     * <p>The list is cleared first, so loading twice does not double up.
     * A line that cannot be understood is skipped and the rest of the file
     * still loads -
     * one damaged entry must never lose the whole file.</p>
     *
     * @param path the file to read
     * @return how many entries were loaded
     * @throws FileNotFoundException if there is no file at that path
     * @throws IOException           if the file cannot be read
     */
    public int loadFromFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("No file at " + file.getAbsolutePath());
        }

        logs.clear();

        BufferedReader in = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                String text = line.trim();
                if (text.isEmpty() || text.startsWith("#")) {
                    continue;   // blank lines and comments are not entries
                }
                Waste waste = Waste.fromFileRecord(text);
                if (waste != null) {
                    logs.add(waste);
                }
            }
        } finally {
            in.close();
        }
        return logs.size();
    }
}
