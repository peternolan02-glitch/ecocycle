/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - Section C (Reuse Marketplace)
 *
 * Author  : Peter Nolan (24338893)
 * Package : marketplace
 * Purpose : Subclass of Listing representing an item given away for free.
 *           Adds a pickup area for collection.
 */
package marketplace; // loads this inside the marketplace package

import ecocycle.RecordIO; // imports the pipe delimiter helper for file saving

// Extends Listing so it inherits all common fields like id, title, owner, and date
public class DonationListing extends Listing {

    // Extra field specific only to free donations (final so it doesn't change once posted)
    private final String pickupArea;

    // Short constructor called by PostListingFormGUI when a user posts a new donation
    public DonationListing(String owner, String title, String category,
                           String condition, String datePosted, String pickupArea) {
        // Calls the short Listing constructor to set the 5 shared fields and auto-assign an ID
        super(owner, title, category, condition, datePosted);

        // Check that pickup area was filled in so listings always have a collection spot
        if (pickupArea == null || pickupArea.trim().isEmpty()) {
            throw new IllegalArgumentException("Pickup area cannot be empty.");
        }
        this.pickupArea = pickupArea.trim(); // store with leading/trailing spaces removed
    }

    // Full constructor called by MarketplaceManager when loading a donation back from a file
    public DonationListing(String id, String owner, String title, String category,
                           String condition, String datePosted, String status, String pickupArea) {
        // Calls the full Listing constructor so the original ID and status are kept
        super(id, owner, title, category, condition, datePosted, status);

        // Check for empty or broken files
        if (pickupArea == null || pickupArea.trim().isEmpty()) {
            throw new IllegalArgumentException("Pickup area cannot be empty.");
        }
        this.pickupArea = pickupArea.trim();
    }

    public String getPickupArea() {
        return pickupArea;
    }

    @Override
    public String getType() {
        return "Donation"; // tells the manager and file saving that this is a donation
    }

    @Override
    public String getSpecificDetail() {
        return "Collection Area: " + pickupArea; // used in listing summaries
    }

    // Tradable interface method: provides a readable description of the donation
    @Override
    public String getListingSummary() {
        return String.format("[Free Donation] %s (%s) - Free for collection at %s",
                getTitle(), getCondition(), pickupArea);
    }

    // Adds the pickup area onto the end of the base file line (super.toFileRecord()) using "|"
    @Override
    public String toFileRecord() {
        return super.toFileRecord() + RecordIO.SEPARATOR + pickupArea;
    }

    // Adds the pickup area to the end of toString() for display in the GUI list
    @Override
    public String toString() {
        return super.toString() + " | Free Collection: " + pickupArea;
    }
}
