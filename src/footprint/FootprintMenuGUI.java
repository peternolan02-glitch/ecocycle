/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - working section
 *
 * Author  : Valentin Gabriel Luchian (24318230)
 * Package : footprint  (Section B)
 * Purpose : Section B's three screens in one file: the section menu first,
 *           then the two forms it opens. They hand control to each other, so
 *           they belong together. Only the menu is public - the forms are
 *           package-private because only this section uses them.
 */
package footprint;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ecocycle.AppTheme;
import ecocycle.MainMenuGUI;
import java.awt.FlowLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.util.ArrayList;
import javax.swing.JProgressBar;

/**
 * Menu for Section B (Carbon Footprint Logger).
 */
public class FootprintMenuGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    /** Shared by every screen in this section. */
    private final FootprintManager manager;

    /** Builds the menu around a manager that already exists. */
    public FootprintMenuGUI(FootprintManager manager) {
        super("EcoCycle - Carbon Footprint Logger");
        this.manager = manager;
        loadOnFirstOpen();

        setSize(600, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.PANEL_BG);

        getContentPane().add(
                AppTheme.createHeader("Section B - Carbon Footprint Logger",
                        "Owner: Valentin Gabriel Luchian - log travel and energy, see your CO2e"),
                BorderLayout.NORTH);

        /* The six section buttons, stacked in a column. */
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 8, 8));
        buttonPanel.setBackground(AppTheme.PANEL_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(18, 70, 18, 70));

        JButton addButton = AppTheme.createButton("1. Log an Activity");
        JButton viewButton = AppTheme.createButton("2. Display / Search / Delete");
        JButton totalButton = AppTheme.createButton("3. Weekly Total vs Goal");
        JButton tipsButton = AppTheme.createButton("4. Low-carbon Tips");
        JButton saveButton = AppTheme.createButton("5. Save to File");
        JButton loadButton = AppTheme.createButton("6. Load from File");

        addButton.addActionListener(e -> open(new LogActivityFormGUI(manager)));
        viewButton.addActionListener(e -> open(new GoalSearchFormGUI(manager)));
        totalButton.addActionListener(e -> AppTheme.showReport(this,
                "Weekly Total vs Goal", manager.getProgressReport()));
        tipsButton.addActionListener(e -> AppTheme.showReport(this,
                "Low-carbon Tips", manager.getTipsReport()));
        saveButton.addActionListener(e -> saveToFile());
        loadButton.addActionListener(e -> loadFromFile());

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(totalButton);
        buttonPanel.add(tipsButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        getContentPane().add(buttonPanel, BorderLayout.CENTER);

        JButton backButton = AppTheme.createButton("< Back to Main Menu");
        backButton.addActionListener(e -> {
            new MainMenuGUI().setVisible(true);
            dispose();
        });
        JPanel bottom = new JPanel();
        bottom.setBackground(AppTheme.PANEL_BG);
        bottom.add(backButton);
        getContentPane().add(bottom, BorderLayout.SOUTH);
    }

    /** Also usable on its own, e.g. from tests, with an empty manager. */
    public FootprintMenuGUI() {
        this(new FootprintManager());
    }

    /** Opens another screen and closes this one, so only one window shows. */
    private void open(JFrame nextScreen) {
        nextScreen.setVisible(true);
        dispose();
    }

    /**
     * Loads the saved file the first time the section is opened. If there is
     * no file yet, a few demo activities are added so the screens are not
     * blank during the demo.
     */
    private void loadOnFirstOpen() {
        if (manager.size() > 0) {
            return;
        }
        try {
            manager.loadFromFile(FootprintManager.DEFAULT_FILE);
        } catch (IOException ex) {
            // no file yet, which is normal on the first run
        }
        if (manager.size() == 0) {
            loadSampleData();
        }
    }

    /** Writes every activity to the text file and tells the user what happened. */
    private void saveToFile() {
        try {
            int written = manager.saveToFile(FootprintManager.DEFAULT_FILE);
            JOptionPane.showMessageDialog(this,
                    "Saved " + written + " activities to\n"
                            + new File(FootprintManager.DEFAULT_FILE).getAbsolutePath(),
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save the file:\n" + ex.getMessage(),
                    "Save failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Reads the text file back into the collection, after confirming. */
    private void loadFromFile() {
        int answer = JOptionPane.showConfirmDialog(this,
                "Loading replaces everything currently on screen. Continue?",
                "Confirm load", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int loaded = manager.loadFromFile(FootprintManager.DEFAULT_FILE);
            String message = "Loaded " + loaded + " activities from\n"
                    + new File(FootprintManager.DEFAULT_FILE).getAbsolutePath();
            JOptionPane.showMessageDialog(this, message,
                    "Loaded", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    "There is no saved file yet.\nLog some activities and press "
                            + "\"Save to File\" first.",
                    "Nothing to load", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not read the file:\n" + ex.getMessage(),
                    "Load failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Adds a few activities so the screens are not empty on the first run. */
    private void loadSampleData() {
        manager.addActivity(new TransportActivity("Car", 18.0, 1, "2026-10-11"));
        manager.addActivity(new EnergyActivity("Electricity", 9.0, "2026-10-13"));
        manager.addActivity(new TransportActivity("Train", 40.0, 1, "2026-10-15"));
    }
}

// LogActivityFormGUI - package-private form screen of this section (see the file header).
/**
 * Form used to log a transport or energy activity.
 */
class LogActivityFormGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    /** The manager this form adds activities to. */
    private final FootprintManager manager;

    /** Transport or energy - decides which subclass gets built. */
    private final JComboBox<String> categoryCombo = new JComboBox<String>(
            new String[]{"Transport", "Energy"});
    /** Only used when the category is Transport. */
    private final JComboBox<String> modeCombo =
            new JComboBox<String>(TransportActivity.getSupportedModes());
    /** Only used when the category is Transport. */
    private final JTextField passengerField = new JTextField(6);
    /** Only used when the category is Energy. */
    private final JComboBox<String> energyCombo =
            new JComboBox<String>(EnergyActivity.getSupportedSources());
    /** Distance travelled in km, or energy used in kWh. */
    private final JTextField amountField = new JTextField(10);
    /** Day of the activity, yyyy-mm-dd. */
    private final JTextField dateField = new JTextField(10);
    /** Weekly reduction goal in kg CO2e. */
    private final JTextField goalField = new JTextField(10);

    /** Shows what has been logged so far in this session. */
    private final DefaultListModel<String> listModel = new DefaultListModel<String>();
    private final JList<String> activityList = new JList<String>(listModel);
    private final JLabel countLabel = new JLabel();

    /** Builds the log-activity form. */
    public LogActivityFormGUI(FootprintManager manager) {
        super("EcoCycle - Log an Activity");
        this.manager = manager;

        setSize(620, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.PANEL_BG);

        getContentPane().add(
                AppTheme.createHeader("Log an Activity",
                        "Section B - record travel or household energy use"),
                BorderLayout.NORTH);

        /* The form: one row per field, label on the left, input on the right. */
        JPanel form = AppTheme.formPanel();
        AppTheme.addFormRow(form, "Category:", categoryCombo);
        AppTheme.addFormRow(form, "Travel mode:", modeCombo);
        AppTheme.addFormRow(form, "Passengers:", passengerField);
        AppTheme.addFormRow(form, "Energy source:", energyCombo);
        AppTheme.addFormRow(form, "Distance (km) / kWh:", amountField);
        AppTheme.addFormRow(form, "Date:", dateField);
        AppTheme.addFormRow(form, "Weekly goal (kg CO2e):", goalField);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(AppTheme.PANEL_BG);
        center.add(form, BorderLayout.NORTH);
        center.add(createButtonPanel(), BorderLayout.CENTER);
        getContentPane().add(center, BorderLayout.CENTER);

        /* The list of everything already logged. */
        activityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activityList.setVisibleRowCount(4);

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(AppTheme.PANEL_BG);
        south.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
        south.add(countLabel, BorderLayout.NORTH);
        south.add(new JScrollPane(activityList), BorderLayout.CENTER);
        getContentPane().add(south, BorderLayout.SOUTH);

        refreshList();
    }

    /** The Save / Clear / Back buttons under the form fields. */
    private JPanel createButtonPanel() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        buttons.setBackground(AppTheme.PANEL_BG);

        JButton saveButton = AppTheme.createButton("Save Activity");
        JButton clearButton = AppTheme.createButton("Clear");
        JButton backButton = AppTheme.createButton("< Back to Section Menu");

        saveButton.addActionListener(e -> saveActivity());
        clearButton.addActionListener(e -> clearFields());
        backButton.addActionListener(e -> {
            new FootprintMenuGUI(manager).setVisible(true);
            dispose();
        });

        buttons.add(saveButton);
        buttons.add(clearButton);
        buttons.add(backButton);
        return buttons;
    }

    /**
     * Reads the fields, creates the correct object and stores it. Wrong
     * input never crashes the app - the user always gets a message.
     */
    private void saveActivity() {
        String amountText = amountField.getText().trim();
        String date = dateField.getText().trim();

        if (amountText.isEmpty() || date.isEmpty()) {
            showError("Please fill in the amount and the date.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            showError("The amount must be a number, not '" + amountText + "'.");
            return;
        }

        /* The goal box is optional - only update it if something was typed. */
        String goalText = goalField.getText().trim();
        if (!goalText.isEmpty()) {
            try {
                manager.setWeeklyGoalKg(Double.parseDouble(goalText));
            } catch (NumberFormatException ex) {
                showError("The weekly goal must be a number.");
                return;
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
                return;
            }
        }

        try {
            CarbonActivity activity;
            if ("Transport".equals(categoryCombo.getSelectedItem())) {
                int passengers = 1;
                String passengerText = passengerField.getText().trim();
                if (!passengerText.isEmpty()) {
                    passengers = Integer.parseInt(passengerText);
                }
                activity = new TransportActivity((String) modeCombo.getSelectedItem(),
                        amount, passengers, date);
            } else {
                activity = new EnergyActivity((String) energyCombo.getSelectedItem(),
                        amount, date);
            }
            manager.addActivity(activity);
            refreshList();
            clearFields();
            String message = "Added " + activity.getId() + "\nCarbon produced: "
                    + String.format("%.2f kg CO2e", activity.calculateCO2e())
                    + "\nRunning total: "
                    + String.format("%.2f kg CO2e", manager.getTotalCO2e());
            /* If a new goal was typed in, write it to the file straight away. */
            if (!goalText.isEmpty()) {
                message += saveGoalToFile();
            }
            JOptionPane.showMessageDialog(this, message,
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            showError("The passenger count must be a whole number.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Writes the file after the weekly goal has been changed.
     *
     * @return a short note for the confirmation box saying what happened
     */
    private String saveGoalToFile() {
        try {
            manager.saveToFile(FootprintManager.DEFAULT_FILE);
            return "\n\nNew weekly goal saved to file.";
        } catch (IOException ex) {
            /* The goal is still set for this session, so say so honestly. */
            return "\n\nGoal set, but the file could not be written:\n"
                    + ex.getMessage();
        }
    }

    /** Empties every input field. */
    private void clearFields() {
        amountField.setText("");
        dateField.setText("");
        goalField.setText("");
        passengerField.setText("");
        categoryCombo.setSelectedIndex(0);
        modeCombo.setSelectedIndex(0);
        energyCombo.setSelectedIndex(0);
    }

    /** Rebuilds the list from the manager so it always matches the data. */
    private void refreshList() {
        listModel.clear();
        for (CarbonActivity activity : manager.getAllActivities()) {
            listModel.addElement(activity.toString());
        }
        countLabel.setText("Logged so far (" + manager.size() + "):");
    }

    /** Shows a message to the user. */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Cannot save", JOptionPane.WARNING_MESSAGE);
    }
}

// GoalSearchFormGUI - package-private form screen of this section (see the file header).
/**
 * Screen used to display, search and delete logged activities.
 */
class GoalSearchFormGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    /** The manager holding the activities. */
    private final FootprintManager manager;

    /** The rows currently shown on screen. */
    private final DefaultListModel<String> listModel = new DefaultListModel<String>();
    private final JList<String> activityList = new JList<String>(listModel);
    private final JLabel captionLabel = new JLabel();

    /** What to search by, and the word to search for. */
    private final JComboBox<String> searchByCombo = new JComboBox<String>(
            new String[]{"Date", "Type"});
    private final JTextField searchField = new JTextField(14);

    /** Shows how much of the weekly goal has been used. */
    private final JProgressBar goalBar = new JProgressBar(0, 150);
    private final JLabel goalLabel = new JLabel();

    /** Builds the display / search / delete screen. */
    public GoalSearchFormGUI(FootprintManager manager) {
        super("EcoCycle - Display / Search / Delete");
        this.manager = manager;

        setSize(650, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.PANEL_BG);

        getContentPane().add(
                AppTheme.createHeader("Display / Search / Delete",
                        "Section B - review your activities against your goal"),
                BorderLayout.NORTH);

        activityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        /* Top: the search row, then a caption explaining what is on screen. */
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchRow.setBackground(AppTheme.PANEL_BG);
        searchRow.setBorder(BorderFactory.createEmptyBorder(12, 26, 6, 26));
        searchRow.add(new JLabel("Search by:"));
        searchRow.add(searchByCombo);
        searchRow.add(searchField);

        captionLabel.setBorder(BorderFactory.createEmptyBorder(4, 30, 2, 30));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppTheme.PANEL_BG);
        topPanel.add(searchRow, BorderLayout.NORTH);
        topPanel.add(captionLabel, BorderLayout.SOUTH);

        /* Goal panel: the bar plus a caption that spells the numbers out. */
        goalBar.setStringPainted(false);
        JPanel goalPanel = new JPanel(new BorderLayout(4, 4));
        goalPanel.setBackground(AppTheme.PANEL_BG);
        goalPanel.setBorder(BorderFactory.createTitledBorder("Weekly goal"));
        goalPanel.add(goalBar, BorderLayout.CENTER);
        goalPanel.add(goalLabel, BorderLayout.SOUTH);

        JPanel centre = new JPanel(new BorderLayout(5, 5));
        centre.setBackground(AppTheme.PANEL_BG);
        centre.add(topPanel, BorderLayout.NORTH);
        centre.add(new JScrollPane(activityList), BorderLayout.CENTER);
        centre.add(goalPanel, BorderLayout.SOUTH);
        getContentPane().add(centre, BorderLayout.CENTER);

        /* Bottom: the action buttons. */
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        actions.setBackground(AppTheme.PANEL_BG);

        JButton displayButton = AppTheme.createButton("Display All");
        JButton searchButton = AppTheme.createButton("Search");
        JButton deleteButton = AppTheme.createButton("Delete Selected");
        JButton goalButton = AppTheme.createButton("Set Goal");
        JButton backButton = AppTheme.createButton("< Back to Section Menu");

        displayButton.addActionListener(e -> displayAll());
        searchButton.addActionListener(e -> search());
        deleteButton.addActionListener(e -> deleteSelected());
        goalButton.addActionListener(e -> setGoal());
        backButton.addActionListener(e -> {
            new FootprintMenuGUI(manager).setVisible(true);
            dispose();
        });

        actions.add(displayButton);
        actions.add(searchButton);
        actions.add(deleteButton);
        actions.add(goalButton);
        actions.add(backButton);
        getContentPane().add(actions, BorderLayout.SOUTH);

        displayAll();
    }

    /** Fills the list with every activity the manager holds. */
    private void displayAll() {
        fill(manager.getAllActivities(),
                "Showing all " + manager.size() + " activities");
    }

    /** Searches using whichever field the drop-down has selected. */
    private void search() {
        String by = (String) searchByCombo.getSelectedItem();
        String term = searchField.getText().trim();

        if (term.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Type something to search for first.",
                    "Nothing to search", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<CarbonActivity> found;
        if ("Date".equals(by)) {
            found = manager.searchByDate(term);
        } else {
            found = manager.searchByType(term);
        }
        fill(found, found.size() + " result(s) for " + by.toLowerCase()
                + " = '" + term + "'");
    }

    /** Deletes the activity the user has highlighted, after checking with them. */
    private void deleteSelected() {
        String selected = activityList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a row in the list first.",
                    "Nothing selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        /* The id is always the first part of the line, e.g. "C001 - ...". */
        String id = selected.split(" - ")[0];

        int answer = JOptionPane.showConfirmDialog(this,
                "Delete activity " + id + "?\n" + selected,
                "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        if (manager.deleteById(id)) {
            displayAll();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Could not find " + id + " any more.",
                    "Delete failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Asks the user for a new weekly goal and saves it. */
    private void setGoal() {
        String text = JOptionPane.showInputDialog(this,
                "Weekly goal in kg CO2e (current: "
                        + String.format("%.2f", manager.getWeeklyGoalKg()) + "):");
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        try {
            manager.setWeeklyGoalKg(Double.parseDouble(text.trim()));
            displayAll();
            /* Save straight away so the goal is not lost if the app is closed. */
            saveGoalToFile();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "The goal must be a number.",
                    "Cannot save", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Cannot save", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Writes the file after the goal has been changed. If the save fails the
     * goal is still set for this session, so the user is warned.
     */
    private void saveGoalToFile() {
        try {
            manager.saveToFile(FootprintManager.DEFAULT_FILE);
            JOptionPane.showMessageDialog(this,
                    "Weekly goal is now "
                            + String.format("%.2f", manager.getWeeklyGoalKg())
                            + " kg CO2e and has been saved to\n"
                            + new File(FootprintManager.DEFAULT_FILE).getAbsolutePath(),
                    "Goal saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "The goal is set for this session, but the file could not "
                            + "be written:\n" + ex.getMessage(),
                    "Saved in memory only", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Puts a list of activities into the JList and updates the caption. */
    private void fill(ArrayList<CarbonActivity> activities, String caption) {
        listModel.clear();
        for (CarbonActivity activity : activities) {
            listModel.addElement(activity.toString());
        }
        if (activities.isEmpty()) {
            listModel.addElement("(nothing to show)");
        }
        captionLabel.setText(caption);
        refreshGoalBar();
    }

    /** Recalculates the goal bar and its caption from the manager. */
    private void refreshGoalBar() {
        double percent = manager.getGoalProgressPercent();
        /* The bar tops out at 150% so an over-budget week is visible. */
        goalBar.setValue((int) Math.min(150, Math.max(0, percent)));
        goalLabel.setText(String.format("Total %.2f kg CO2e of a %.2f kg goal (%.1f%%)",
                manager.getTotalCO2e(), manager.getWeeklyGoalKg(), percent));
    }
}
