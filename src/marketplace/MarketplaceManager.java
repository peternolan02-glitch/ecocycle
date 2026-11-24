/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - Section C (Reuse Marketplace)
 *
 * Author  : Peter Nolan (24338893)
 * Package : marketplace
 * Purpose : Collection manager class that holds and manipulates the ArrayList
 *           of Listing objects. Handles CRUD, 30-day auto-expiry, stats,
 *           and file reading/writing.
 */
package marketplace; // places this inside the marketplace package

import java.io.BufferedReader; // tools for reading from a text file line by line
import java.io.BufferedWriter; // tools for buffering file writes so it writes efficiently
import java.io.File; // represents file and folder paths on the computer
import java.io.FileNotFoundException; // thrown if the file does not exist when loading
import java.io.FileReader; // reads raw characters from a file
import java.io.FileWriter; // writes raw characters to a file
import java.io.IOException; // general file input/output error
import java.io.PrintWriter; // writes formatted lines of text to a file (like println)
import java.time.LocalDate; // javas modern date class for handling yyyy-mm-dd
import java.time.temporal.ChronoUnit; // calculates the number of days between two dates for expiry
import java.util.ArrayList; // resizable arraylist required for the 10-mark data structure requirement
import ecocycle.RecordIO; // helper we made in ecocycle for splitting and joining pipe lines

// Manages the collection of all listings in memory and coordinates file I/O and searches
// This acts as the brain of section c holding the actual data and business rules
public class MarketplaceManager {

    // Default file location where marketplace data is saved (data folder in project root)
    // Uses File.separator so it works on both windows and mac without hardcoding slashes
    public static final String DEFAULT_FILE = "data" + File.separator + "marketplace.txt";

    // Data structure storing all marketplace listings (satisfies the 10-mark Data Structure requirement)
    // private final so outside classes cannot overwrite the list reference directly
    private final ArrayList<Listing> listings;

    // Constructor creates a fresh, empty ArrayList ready to hold listings in memory
    public MarketplaceManager() {
        this.listings = new ArrayList<>(); // instantiate the empty list
    }

    // CRUD: Add a new listing to the ArrayList
    // Called by PostListingFormGUI when a user hits save listing
    public void addListing(Listing listing) {
        if (listing != null) { // guard check so we never store a null listing in our list
            listings.add(listing); // adds the listing to the end of the arraylist
        }
    }

    // Returns how many total listings are currently stored
    // used by GUI to display counts and check if list is empty
    public int size() {
        return listings.size();
    }

    // Returns a copy of the ArrayList so the GUI can read listings without altering the original list directly
    // Returning a new ArrayList copy protects our internal data from accidental edits outside
    public ArrayList<Listing> getAllListings() {
        return new ArrayList<>(listings);
    }

    // Formats all listings into one big string for displaying in reports or text areas
    // called for the display all button or full marketplace summaries
    public String displayAll() {
        if (listings.isEmpty()) { // if the list has zero items, show a friendly message
            return "No listings currently in the marketplace.";
        }
        StringBuilder sb = new StringBuilder(); // stringbuilder is faster than string concatenation in loops
        for (int i = 0; i < listings.size(); i++) { // loop through every listing in the ArrayList
            sb.append(listings.get(i).toString()).append(System.lineSeparator()); // puts listing line + newline onto the text
        }
        return sb.toString(); // convert the stringbuilder back to a standard string
    }

    // Finds a single listing by its unique ID (e.g. L001)
    // Called when deleting or claiming an item to locate the object in memory first
    public Listing findById(String id) {
        if (id == null) { // guard check against null id passed in
            return null;
        }
        for (Listing item : listings) { // standard for-each loop checking each item in the ArrayList
            if (item.getId().equalsIgnoreCase(id.trim())) { // ignore spaces and casing when comparing IDs
                return item; // found it, return the matching object straight away
            }
        }
        return null; // loop finished with no match, return null
    }

    // CRUD: Delete a listing by its ID
    // Called by BrowseListingFormGUI when the user clicks Delete Selected
    public boolean deleteById(String id) {
        Listing target = findById(id); // look up the item using findById first
        if (target != null) { // if it exists, remove it from the ArrayList
            return listings.remove(target); // remove() returns true if successfully removed from the list
        }
        return false; // item wasn't found, so nothing was deleted
    }

    // Claims an available listing by its ID (Feature 3 from proposal)
    // Called by BrowseListingFormGUI when the user clicks Claim Selected
    public boolean claimListing(String id) {
        Listing target = findById(id); // find the item by ID first
        if (target != null) {
            return target.claim(); // calls the Tradable claim() method on the listing itself (turns to Claimed)
        }
        return false; // return false if the ID was not found in the list
    }

    // Searches for listings whose title contains a keyword (case-insensitive)
    // Called by BrowseListingFormGUI when typing in the search bar and clicking Search
    public ArrayList<Listing> searchByKeyword(String keyword) {
        ArrayList<Listing> results = new ArrayList<>(); // temporary list to store all matching listings
        if (keyword == null || keyword.trim().isEmpty()) {
            return results; // return empty list if search box was blank
        }
        String term = keyword.trim().toLowerCase(); // lowercase for case-insensitive match so "CHAIR" matches "chair"
        for (Listing item : listings) {
            if (item.getTitle().toLowerCase().contains(term)) { // check if title contains the search word
                results.add(item); // match found, add to results list
            }
        }
        return results; // return list of all matching items
    }

