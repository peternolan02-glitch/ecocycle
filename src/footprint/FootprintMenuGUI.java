/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 2 - GUI skeleton
 *
 * Author  : Valentin Gabriel Luchian / 24318230   (replace before submission)
 * Package : footprint  (Section B)
 * Purpose : Section B's three screens in one file: the section menu first,
 *           then the two forms it opens. Only the menu is public - the forms
 *           are package-private because only this section uses them.
 */
package footprint;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import ecocycle.AppTheme;
import ecocycle.MainMenuGUI;

/**
 * Menu for Section B (Carbon Footprint Logger).
 */
public class FootprintMenuGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    /** Builds the section menu window. */
    public FootprintMenuGUI() {
        super("EcoCycle - Carbon Footprint Logger");
        AppTheme.setup(this, 600, 460, "Section B - Carbon Footprint Logger",
                "Owner: Valentin Gabriel Luchian - log travel and energy, track your CO2e");
        add(AppTheme.buttonColumn(
                AppTheme.button("1. Log an Activity",
                        () -> AppTheme.open(this, new LogActivityFormGUI())),
                AppTheme.button("2. Display / Search / Delete",
                        () -> AppTheme.open(this, new GoalSearchFormGUI())),
                AppTheme.button("3. Weekly Total (Checkpoint 3)",
                        () -> AppTheme.showNotYet(this, "The weekly total")),
                AppTheme.button("4. Low-carbon Tips (Checkpoint 3)",
                        () -> AppTheme.showNotYet(this, "Low-carbon tips"))),
                BorderLayout.CENTER);
        add(AppTheme.buttonRow(AppTheme.button("< Back to Main Menu",
                () -> AppTheme.open(this, new MainMenuGUI()))), BorderLayout.SOUTH);
    }
}

// LogActivityFormGUI - package-private form screen of this section (see the file header).
/**
 * Form used to log a travel or energy activity.
 */
class LogActivityFormGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    /** The input fields, in the order they appear on screen. */
    private final JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Transport", "Energy"});
    private final JComboBox<String> modeCombo = new JComboBox<>(new String[]{"Car", "Bus", "Train", "Bike", "Walk"});
    private final JTextField passengerField = new JTextField(6);
    private final JComboBox<String> energyCombo = new JComboBox<>(new String[]{"Electricity", "Gas", "Heating Oil"});
    private final JTextField amountField = new JTextField(10);
    private final JTextField dateField = new JTextField(10);

    /** Builds the log-activity form. */
    public LogActivityFormGUI() {
        super("EcoCycle - Log an Activity");
        AppTheme.setup(this, 620, 500, "Log an Activity", "Section B - record a journey or an energy bill");

        JPanel form = AppTheme.formPanel();
        AppTheme.addFormRow(form, "Category:", categoryCombo);
        AppTheme.addFormRow(form, "Travel mode:", modeCombo);
        AppTheme.addFormRow(form, "Passengers:", passengerField);
        AppTheme.addFormRow(form, "Energy source:", energyCombo);
        AppTheme.addFormRow(form, "Distance (km) / kWh:", amountField);
        AppTheme.addFormRow(form, "Date:", dateField);

        add(AppTheme.stack(form, AppTheme.buttonRow(
                AppTheme.button("Save Activity", () -> AppTheme.showNotYet(this, "Saving activities")),
                AppTheme.button("< Back to Section Menu",
                        () -> AppTheme.open(this, new FootprintMenuGUI())))), BorderLayout.CENTER);
    }
}

// GoalSearchFormGUI - package-private form screen of this section (see the file header).
/**
 * Screen used to display, search and delete activities.
 */
class GoalSearchFormGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    /** What to search by, and the word to search for. */
    private final JComboBox<String> searchByCombo = new JComboBox<>(new String[]{"Date", "Type"});
    private final JTextField searchField = new JTextField(14);

    /** Builds the display / search / delete screen. */
    public GoalSearchFormGUI() {
        super("EcoCycle - Display / Search / Delete");
        AppTheme.setup(this, 650, 520, "Display / Search / Delete",
                "Section B - review your activities and weekly goal");

        add(AppTheme.stack(AppTheme.searchRow(searchByCombo, searchField),
                new JScrollPane(new JList<String>())), BorderLayout.CENTER);
        add(AppTheme.buttonRow(
                AppTheme.button("Display All", () -> AppTheme.showNotYet(this, "Displaying activities")),
                AppTheme.button("Search", () -> AppTheme.showNotYet(this, "Searching")),
                AppTheme.button("Delete Selected", () -> AppTheme.showNotYet(this, "Deleting activities")),
                AppTheme.button("Set Goal", () -> AppTheme.showNotYet(this, "Setting the weekly goal")),
                AppTheme.button("< Back to Section Menu",
                        () -> AppTheme.open(this, new FootprintMenuGUI()))), BorderLayout.SOUTH);
    }
}
