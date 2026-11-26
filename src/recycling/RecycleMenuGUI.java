/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - individual section (Section A)
 *
 * Author  : Jamie Stackpoole (25130340)
 * Package : recycling  (Section A)
 * Purpose : Section A's three screens in one file: the section menu first,
 *           then the two forms it opens. They hand control to each other, so
 *           they belong together. Only the menu is public - the forms are
 *           package-private because only this section uses them.
 */
package recycling;

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
import java.awt.FlowLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ListSelectionModel;
import java.util.ArrayList;

/**
 * Menu for Section A (Waste & Recycling Tracker).
 */
public class RecycleMenuGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    /** Shared by every screen in this section. */
    private final WasteManager manager;

    /** Builds the menu around a manager that already exists. */
    public RecycleMenuGUI(WasteManager manager) {
        super("EcoCycle - Waste & Recycling Tracker");
        this.manager = manager;
        loadOnFirstOpen();

        setSize(600, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.PANEL_BG);

        getContentPane().add(
                AppTheme.createHeader("Section A - Waste & Recycling Tracker",
                        "Owner: Jamie Stackpoole - log waste, get advice, see your stats"),
                BorderLayout.NORTH);

        /* The six section buttons, stacked in a column. */
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 8, 8));
        buttonPanel.setBackground(AppTheme.PANEL_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(18, 70, 18, 70));

        JButton addButton = AppTheme.createButton("1. Add Waste Log");
        JButton viewButton = AppTheme.createButton("2. Display / Search / Delete");
        JButton adviceButton = AppTheme.createButton("3. Disposal Tips & Centres");
        JButton statsButton = AppTheme.createButton("4. Waste Statistics");
        JButton saveButton = AppTheme.createButton("5. Save to File");
        JButton loadButton = AppTheme.createButton("6. Load from File");

        addButton.addActionListener(e -> open(new AddWasteFormGUI(manager)));
        viewButton.addActionListener(e -> open(new WasteSearchFormGUI(manager)));
        adviceButton.addActionListener(e -> AppTheme.showReport(this,
                "Disposal Tips & Centres", manager.getAdviceReport()));
        statsButton.addActionListener(e -> AppTheme.showReport(this,
                "Waste Statistics", manager.getStatsReport()));
        saveButton.addActionListener(e -> saveToFile());
        loadButton.addActionListener(e -> loadFromFile());

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(adviceButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        getContentPane().add(buttonPanel, BorderLayout.CENTER);

        JButton backButton = AppTheme.createButton("Exit");
        backButton.addActionListener(e -> dispose());
        JPanel bottom = new JPanel();
        bottom.setBackground(AppTheme.PANEL_BG);
        bottom.add(backButton);
        getContentPane().add(bottom, BorderLayout.SOUTH);
    }

    /** Also usable on its own, e.g. from tests, with an empty manager. */
    public RecycleMenuGUI() {
        this(new WasteManager());
    }

    /**
     * Opens another screen and closes this one, so only one window is on
     * screen at a time.
     *
     * @param nextScreen the window to show
     */
    private void open(JFrame nextScreen) {
        nextScreen.setVisible(true);
        dispose();
    }

    /**
     * Loads the saved file the first time the section is opened. If there is
     * no file yet (or it is empty) a few demo entries are added, so the
     * screens are not blank during the demo.
     */
    private void loadOnFirstOpen() {
        if (manager.size() > 0) {
            return;
        }
        try {
            manager.loadFromFile(WasteManager.DEFAULT_FILE);
        } catch (IOException ex) {
            // no file yet, which is normal on the first run
        }
        if (manager.size() == 0) {
            manager.addWaste(new RecyclableWaste("Glass jar", "Glass", 0.4,
                    "2026-10-12", true));
            manager.addWaste(new OrganicWaste("Vegetable scraps", 1.1,
                    "2026-10-14", true));
            manager.addWaste(new RecyclableWaste("Cardboard box", "Cardboard", 0.25,
                    "2026-10-16", false));
        }
    }

    /** Writes every log to the text file and tells the user what happened. */
    private void saveToFile() {
        try {
            int written = manager.saveToFile(WasteManager.DEFAULT_FILE);
            JOptionPane.showMessageDialog(this,
                    "Saved " + written + " entries to\n"
                            + new File(WasteManager.DEFAULT_FILE).getAbsolutePath(),
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
            int loaded = manager.loadFromFile(WasteManager.DEFAULT_FILE);
            String message = "Loaded " + loaded + " entries from\n"
                    + new File(WasteManager.DEFAULT_FILE).getAbsolutePath();
            JOptionPane.showMessageDialog(this, message,
                    "Loaded", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    "There is no saved file yet.\nAdd some entries and press "
                            + "\"Save to File\" first.",
                    "Nothing to load", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not read the file:\n" + ex.getMessage(),
                    "Load failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// AddWasteFormGUI - package-private form screen of this section (see the file header).
/**
 * Form used to add a waste log entry.
 */
class AddWasteFormGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    /** The manager this form adds entries to. */
    private final WasteManager manager;

    /** The input fields, in the order they appear on screen. */
    private final JTextField descriptionField = new JTextField(18);
    private final JComboBox<String> typeCombo = new JComboBox<String>(
            new String[]{"Recyclable", "Organic"});
    private final JTextField materialField = new JTextField(14);
    private final JCheckBox cleanBox = new JCheckBox("Already cleaned");
    private final JCheckBox compostableBox = new JCheckBox("Compostable at home", true);
    private final JTextField weightField = new JTextField(8);
    private final JTextField dateField = new JTextField(10);

    /** Shows what has been logged so far in this session. */
    private final DefaultListModel<String> listModel = new DefaultListModel<String>();
    private final JList<String> logList = new JList<String>(listModel);
    private final JLabel countLabel = new JLabel();

    /** Builds the add-waste form. */
    public AddWasteFormGUI(WasteManager manager) {
        super("EcoCycle - Add Waste Log");
        this.manager = manager;

        setSize(620, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.PANEL_BG);

        getContentPane().add(
                AppTheme.createHeader("Add Waste Log",
                        "Section A - record an item you threw away"),
                BorderLayout.NORTH);

        /* The form: one row per field, label on the left, input on the right. */
        JPanel form = AppTheme.formPanel();
        AppTheme.addFormRow(form, "Description:", descriptionField);
        AppTheme.addFormRow(form, "Type:", typeCombo);
        AppTheme.addFormRow(form, "Material:", materialField);
        AppTheme.addFormRow(form, "Cleaned?", cleanBox);
        AppTheme.addFormRow(form, "Compostable?", compostableBox);
        AppTheme.addFormRow(form, "Weight (kg):", weightField);
        AppTheme.addFormRow(form, "Date:", dateField);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(AppTheme.PANEL_BG);
        center.add(form, BorderLayout.NORTH);
        center.add(createButtonPanel(), BorderLayout.CENTER);
        getContentPane().add(center, BorderLayout.CENTER);

        /* The list of everything already logged. */
        logList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logList.setVisibleRowCount(4);

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(AppTheme.PANEL_BG);
        south.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
        south.add(countLabel, BorderLayout.NORTH);
        south.add(new JScrollPane(logList), BorderLayout.CENTER);
        getContentPane().add(south, BorderLayout.SOUTH);

        refreshList();
    }

    /** The Save / Clear / Back buttons under the form fields. */
    private JPanel createButtonPanel() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        buttons.setBackground(AppTheme.PANEL_BG);

        JButton saveButton = AppTheme.createButton("Save Entry");
        JButton clearButton = AppTheme.createButton("Clear");
        JButton backButton = AppTheme.createButton("< Back to Section Menu");

        saveButton.addActionListener(e -> saveEntry());
        clearButton.addActionListener(e -> clearFields());
        backButton.addActionListener(e -> {
            new RecycleMenuGUI(manager).setVisible(true);
            dispose();
        });

        buttons.add(saveButton);
        buttons.add(clearButton);
        buttons.add(backButton);
        return buttons;
    }

    /**
     * Reads the fields, creates the correct object and stores it.
     *
     * <p>Two things can go wrong: the weight may not be a number, and the
     * constructor may reject the values. Both are caught here so the app
     * never crashes and the user always gets a useful message.</p>
     */
    private void saveEntry() {
        String description = descriptionField.getText().trim();
        String material = materialField.getText().trim();
        String weightText = weightField.getText().trim();
        String date = dateField.getText().trim();

        if (description.isEmpty() || weightText.isEmpty() || date.isEmpty()) {
            showError("Please fill in Description, Weight and Date.");
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightText);
        } catch (NumberFormatException ex) {
            showError("The weight must be a number, not '" + weightText + "'.");
            return;
        }

        try {
            Waste waste;
            if ("Recyclable".equals(typeCombo.getSelectedItem())) {
                waste = new RecyclableWaste(description, material, weight, date,
                        cleanBox.isSelected());
            } else {
                waste = new OrganicWaste(description, weight, date,
                        compostableBox.isSelected());
            }
            manager.addWaste(waste);
            refreshList();
            clearFields();
            JOptionPane.showMessageDialog(this,
                    "Added " + waste.getId() + "\n" + waste.getDisposalAdvice()
                            + "\nImpact: " + String.format("%.2f kg CO2e", waste.calculateImpact()),
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /** Empties every input field. */
    private void clearFields() {
        descriptionField.setText("");
        materialField.setText("");
        weightField.setText("");
        dateField.setText("");
        cleanBox.setSelected(false);
        compostableBox.setSelected(true);
        typeCombo.setSelectedIndex(0);
    }

    /** Rebuilds the list from the manager so it always matches the data. */
    private void refreshList() {
        listModel.clear();
        for (Waste waste : manager.getAllLogs()) {
            listModel.addElement(waste.toString());
        }
        countLabel.setText("Logged so far (" + manager.size() + "):");
    }

    /** Shows a message to the user. */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Cannot save", JOptionPane.WARNING_MESSAGE);
    }
}

// WasteSearchFormGUI - package-private form screen of this section (see the file header).
/**
 * Screen used to display, search and delete waste logs.
 */
class WasteSearchFormGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    /** The manager holding the entries. */
    private final WasteManager manager;

    /** The rows currently shown on screen. */
    private final DefaultListModel<String> listModel = new DefaultListModel<String>();
    private final JList<String> logList = new JList<String>(listModel);
    private final JLabel captionLabel = new JLabel();

    /** What to search by, and the word to search for. */
    private final JComboBox<String> searchByCombo = new JComboBox<String>(
            new String[]{"Type", "Description", "Date"});
    private final JTextField searchField = new JTextField(14);

    /** Builds the display / search / delete screen. */
    public WasteSearchFormGUI(WasteManager manager) {
        super("EcoCycle - Display / Search / Delete");
        this.manager = manager;

        setSize(650, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.PANEL_BG);

        getContentPane().add(
                AppTheme.createHeader("Display / Search / Delete",
                        "Section A - review and tidy up your waste logs"),
                BorderLayout.NORTH);

        logList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        /* Top: the search row, then a caption explaining what is on screen. */
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchRow.setBackground(AppTheme.PANEL_BG);
        searchRow.setBorder(BorderFactory.createEmptyBorder(12, 26, 6, 26));
        searchRow.add(new JLabel("Search by:"));
        searchRow.add(searchByCombo);
        searchRow.add(searchField);

        captionLabel.setBorder(BorderFactory.createEmptyBorder(6, 30, 2, 30));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(AppTheme.PANEL_BG);
        top.add(searchRow, BorderLayout.NORTH);
        top.add(captionLabel, BorderLayout.SOUTH);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(AppTheme.PANEL_BG);
        centre.add(top, BorderLayout.NORTH);
        centre.add(new JScrollPane(logList), BorderLayout.CENTER);
        getContentPane().add(centre, BorderLayout.CENTER);

        /* Bottom: the action buttons. */
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        actions.setBackground(AppTheme.PANEL_BG);

        JButton displayButton = AppTheme.createButton("Display All");
        JButton searchButton = AppTheme.createButton("Search");
        JButton deleteButton = AppTheme.createButton("Delete Selected");
        JButton statsButton = AppTheme.createButton("Statistics");
        JButton backButton = AppTheme.createButton("< Back to Section Menu");

        displayButton.addActionListener(e -> displayAll());
        searchButton.addActionListener(e -> search());
        deleteButton.addActionListener(e -> deleteSelected());
        statsButton.addActionListener(e -> AppTheme.showReport(this,
                "Waste Statistics", manager.getStatsReport()));
        backButton.addActionListener(e -> {
            new RecycleMenuGUI(manager).setVisible(true);
            dispose();
        });

        actions.add(displayButton);
        actions.add(searchButton);
        actions.add(deleteButton);
        actions.add(statsButton);
        actions.add(backButton);
        getContentPane().add(actions, BorderLayout.SOUTH);

        displayAll();
    }

    /** Fills the list with every entry the manager holds. */
    private void displayAll() {
        fill(manager.getAllLogs(), "Showing all " + manager.size() + " entries");
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

        ArrayList<Waste> found;
        if ("Type".equals(by)) {
            found = manager.searchByType(term);
        } else if ("Date".equals(by)) {
            found = manager.searchByDate(term);
        } else {
            found = manager.searchByDescription(term);
        }
        fill(found, found.size() + " result(s) for " + by.toLowerCase()
                + " = '" + term + "'");
    }

    /** Deletes the entry the user has highlighted, after checking with them. */
    private void deleteSelected() {
        String selected = logList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a row in the list first.",
                    "Nothing selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        /* The id is always the first part of the line, e.g. "W001 - ...". */
        String id = selected.split(" - ")[0];

        int answer = JOptionPane.showConfirmDialog(this,
                "Delete entry " + id + "?\n" + selected,
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

    /** Puts a list of objects into the JList and updates the caption. */
    private void fill(ArrayList<Waste> entries, String caption) {
        listModel.clear();
        for (Waste waste : entries) {
            listModel.addElement(waste.toString());
        }
        if (entries.isEmpty()) {
            listModel.addElement("(nothing to show)");
        }
        captionLabel.setText(caption);
    }
}

// WasteTrackerApp - package-private launcher: all it does is open the section menu.
/**
 * The entry point of this project. Opens the Section A menu.
 */
class WasteTrackerApp {

    /**
     * Starts the program on Swing's Event Dispatch Thread.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RecycleMenuGUI().setVisible(true));
    }
}
