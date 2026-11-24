/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - Section C (Reuse Marketplace)
 *
 * Author  : Peter Nolan (24338893)
 * Package : marketplace
 * Purpose : User interface for Section C. Contains the main section menu
 *           and the two user-input forms (PostListingFormGUI and BrowseListingFormGUI).
 */
package marketplace; // places this inside the marketplace package

import java.awt.BorderLayout; // layout manager for north/south/center positioning
import java.awt.FlowLayout; // layout manager for placing buttons in a neat row
import java.awt.GridLayout; // layout manager for grids and vertical button stacks
import java.io.File; // handles file paths for saving and loading messages
import java.io.FileNotFoundException; // error thrown if the saved file does not exist
import java.io.IOException; // general file I/O error
import java.time.LocalDate; // automatically gets today's date for new listings
import java.util.ArrayList; // list used for passing search and filter results into the GUI
import javax.swing.BorderFactory; // creates empty padding borders around panels
import javax.swing.DefaultListModel; // data model backing the JList on screen
import javax.swing.JButton; // clickable GUI buttons
import javax.swing.JComboBox; // dropdown menus for categories and conditions
import javax.swing.JFrame; // top-level window container
import javax.swing.JLabel; // text labels on screen
import javax.swing.JList; // scrollable list showing active listings
import javax.swing.JOptionPane; // popup dialogs for messages, errors, and input
import javax.swing.JPanel; // container panel for grouping components
import javax.swing.JScrollPane; // adds scrollbars around lists and text areas
import javax.swing.JTextField; // single-line text input fields
import javax.swing.ListSelectionModel; // restricts JList to single-item selection
import ecocycle.AppTheme; // our shared styling helper
import ecocycle.MainMenuGUI; // the landing menu we return to on Back

// Main section menu window for Section C (Reuse Marketplace)
public class MarketplaceMenuGUI extends JFrame {

    private final MarketplaceManager manager; // reference to the shared collection manager

    // Constructor creates a new manager if opened on its own (e.g. from main)
    public MarketplaceMenuGUI() {
        this(new MarketplaceManager());
    }

