/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - Shared Interface
 *
 * Package : ecocycle
 * Purpose : Shared interface for classes that calculate an environmental impact (like CO2 or waste saved).
 */
package ecocycle; // shared package so all sections can use this interface

// Any class that implements this interface must provide its own calculateImpact() method
public interface ImpactCalculable {

    // Calculates and returns the environmental impact as a decimal number (e.g. kg of CO2e)
    double calculateImpact();
}
