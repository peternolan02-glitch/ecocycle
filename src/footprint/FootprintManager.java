/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - working section
 *
 * Author  : Valentin Gabriel Luchian (24318230)
 * Package : footprint  (Section B)
 * Purpose : Holds every CarbonActivity object in an ArrayList and provides
 *           the Add / Display / Search / Delete operations plus the weekly
 *           goal the GUI needs.
 */
package footprint;

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
import ecocycle.RecordIO;

/**
 * The collection manager for Section B: it owns the ArrayList of activities.
 */
public class FootprintManager {

    /** Where the activities are saved when the user does not choose a name. */
    public static final String DEFAULT_FILE =
            "data" + File.separator + "footprint.txt";

    /**
     * Marks the one line in the file that holds the weekly goal rather than
     * an activity. It is not a "#" comment, because comments are skipped.
     */
    public static final String GOAL_PREFIX = "GOAL|";

    /** The goal the user starts with before they change it. */
    public static final double DEFAULT_GOAL_KG = 50.0;

    /** The in-memory store of logged activities. */
    private final ArrayList<CarbonActivity> activities = new ArrayList<CarbonActivity>();

    /** The user's weekly carbon budget, in kg CO2e. */
    private double weeklyGoalKg = DEFAULT_GOAL_KG;

    /* ---------------------------------------------------------------------
       ADD, DISPLAY, SEARCH, DELETE  (the four operations every section has)
       --------------------------------------------------------------------- */

    /** Adds an activity to the collection. */
    public void addActivity(CarbonActivity activity) {
        activities.add(activity);
    }

    /** The number of activities stored. */
    public int size() {
        return activities.size();
    }

    /**
     * A copy of every activity. A copy (not the real list) so the GUI cannot
     * change the collection without going through addActivity / deleteById.
     */
    public ArrayList<CarbonActivity> getAllActivities() {
        return new ArrayList<CarbonActivity>(activities);
    }

    /**
     * Builds the text shown by the Display All button: one line per
     * activity, or a message if there is nothing stored.
     */
    public String displayAll() {
        if (activities.isEmpty()) {
            return "No activities logged yet.";
        }
        StringBuilder out = new StringBuilder();
        for (CarbonActivity activity : activities) {
            out.append(activity.toString()).append(System.lineSeparator());
        }
        return out.toString();
    }

    /** Searches by the exact date, ignoring spaces and letter case. */
    public ArrayList<CarbonActivity> searchByDate(String date) {
        ArrayList<CarbonActivity> found = new ArrayList<CarbonActivity>();
        if (date == null) {
            return found;
        }
        String wanted = date.trim();
        for (CarbonActivity activity : activities) {
            if (activity.getDate().equalsIgnoreCase(wanted)) {
                found.add(activity);
            }
        }
        return found;
    }

    /** Searches by type, e.g. "Transport" or "Energy". */
    public ArrayList<CarbonActivity> searchByType(String type) {
        ArrayList<CarbonActivity> found = new ArrayList<CarbonActivity>();
        if (type == null) {
            return found;
        }
        String wanted = type.trim();
        for (CarbonActivity activity : activities) {
            if (activity.getType().equalsIgnoreCase(wanted)) {
                found.add(activity);
            }
        }
        return found;
    }