    // Constructor receiving a manager so data is shared across screens in this section
    public MarketplaceMenuGUI(MarketplaceManager manager) {
        super("EcoCycle - Reuse Marketplace"); // window title
        this.manager = manager;

        // Automatically load saved file on startup, or fall back to sample items if no file exists
        loadInitialData();

        setSize(600, 560); // window dimensions
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centres the window on screen
        getContentPane().setBackground(AppTheme.PANEL_BG); // sets uniform background colour

        // Header banner at top of window
        getContentPane().add(
                AppTheme.createHeader("Section C - Reuse Marketplace",
                        "Owner: Peter Nolan - donate or swap items to keep them out of landfill"),
                BorderLayout.NORTH);

        // Grid panel holding the 6 section feature buttons stacked vertically
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 8, 8));
        buttonPanel.setBackground(AppTheme.PANEL_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(18, 70, 18, 70)); // side margins

        JButton postBtn = AppTheme.createButton("1. Post a Listing");
        JButton browseBtn = AppTheme.createButton("2. Browse / Search / Claim / Delete");
        JButton statsBtn = AppTheme.createButton("3. Marketplace Statistics");
        JButton myListingsBtn = AppTheme.createButton("4. My Listings");
        JButton saveBtn = AppTheme.createButton("5. Save to File");
        JButton loadBtn = AppTheme.createButton("6. Load from File");

        // Button 1: Opens Form 1 (Post a Listing) and closes this menu
        postBtn.addActionListener(e -> {
            new PostListingFormGUI(manager).setVisible(true);
            dispose();
        });

        // Button 2: Opens Form 2 (Browse, Search, Claim, Delete) and closes this menu
        browseBtn.addActionListener(e -> {
            new BrowseListingFormGUI(manager).setVisible(true);
            dispose();
        });

        // Button 3: Runs the 30-day expiry check and displays the stats report popup
        statsBtn.addActionListener(e -> {
            manager.checkAndExpireListings();
            AppTheme.showReport(this, "Marketplace Statistics", manager.getStatsReport());
        });

        // Button 4: Prompts the user for their name and displays only their posted listings
        myListingsBtn.addActionListener(e -> showMyListingsDialog());

        // Button 5: Saves the current collection to data/marketplace.txt
        saveBtn.addActionListener(e -> handleSaveToFile());

        // Button 6: Reloads listings from data/marketplace.txt after confirming with the user
        loadBtn.addActionListener(e -> handleLoadFromFile());

        buttonPanel.add(postBtn);
        buttonPanel.add(browseBtn);
        buttonPanel.add(statsBtn);
        buttonPanel.add(myListingsBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(loadBtn);

        getContentPane().add(buttonPanel, BorderLayout.CENTER);

        // Bottom panel holding the Back to Main Menu button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(AppTheme.PANEL_BG);
        JButton backBtn = AppTheme.createButton("< Back to Main Menu");
        backBtn.addActionListener(e -> {
            new MainMenuGUI().setVisible(true); // open the main landing menu
            dispose(); // close this section menu
        });
        bottomPanel.add(backBtn);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    // Loads saved file if present; otherwise seeds sample items so the screen is never blank
    private void loadInitialData() {
        if (manager.size() == 0) {
            try {
                manager.loadFromFile(MarketplaceManager.DEFAULT_FILE);
            } catch (IOException e) {
                manager.loadSampleData(); // first run: load sample listings
            }
        }
    }

    // Prompts for owner name via an input dialog and displays matching listings
    private void showMyListingsDialog() {
        String owner = JOptionPane.showInputDialog(this,
                "Enter your name to view your listings:",
                "My Listings", JOptionPane.QUESTION_MESSAGE);
        if (owner != null && !owner.trim().isEmpty()) { // check user didn't hit cancel or enter blank
            AppTheme.showReport(this, "Listings for: " + owner.trim(),
                    manager.getMyListingsReport(owner.trim()));
        }
    }

    // Writes listings to disk and gives user confirmation or error popup
    private void handleSaveToFile() {
        try {
            int count = manager.saveToFile(MarketplaceManager.DEFAULT_FILE);
            JOptionPane.showMessageDialog(this,
                    "Successfully saved " + count + " listings to:\n"
                            + new File(MarketplaceManager.DEFAULT_FILE).getAbsolutePath(),
                    "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving file: " + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Confirms with user, then reads listings from disk and refreshes the collection
    private void handleLoadFromFile() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Loading from file will replace current listings on screen. Continue?",
                "Confirm Load", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) { // only proceed if user clicked YES
            try {
                int count = manager.loadFromFile(MarketplaceManager.DEFAULT_FILE);
                JOptionPane.showMessageDialog(this,
                        "Successfully loaded " + count + " listings from file.",
                        "Load Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                        "No saved file found. Please create listings and Save first.",
                        "File Not Found", JOptionPane.WARNING_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error reading file: " + e.getMessage(),
                        "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Standalone launcher so Section C can be run independently for grading
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new MarketplaceMenuGUI().setVisible(true));
    }
}

// FORM 1: POST A LISTING (Satisfies Form 1 of the 2 required user input forms)
class PostListingFormGUI extends JFrame {

    private final MarketplaceManager manager;

    // GUI input fields and dropdowns
    private final JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Donation", "Swap"});
    private final JTextField ownerField = new JTextField(15);
    private final JTextField titleField = new JTextField(15);
    private final JComboBox<String> categoryCombo = new JComboBox<>(new String[]{
            "Furniture", "Electronics", "Clothing", "Books", "Household", "Sports"});
    private final JComboBox<String> conditionCombo = new JComboBox<>(new String[]{
            "Like New", "Good", "Fair"});
    private final JTextField dateField = new JTextField(10);
    private final JLabel detailLabel = new JLabel("Pickup Area (e.g. Dublin 2):");
    private final JTextField detailField = new JTextField(15);

    public PostListingFormGUI(MarketplaceManager manager) {
        super("EcoCycle - Post a Listing");
        this.manager = manager;

        setSize(620, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.PANEL_BG);

        // Header banner
        getContentPane().add(
                AppTheme.createHeader("Post a Marketplace Listing",
                        "Section C - Offer an unwanted item for donation or swap"),
                BorderLayout.NORTH);

        // Auto-fill today's date in yyyy-mm-dd format so user doesn't have to type it manually
        dateField.setText(LocalDate.now().toString());

        // Event listener: updates the label dynamically based on Donation vs Swap selection
        typeCombo.addActionListener(e -> {
            if ("Donation".equals(typeCombo.getSelectedItem())) {
                detailLabel.setText("Pickup Area (e.g. Dublin 2):");
            } else {
                detailLabel.setText("Wanted in Return (e.g. Kettle):");
            }
        });

        // Add rows to our 2-column form layout
        JPanel form = AppTheme.formPanel();
        AppTheme.addFormRow(form, "Listing Type:", typeCombo);
        AppTheme.addFormRow(form, "Your Name (Owner):", ownerField);
        AppTheme.addFormRow(form, "Item Title:", titleField);
        AppTheme.addFormRow(form, "Category:", categoryCombo);
        AppTheme.addFormRow(form, "Condition:", conditionCombo);
        AppTheme.addFormRow(form, "Date Posted (yyyy-mm-dd):", dateField);
        AppTheme.addFormRow(form, detailLabel.getText(), detailField);

        getContentPane().add(form, BorderLayout.CENTER);

        // Buttons row at bottom
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonRow.setBackground(AppTheme.PANEL_BG);

        JButton saveBtn = AppTheme.createButton("Save Listing");
        JButton clearBtn = AppTheme.createButton("Clear");
        JButton backBtn = AppTheme.createButton("< Back to Section Menu");

        saveBtn.addActionListener(e -> saveListing());
        clearBtn.addActionListener(e -> clearFields());
        backBtn.addActionListener(e -> {
            new MarketplaceMenuGUI(manager).setVisible(true); // reopen section menu
            dispose(); // close form
        });

        buttonRow.add(saveBtn);
        buttonRow.add(clearBtn);
        buttonRow.add(backBtn);

        getContentPane().add(buttonRow, BorderLayout.SOUTH);
    }

    // Reads user inputs, validates them, creates the object, and adds it to the manager
    private void saveListing() {
        String type = (String) typeCombo.getSelectedItem();
        String owner = ownerField.getText().trim();
        String title = titleField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String condition = (String) conditionCombo.getSelectedItem();
        String date = dateField.getText().trim();
        String detail = detailField.getText().trim();

        // Check fields: stop if any text field was left empty
        if (owner.isEmpty() || title.isEmpty() || date.isEmpty() || detail.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields before saving.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return; // stop execution so blank items are never created
        }

        try {
            Listing newListing;
            // Build the right subclass based on dropdown choice
            if ("Donation".equalsIgnoreCase(type)) {
                newListing = new DonationListing(owner, title, category, condition, date, detail);
            } else {
                newListing = new SwapListing(owner, title, category, condition, date, detail);
            }

            manager.addListing(newListing); // store in the ArrayList

            JOptionPane.showMessageDialog(this,
                    "Successfully posted!\n" + newListing.getListingSummary() + "\nAssigned ID: " + newListing.getId(),
                    "Listing Created", JOptionPane.INFORMATION_MESSAGE);

            clearFields(); // reset form fields ready for next entry

        } catch (IllegalArgumentException ex) {
            // catches bad date formats or constructor errors and shows friendly popup
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Resets all input fields back to their defaults
    private void clearFields() {
        ownerField.setText("");
        titleField.setText("");
        detailField.setText("");
        dateField.setText(LocalDate.now().toString());
        typeCombo.setSelectedIndex(0);
        categoryCombo.setSelectedIndex(0);
        conditionCombo.setSelectedIndex(0);
    }
}

// FORM 2: BROWSE / SEARCH / CLAIM / DELETE (Satisfies Form 2 of the 2 required user input forms)
class BrowseListingFormGUI extends JFrame {

    private final MarketplaceManager manager;

    // Swing components for displaying listings in a scrollable list
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> listingList = new JList<>(listModel);
    private final JLabel captionLabel = new JLabel("All Active Listings:");

    // Search and filter controls
    private final JComboBox<String> filterTypeCombo = new JComboBox<>(new String[]{
            "All", "Available", "Claimed", "Expired", "Furniture", "Electronics", "Clothing", "Books", "Sports"});
    private final JTextField searchField = new JTextField(14);

    public BrowseListingFormGUI(MarketplaceManager manager) {
        super("EcoCycle - Browse & Search Listings");
        this.manager = manager;

        setSize(700, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.PANEL_BG);

        // Header banner
        getContentPane().add(
                AppTheme.createHeader("Browse, Claim & Manage Listings",
                        "Section C - Find reusable items or claim listings"),
                BorderLayout.NORTH);

        // Top Search & Filter Bar
        JPanel topSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topSearchPanel.setBackground(AppTheme.PANEL_BG);
        topSearchPanel.add(new JLabel("Search Title:"));
        topSearchPanel.add(searchField);
        JButton searchBtn = AppTheme.createButton("Search");
        topSearchPanel.add(searchBtn);

        topSearchPanel.add(new JLabel("Filter:"));
        topSearchPanel.add(filterTypeCombo);
        JButton filterBtn = AppTheme.createButton("Filter");
        topSearchPanel.add(filterBtn);

        searchBtn.addActionListener(e -> performSearch());
        filterBtn.addActionListener(e -> performFilter());

        // Center Panel: Caption label on top, JList in scroll pane in center
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(AppTheme.PANEL_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        captionLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
        centerPanel.add(captionLabel, BorderLayout.NORTH);

        listingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // user can pick one item at a time
        centerPanel.add(new JScrollPane(listingList), BorderLayout.CENTER);

        getContentPane().add(centerPanel, BorderLayout.CENTER);

        // Bottom action buttons: Display All, Claim, Delete, Back
        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        bottomButtons.setBackground(AppTheme.PANEL_BG);

        JButton displayAllBtn = AppTheme.createButton("Display All");
        JButton claimBtn = AppTheme.createButton("Claim Selected");
        JButton deleteBtn = AppTheme.createButton("Delete Selected");
        JButton backBtn = AppTheme.createButton("< Back to Section Menu");

        displayAllBtn.addActionListener(e -> displayAllListings());
        claimBtn.addActionListener(e -> claimSelectedItem());
        deleteBtn.addActionListener(e -> deleteSelectedItem());
        backBtn.addActionListener(e -> {
            new MarketplaceMenuGUI(manager).setVisible(true); // return to section menu
            dispose();
        });

        bottomButtons.add(displayAllBtn);
        bottomButtons.add(claimBtn);
        bottomButtons.add(deleteBtn);
        bottomButtons.add(backBtn);

        getContentPane().add(bottomButtons, BorderLayout.SOUTH);

        // Load and show all listings on screen startup
        displayAllListings();
    }

    // Refreshes the 30-day expiry check and displays every item currently in the manager
    private void displayAllListings() {
        manager.checkAndExpireListings();
        populateList(manager.getAllListings(), "Showing all listings (" + manager.size() + "):");
    }

    // Searches listing titles containing the keyword typed in the search bar
    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) { // warn user if search bar is blank
            JOptionPane.showMessageDialog(this, "Enter a search term first.",
                    "Input Needed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ArrayList<Listing> results = manager.searchByKeyword(keyword);
        populateList(results, "Search results for: '" + keyword + "' (" + results.size() + " found):");
    }

    // Filters the JList by status (Available, Claimed, Expired) or category
    private void performFilter() {
        String selected = (String) filterTypeCombo.getSelectedItem();
        if ("All".equals(selected)) {
            displayAllListings(); // reset back to all
        } else if ("Available".equals(selected) || "Claimed".equals(selected) || "Expired".equals(selected)) {
            ArrayList<Listing> results = manager.searchByStatus(selected);
            populateList(results, "Filtered by status: " + selected + " (" + results.size() + "):");
        } else {
            ArrayList<Listing> results = manager.searchByCategory(selected);
            populateList(results, "Filtered by category: " + selected + " (" + results.size() + "):");
        }
    }

    // Claims the item currently highlighted by the user in the JList
    private void claimSelectedItem() {
        String selectedLine = listingList.getSelectedValue();
        if (selectedLine == null || selectedLine.startsWith("(")) { // check if user clicked an actual item
            JOptionPane.showMessageDialog(this, "Please select a listing from the list to claim.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = extractId(selectedLine); // parse the ID e.g. "L001" from the line
        if (id == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Do you want to claim this listing (" + id + ")?\nThis marks it as successfully reused.",
                "Confirm Claim", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) { // user confirmed
            boolean success = manager.claimListing(id); // calls manager claim
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Item " + id + " has been successfully claimed! Status changed to 'Claimed'.",
                        "Item Claimed", JOptionPane.INFORMATION_MESSAGE);
                displayAllListings(); // refresh list to show updated status
            } else {
                JOptionPane.showMessageDialog(this,
                        "Unable to claim item. It may already be Claimed or Expired.",
                        "Claim Failed", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Deletes the item currently highlighted by the user in the JList
    private void deleteSelectedItem() {
        String selectedLine = listingList.getSelectedValue();
        if (selectedLine == null || selectedLine.startsWith("(")) {
            JOptionPane.showMessageDialog(this, "Please select a listing to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = extractId(selectedLine);
        if (id == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete listing " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) { // user confirmed
            if (manager.deleteById(id)) {
                JOptionPane.showMessageDialog(this, "Listing " + id + " was deleted.",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                displayAllListings(); // refresh the list
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete listing.",
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper method: parses the ID from lines that start with "[L001] ..."
    private String extractId(String line) {
        try {
            int start = line.indexOf('[');
            int end = line.indexOf(']');
            if (start != -1 && end != -1) {
                return line.substring(start + 1, end).trim(); // gets text between "[" and "]"
            }
        } catch (Exception e) {
            // return null if format is unexpected
        }
        return null;
    }

    // Fills the JList model with listing strings and updates the caption label above the list
    private void populateList(ArrayList<Listing> items, String caption) {
        listModel.clear(); // empty previous entries
        for (Listing item : items) {
            listModel.addElement(item.toString()); // add each item line
        }
        if (items.isEmpty()) { // if zero items matched
            listModel.addElement("(No matching listings found)");
        }
        captionLabel.setText(caption);
    }
}
