/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - Section C (Reuse Marketplace)
 *
 * Author  : Peter Nolan (24338893)
 * Package : marketplace
 * Purpose : Subclass of Listing representing an item offered for exchange.
 *           Adds details about the item wanted in return.
 */
package marketplace; // places this inside the marketplace package

import ecocycle.RecordIO; // imports the pipe delimiter helper for file saving

// Extends Listing so it inherits all common fields like id, title, owner, and date
public class SwapListing extends Listing {

    // Extra field specific only to swaps (final so it doesn't change once posted)
    private final String wantedItem;

    // Short constructor called by PostListingFormGUI when a user posts a new swap
    public SwapListing(String owner, String title, String category,
                       String condition, String datePosted, String wantedItem) {
        // Calls the short Listing constructor to set the 5 shared fields and auto-assign an ID
        super(owner, title, category, condition, datePosted);

        // Check that wanted item was filled in so people know what exchange is wanted
        if (wantedItem == null || wantedItem.trim().isEmpty()) {
            throw new IllegalArgumentException("Wanted item in exchange cannot be empty.");
        }
        this.wantedItem = wantedItem.trim(); // store with leading/trailing spaces removed
    }

    // Full constructor called by MarketplaceManager when loading a swap back from a file
    public SwapListing(String id, String owner, String title, String category,
                       String condition, String datePosted, String status, String wantedItem) {
        // Calls the full Listing constructor so the original ID and status are kept
        super(id, owner, title, category, condition, datePosted, status);

        // Check for empty or broken files
        if (wantedItem == null || wantedItem.trim().isEmpty()) {
            throw new IllegalArgumentException("Wanted item in exchange cannot be empty.");
        }
        this.wantedItem = wantedItem.trim();
    }

    public String getWantedItem() {
        return wantedItem;
    }

    @Override
    public String getType() {
        return "Swap"; // tells the manager and file saving that this is a swap
    }

    @Override
    public String getSpecificDetail() {
        return "Wanted in Return: " + wantedItem; // used in listing summaries
    }

    // Tradable interface method: provides a readable description of the swap
    @Override
    public String getListingSummary() {
        return String.format("[Item Swap] %s (%s) - Looking to swap for: %s",
                getTitle(), getCondition(), wantedItem);
    }

    // Adds the wanted item onto the end of the base file line (super.toFileRecord()) using "|"
    @Override
    public String toFileRecord() {
        return super.toFileRecord() + RecordIO.SEPARATOR + wantedItem;
    }

    // Adds the wanted item to the end of toString() for display in the GUI list
    @Override
    public String toString() {
        return super.toString() + " | Wanted in Return: " + wantedItem;
    }
}
