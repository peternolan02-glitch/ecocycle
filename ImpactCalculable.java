/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 *
 * Author  : Jamie Stackpoole (25130340)
 * Package : ecocycle  (shared helper)
 * Purpose : A shared contract for anything that can report a carbon impact.
 *           Waste implements it, so every impact figure is worked out the same way.
 *
 * Note    : It is an interface rather than a class because we only need the
 *           behaviour - there are no fields to share.
 */
package ecocycle;

/**
 * Anything that can work out its own carbon impact.
 */
public interface ImpactCalculable {

    /**
     * The carbon impact of this item or activity, in kg CO2e.
     *
     * @return the impact in kg CO2e
     */
    double calculateImpact();
}
