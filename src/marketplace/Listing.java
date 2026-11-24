/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - Section C (Reuse Marketplace)
 *
 * Author  : Peter Nolan (24338893)
 * Package : marketplace
 * Purpose : Abstract base class representing an item listed in the marketplace.
 *           Stores core details and implements the Tradable interface.
 */
package marketplace; //places this insdie the marketplace section or package

import ecocycle.RecordIO; //this will import the RecordIO and Tradeable we made in ecocycle for the interface and the Record helper
import ecocycle.Tradable;

//its Abstract so it will function like a blueprint (No  new listing(...)) it must be a donation listing or a swap listing
public abstract class Listing implements Tradable { //it implements tradable because it promises that listing will provide claim() abd getlistingsummary()

    // Status constants for the listings and there lifecycles
    public static final String STATUS_AVAILABLE = "Available"; 
    public static final String STATUS_CLAIMED = "Claimed";
    public static final String STATUS_EXPIRED = "Expired";

    // Data members (encapsulated) these are listing specific (final) so once its created they dont change (constants)
    private final String id;
    private final String owner;
    private final String title;
    private final String category;
    private final String condition;
    private final String datePosted; // Format: yyyy-mm-dd
    private String status; // we leave this one as not final as the status of the listing can change avaliable  to claimed or expired (var)

    // Static counter used to automatically assign unique IDs (e.g. 1,2,3)
    // this is shared by all listings (shared variable)
    private static int idCounter = 1;

    // this is the short  constructor for new listings it gets called by PostlistingFormGui in MarketplaceMenuGui as it fills these 5 specific fields
    public Listing(String owner, String title, String category, String condition, String datePosted) {
        this(generateNextId(), owner, title, category, condition, datePosted, STATUS_AVAILABLE); //this will call the full constructor passing in fresh id and setting to avaliable by default
    }

    //this is the full constructor for adding the id and and status
    // it has if checks for making sure fields are filled in properly or throwing back errors
    public Listing(String id, String owner, String title, String category,
                   String condition, String datePosted, String status) {

        // Basic checks using standard string checks
        if (owner == null || owner.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be empty.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Item title cannot be empty.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        if (condition == null || condition.trim().isEmpty()) {
            throw new IllegalArgumentException("Condition cannot be empty.");
        }
        if (datePosted == null || !isValidDate(datePosted)) {
            throw new IllegalArgumentException("Date must be in yyyy-mm-dd format.");
        }
        
        //clean value storage with trimming for leading or trailing spaces
        this.id = id;
        this.owner = owner.trim();
        this.title = title.trim();
        this.category = category.trim();
        this.condition = condition.trim();
        this.datePosted = datePosted.trim();

        // This checks the status and only accepts claimed or expired anything else will default to avaliable
        if (STATUS_CLAIMED.equalsIgnoreCase(status)) {
            this.status = STATUS_CLAIMED;
        } else if (STATUS_EXPIRED.equalsIgnoreCase(status)) {
            this.status = STATUS_EXPIRED;
        } else {
            this.status = STATUS_AVAILABLE;
        }

        // This will progress the previously defined counter by 1
        updateIdCounter(id);
    }

    // this will format the id counter  with a leading "00" so 1 becomes L001 and 2 becomes L002 
    // this will help witj clean sorting and for item removal
    private static String generateNextId() {
        return String.format("L%03d", idCounter++);
    }

    // This will allow the system to read the counter from the existing file so for example if you upload l003 and close the app the next day it wont start from L001 it will start from L004
    private static void updateIdCounter(String existingId) {
        if (existingId != null && existingId.startsWith("L")) { //this will make sure before anything goes ahead the existingId is not null and begins with a "L"
            try { // this will not run if the character after L arent digits. Prevents crashing
                int num = Integer.parseInt(existingId.substring(1)); // Parse the "001" so it appears as a real hole number "5",then cut the "L" off (substring(1))
                if (num >= idCounter) { // this makes sure the idcounter is always 1 higher than the biggest id in the file
                    idCounter = num + 1; //if 5 is bigger than or equal to idCounter we bump id counter up to 6 (5+1).
                }
            } catch (NumberFormatException e) { 
                //This just stops updateIdCounter if it cant parse the id it leaves the counter as it was and prevents crashes
                // the follow on is the MarkeplaceManager catching IllegalArgumentException and preventing faulty lines from being in 
            }
        }
    }

    // Date checker
    private static boolean isValidDate(String dateStr) { // this will return a true or false for checking the date
        String trimmed = dateStr.trim(); //removes accidental white space if in front or behind the the date string (dateStr)
        if (trimmed.length() != 10) { // a valid date has 10 characters 4-2-2 (- = +1)
            return false;
        }
        
        if (trimmed.charAt(4) != '-' || trimmed.charAt(7) != '-') { //checks for dashes at specific postions (4 is the 5th character, 7 is the 8th character)
            return false; 
        }
        // Verify other characters are digits
        for (int i = 0; i < trimmed.length(); i++) { //this loop checls all characters in the string from 0-9 (all 10 characters)
            if (i == 4 || i == 7) { //this will skip the dashes so when "i" hits 4 or 7 it will just continue regardless the result
                continue;
            }
            if (!Character.isDigit(trimmed.charAt(i))) { //! = NOT so if a character is NOT a digit in the year month and date positions return false 
                return false;
            }
        }
        return true; // if the date got past all above checks return true !
    }

    // Tradable Interface method : boolean claim()
    // Will allow user to claim an item
    @Override // compiler checks to ensure this matches the tradable interface method exactly
    public boolean claim() { //public to be accesible by market place manager
        if (STATUS_AVAILABLE.equalsIgnoreCase(this.status)) { //this will check and item for currenlty avaliable (ignoring cases)
            this.status = STATUS_CLAIMED; //updates the status variable from available to claimed
            return true;
        }
        return false; //if the if conditon was false so (claimed or expired) will return false
    }

    //Status & Expiry Operations
    public void markExpired() { //void as is it wont return a value is called in marketplace manager
        if (STATUS_AVAILABLE.equalsIgnoreCase(this.status)) { //checks to make sure that if its avaliable so not claimed then will change to expired
            this.status = STATUS_EXPIRED;
        }
    }

    //Abstract Methods to be implemented by Subclasses
    public abstract String getType();
    public abstract String getSpecificDetail();

    //Getters and Setters
    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getCondition() {
        return condition;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //Formats the listing as a single line for display in lists.
    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s | Posted: %s by %s [%s]",
                id, title, category, condition, datePosted, owner, status);
    }

    //Formats the common fields for saving into a text file using pipe delimiter.
    public String toFileRecord() {
        return RecordIO.join(id, getType(), owner, title, category, condition, datePosted, status);
    }
}
