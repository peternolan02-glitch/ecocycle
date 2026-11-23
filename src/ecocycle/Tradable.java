/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - Shared Interface
 *
 * Author  : Peter Nolan(24338893)
 * Package : ecocycle
 * Purpose : Defines the listings for any item that can be traded or claimed
 *           in the circular reuse marketplace.
 */
package ecocycle; //This will declare that the tradble will liv in the ecocycle Package. Becasue is is shared core package not just for marketplace. This will allow any class in the project to access it !


// the interface for reusable items in the marketm
// making claiming capability and summary descriptions possible.
public interface Tradable { //The interface tag is abstract so its code isnt used it will just declare a list of methods that any class can apply

   
    boolean claim(); // attempts to claim or resver a specific item, it will return true if the item is successfully claimed flase outside of that


    String getListingSummary(); // returns the desc of the listing or the donation
}
