/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - individual section (Section A)
 *
 * Author  : Jamie Stackpoole (25130340)
 * Package : recycling  (Section A)
 * Purpose : An organic waste entry: food or garden waste, and whether it can be composted.
 */
package recycling;

import ecocycle.RecordIO;

/**
 * An organic (food or garden) waste entry.
 */
public class OrganicWaste extends Waste {

    /** True if the item can be composted at home. */
    private final boolean compostable;

    /**
     * Builds an organic entry with an automatically generated id.
     *
     * @param description what the item was
     * @param weightKg    weight in kilograms
     * @param date        day it was logged, yyyy-mm-dd
     * @param compostable whether it can be composted
     * @throws IllegalArgumentException if any value is invalid
     */
    public OrganicWaste(String description, double weightKg,
                        String date, boolean compostable) {
        this(null, description, weightKg, date, compostable);
    }

    /**
     * Builds an organic entry from a known id, used when loading a file.
     *
     * @param id          the id from the file, or null to generate a new one
     * @param description what the item was
     * @param weightKg    weight in kilograms
     * @param date        day it was logged, yyyy-mm-dd
     * @param compostable whether it can be composted
     */
    OrganicWaste(String id, String description, double weightKg,
                 String date, boolean compostable) {
        super(id, description, weightKg, date);
        this.compostable = compostable;
    }

    /** Whether the item can be composted. */
    public boolean isCompostable() {
        return compostable;
    }

    /** Called "Organic" everywhere in the app and the file. */
    @Override
    public String getType() {
        return "Organic";
    }

    /** Sends the user to the compost heap or to the green waste centre. */
    @Override
    public String getDisposalAdvice() {
        if (compostable) {
            return firstLetterUpperCase()
                    + " can go on your home compost heap and will rot down "
                    + "into useful soil.";
        }
        return firstLetterUpperCase()
                + " cannot be composted - take it to the green waste centre.";
    }

    /** Composting avoids about 0.30 kg CO2e per kg. */
    @Override
    public double calculateImpact() {
        return getWeightKg() * 0.30;
    }

    /** The description with its first letter upper cased, for the advice. */
    private String firstLetterUpperCase() {
        String text = getDescription();
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    /** Adds the one field only an organic entry has. */
    @Override
    public String toFileRecord() {
        return super.toFileRecord() + RecordIO.SEPARATOR + compostable;
    }
}