    // Filters listings by category (e.g. Furniture, Electronics)
    // Called by BrowseListingFormGUI when selecting from the filter dropdown
    public ArrayList<Listing> searchByCategory(String category) {
        ArrayList<Listing> results = new ArrayList<>(); // temporary list to hold matches
        if (category == null || category.trim().isEmpty()) {
            return results; // return empty if category is blank
        }
        String cat = category.trim();
        for (Listing item : listings) {
            if (item.getCategory().equalsIgnoreCase(cat)) { // exact match on category name
                results.add(item); // add matching item to our results list
            }
        }
        return results;
    }

    // Filters listings by status: "Available", "Claimed", or "Expired"
    // Called by BrowseListingFormGUI filter dropdown to view only available or claimed items
    public ArrayList<Listing> searchByStatus(String status) {
        ArrayList<Listing> results = new ArrayList<>();
        if (status == null || status.trim().isEmpty()) {
            return results;
        }
        String stat = status.trim();
        for (Listing item : listings) {
            if (item.getStatus().equalsIgnoreCase(stat)) { // compare listing status with the requested status
                results.add(item);
            }
        }
        return results;
    }

    // Filters listings by owner name (Satisfies Feature 4: My Listings from our proposal)
    // Called when clicking "My Listings" in MarketplaceMenuGUI so user only sees their own stuff
    public ArrayList<Listing> searchByOwner(String owner) {
        ArrayList<Listing> results = new ArrayList<>();
        if (owner == null || owner.trim().isEmpty()) {
            return results;
        }
        String name = owner.trim();
        for (Listing item : listings) {
            if (item.getOwner().equalsIgnoreCase(name)) { // checks if owner matches the entered name
                results.add(item); // add to results
            }
        }
        return results;
    }

    // 30-Day Auto-Expiry (Feature 6 from our proposal)
    // Automatically marks available items as "Expired" if older than 30 days
    // Called on file load, browse refresh, and stats view to keep listings current
    public int checkAndExpireListings() {
        int expiredCount = 0; // count how many items got expired this check
        LocalDate today = LocalDate.now(); // get today's real date from system clock

        for (Listing item : listings) {
            // crucial check: only expire unclaimed items, leave Claimed ones as Claimed so reuse history stays intact
            if (Listing.STATUS_AVAILABLE.equalsIgnoreCase(item.getStatus())) {
                try {
                    LocalDate postedDate = LocalDate.parse(item.getDatePosted()); // parse yyyy-mm-dd string into a date
                    long daysBetween = ChronoUnit.DAYS.between(postedDate, today); // count how many days have passed
                    if (daysBetween >= 30) { // 30 days or older means it has expired
                        item.markExpired(); // change status on the item to Expired
                        expiredCount++; // increase our counter
                    }
                } catch (Exception e) {
                    // if date parsing fails on a damaged line, safely skip it so app doesnt crash
                }
            }
        }
        return expiredCount; // returns number of items expired
    }

    // Marketplace Statistics & Reports (Feature 5 from our proposal)
    // Builds a formatted report showing total, available, claimed, and reuse percentage
    // Called when clicking "Marketplace Statistics" in MarketplaceMenuGUI
    public String getStatsReport() {
        int total = listings.size(); // total items posted in marketplace
        int available = 0;
        int claimed = 0;
        int expired = 0;
        int donations = 0;
        int swaps = 0;

        for (Listing item : listings) { // tally each category and status across the list
            if (Listing.STATUS_AVAILABLE.equalsIgnoreCase(item.getStatus())) {
                available++; // count currently available
            } else if (Listing.STATUS_CLAIMED.equalsIgnoreCase(item.getStatus())) {
                claimed++; // count successfully claimed/reused
            } else if (Listing.STATUS_EXPIRED.equalsIgnoreCase(item.getStatus())) {
                expired++; // count expired items
            }

            // count listing types using polymorphic getType() method
            if ("Donation".equalsIgnoreCase(item.getType())) {
                donations++; // count free donations
            } else {
                swaps++; // count item swaps
            }
        }

        // Calculate circular economy reuse rate (claimed / total * 100)
        // casting (double) prevents integer division from rounding to 0
        double reuseRate = (total > 0) ? ((double) claimed / total) * 100.0 : 0.0;

        StringBuilder sb = new StringBuilder(); // assemble clean multi-line report for popup
        sb.append("========================================").append(System.lineSeparator());
        sb.append("    ECOCYCLE REUSE MARKETPLACE STATS    ").append(System.lineSeparator());
        sb.append("========================================").append(System.lineSeparator());
        sb.append(String.format("Total Listings Posted : %d%n", total));
        sb.append(String.format("  - Available         : %d%n", available));
        sb.append(String.format("  - Successfully Reused: %d%n", claimed));
        sb.append(String.format("  - Expired (>30 days): %d%n", expired));
        sb.append("----------------------------------------").append(System.lineSeparator());
        sb.append(String.format("Listing Types:%n"));
        sb.append(String.format("  - Free Donations    : %d%n", donations));
        sb.append(String.format("  - Item Swaps        : %d%n", swaps));
        sb.append("----------------------------------------").append(System.lineSeparator());
        sb.append(String.format("Reuse Success Rate    : %.1f%%%n", reuseRate));
        sb.append("========================================").append(System.lineSeparator());

        return sb.toString(); // returns the finished formatted report string
    }