    /**
     * Looks for one activity by its id, e.g. C001. Returns null if there
     * is no match.
     */
    public CarbonActivity findById(String id) {
        if (id == null) {
            return null;
        }
        String wanted = id.trim();
        for (CarbonActivity activity : activities) {
            if (activity.getId().equalsIgnoreCase(wanted)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * Deletes one activity by its id. Returns false if no id matched.
     */
    public boolean deleteById(String id) {
        CarbonActivity target = findById(id);
        if (target == null) {
            return false;
        }
        return activities.remove(target);
    }

    /* ---------------------------------------------------------------------
       NUMBERS AND REPORTS
       --------------------------------------------------------------------- */

    /**
     * Sets the weekly carbon budget. A goal of zero or less is refused
     * with an IllegalArgumentException.
     */
    public void setWeeklyGoalKg(double weeklyGoalKg) {
        if (weeklyGoalKg <= 0) {
            throw new IllegalArgumentException("Weekly goal must be above zero.");
        }
        this.weeklyGoalKg = weeklyGoalKg;
    }

    /** The weekly carbon budget, in kg CO2e. */
    public double getWeeklyGoalKg() {
        return weeklyGoalKg;
    }

    /**
     * Adds up the carbon from every activity.
     *
     * <p>There is deliberately no if statement in this loop: calculateCO2e()
     * is called on a CarbonActivity and the right subclass answers on its
     * own. That is polymorphism, and the demo script points here.</p>
     */
    public double getTotalCO2e() {
        double total = 0;
        for (CarbonActivity activity : activities) {
            total += activity.calculateCO2e();
        }
        return total;
    }

    /** Adds up the carbon from the activities on one given day. */
    public double getCO2eForDate(String date) {
        double total = 0;
        for (CarbonActivity activity : searchByDate(date)) {
            total += activity.calculateCO2e();
        }
        return total;
    }

    /** Counts the activities of one type, "Transport" or "Energy". */
    public int countByType(String type) {
        return searchByType(type).size();
    }

    /**
     * How far along the weekly goal the user is, as a percentage. The value
     * may go over 100 so an over-budget week stays honest.
     */
    public double getGoalProgressPercent() {
        if (weeklyGoalKg <= 0) {
            return 0;
        }
        return (getTotalCO2e() / weeklyGoalKg) * 100.0;
    }

    /** Builds the text shown by the Weekly Total button. */
    public String getProgressReport() {
        StringBuilder out = new StringBuilder();
        out.append("Activities logged : ").append(activities.size())
                .append(System.lineSeparator());
        out.append(String.format("Total carbon      : %.2f kg CO2e%n", getTotalCO2e()));
        out.append(String.format("Weekly goal       : %.2f kg CO2e%n", weeklyGoalKg));
        out.append(String.format("Goal used         : %.1f%%%n", getGoalProgressPercent()));
        out.append(String.format("Transport entries : %d%n", countByType("Transport")));
        out.append(String.format("Energy entries    : %d", countByType("Energy")));
        return out.toString();
    }

    /** Builds the text shown by the Tips button, based on what was logged. */
    public String getTipsReport() {
        StringBuilder out = new StringBuilder();
        out.append("LOW-CARBON TIPS").append(System.lineSeparator());

        if (countByType("Transport") == 0 && countByType("Energy") == 0) {
            out.append("  Log an activity first and your tips will appear here.")
                    .append(System.lineSeparator());
            return out.toString();
        }
        if (countByType("Transport") > 0) {
            out.append("  - Share a car ride or take the bus for short trips.")
                    .append(System.lineSeparator());
            out.append("  - Walking and cycling produce no direct carbon at all.")
                    .append(System.lineSeparator());
        }
        if (countByType("Energy") > 0) {
            out.append("  - Turn off lights and sockets in rooms you are not using.")
                    .append(System.lineSeparator());
            out.append("  - Wash laundry at 30 degrees instead of 40 or hotter.")
                    .append(System.lineSeparator());
        }
        if (getGoalProgressPercent() > 100) {
            out.append("  - You are over your weekly goal. Try one car trip less "
                    + "next week.").append(System.lineSeparator());
        }
        return out.toString();
    }

    /* ---------------------------------------------------------------------
       FILE INPUT AND OUTPUT
       --------------------------------------------------------------------- */

    /**
     * Writes every activity to a text file, one per line, plus the weekly
     * goal line.
     *
     * @return how many activities were written
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
            out.println("# EcoCycle carbon log - saved " + new Date());
            out.println("# id|type|date|amount|mode|passengers|source");
            out.println(GOAL_PREFIX + weeklyGoalKg);
            for (CarbonActivity activity : activities) {
                out.println(activity.toFileRecord());
            }
        } finally {
            out.close();
        }
        return activities.size();
    }

    /**
     * Replaces the collection with the activities from a file. Damaged lines
     * are skipped, and the
     * weekly goal is restored too - a missing or damaged goal line leaves
     * the default goal in place.
     *
     * @return how many activities were loaded
     * @throws FileNotFoundException if there is no file at that path
     * @throws IOException           if the file cannot be read
     */
    public int loadFromFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("No file at " + file.getAbsolutePath());
        }

        activities.clear();
        weeklyGoalKg = DEFAULT_GOAL_KG;

        BufferedReader in = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                String text = line.trim();
                if (text.isEmpty() || text.startsWith("#")) {
                    continue;   // blank lines and comments are not entries
                }
                /* The goal line is settings, not an activity. */
                if (text.startsWith(GOAL_PREFIX)) {
                    readGoal(text);
                    continue;
                }
                CarbonActivity activity = CarbonActivity.fromFileRecord(text);
                if (activity != null) {
                    activities.add(activity);
                }
            }
        } finally {
            in.close();
        }
        return activities.size();
    }

    /**
     * Reads the weekly goal out of its line in the file. Returns false if
     * the number was damaged, leaving the default goal in place.
     */
    private boolean readGoal(String text) {
        String[] parts = RecordIO.split(text);
        if (parts.length < 2) {
            return false;
        }
        try {
            double goal = RecordIO.toDouble(parts[1]);
            if (goal <= 0) {
                return false;
            }
            weeklyGoalKg = goal;
            return true;
        } catch (IllegalArgumentException ex) {
            /* not a number - leave the goal at its default */
            return false;
        }
    }
}