    // Builds a report of listings for a specific owner
    // Called by MarketplaceMenuGUI for the My Listings popup dialog
    public String getMyListingsReport(String ownerName) {
        ArrayList<Listing> myListings = searchByOwner(ownerName); // get items belonging to this user
        if (myListings.isEmpty()) { // if user has posted nothing, tell them nicely
            return "No listings found for user: " + ownerName;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Listings posted by: ").append(ownerName).append(System.lineSeparator());
        sb.append("--------------------------------------------------").append(System.lineSeparator());
        for (Listing item : myListings) {
            sb.append(item.toString()).append(System.lineSeparator()); // puts each of their items onto the text
        }
        return sb.toString();
    }

    // File I/O: Saves all listings to a text file (satisfies the 10-mark File Writing requirement)
    // Called when clicking "Save to File" in MarketplaceMenuGUI
    public int saveToFile(String filepath) throws IOException {
        File file = new File(filepath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) { // create the "data" folder automatically if missing on disk
            parentDir.mkdirs();
        }

        // PrintWriter wrapped in BufferedWriter for fast writing to file
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        try {
            writer.println("# EcoCycle Marketplace Data File"); // comment line explaining the file format
            writer.println("# Format: ID|Type|Owner|Title|Category|Condition|DatePosted|Status|SpecificDetail");
            for (Listing item : listings) {
                writer.println(item.toFileRecord()); // writes each listing as a pipe-separated line to disk
            }
        } finally {
            writer.close(); // always close the file writer to flush data to disk and free file lock
        }

        return listings.size(); // returns how many records got saved
    }

    // File I/O: Reads listings back from a text file (satisfies the 10-mark File Reading requirement)
    // Called on application startup and when clicking "Load from File" in MarketplaceMenuGUI
    public int loadFromFile(String filepath) throws IOException {
        File file = new File(filepath);
        if (!file.exists()) { // guard check if file doesn't exist yet on disk
            throw new FileNotFoundException("Marketplace file not found at: " + file.getAbsolutePath());
        }

        listings.clear(); // clear existing items before loading so we don't get duplicates stacked up
        BufferedReader reader = new BufferedReader(new FileReader(file)); // buffered reader reads line by line
        try {
            String line;
            while ((line = reader.readLine()) != null) { // read line by line until end of file is reached
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) { // skip blank lines and comment headers
                    continue; // jump to next line
                }

                String[] parts = RecordIO.split(trimmed); // split line by "|" using our RecordIO helper
                if (parts.length >= 9) { // must have all 9 required fields to be a valid listing
                    String id = parts[0];
                    String type = parts[1];
                    String owner = parts[2];
                    String title = parts[3];
                    String category = parts[4];
                    String condition = parts[5];
                    String datePosted = parts[6];
                    String status = parts[7];
                    String specificDetail = parts[8];

                    try {
                        // Polymorphically rebuild either a DonationListing or a SwapListing depending on type field
                        if ("Donation".equalsIgnoreCase(type)) {
                            listings.add(new DonationListing(id, owner, title, category,
                                    condition, datePosted, status, specificDetail));
                        } else if ("Swap".equalsIgnoreCase(type)) {
                            listings.add(new SwapListing(id, owner, title, category,
                                    condition, datePosted, status, specificDetail));
                        }
                    } catch (IllegalArgumentException e) {
                        // damaged data on one line is skipped safely without crashing the whole load
                    }
                }
            }
        } finally {
            reader.close(); // always close the file reader
        }

        // Run the 30-day auto-expiry rule immediately after loading so outdated listings update right away
        checkAndExpireListings();

        return listings.size(); // return count of how many items loaded
    }

    // Loads realistic sample data for demo purposes if no saved file exists yet
    // Ensures screens and lists are never empty during presentation
    public void loadSampleData() {
        listings.clear(); // start fresh
        listings.add(new DonationListing("Peter", "Wooden Desk Chair", "Furniture",
                "Good", "2026-11-10", "Dublin 2 - Grand Canal"));
        listings.add(new SwapListing("Sarah", "Toaster 4-Slice", "Electronics",
                "Like New", "2026-11-15", "Kettle or Blender"));
        listings.add(new DonationListing("Mark", "A-Level Maths Textbook", "Books",
                "Fair", "2026-10-10", "Dundrum")); // Posted >30 days ago, will test the expiry rule!
        listings.add(new SwapListing("Emma", "Mountain Bike Helmet", "Sports",
                "Good", "2026-11-18", "Bike Lock"));

        // Run expiry check on sample data so Mark's textbook becomes Expired immediately
        checkAndExpireListings();
    }
}
