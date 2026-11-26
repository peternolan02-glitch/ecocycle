/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - individual section (Section A)
 *
 * Author  : Jamie Stackpoole (25130340)
 * Package : recycling  (Section A)
 * Purpose : A recyclable waste entry: a material and whether it was cleaned.
 */
package recycling;

import ecocycle.RecordIO;

/**
 * A recyclable waste entry.
 */
public class RecyclableWaste extends Waste {

    /** What the item is made of, e.g. "Glass". */
    private final String material;

    /** True if the user rinsed it before binning it. */
    private final boolean clean;

    /**
     * Builds a recyclable entry with an automatically generated id.
     *
     * @param description what the item was
     * @param material    what it is made of
     * @param weightKg    weight in kilograms
     * @param date        day it was logged, yyyy-mm-dd
     * @param clean       whether it has been cleaned
     * @throws IllegalArgumentException if any value is invalid
     */
    public RecyclableWaste(String description, String material,
                           double weightKg, String date, boolean clean) {
        this(null, description, material, weightKg, date, clean);
    }

    /**
     * Builds a recyclable entry from a known id, used when loading a file.
     *
     * @param id          the id from the file, or null to generate a new one
     * @param description what the item was
     * @param material    what it is made of
     * @param weightKg    weight in kilograms
     * @param date        day it was logged, yyyy-mm-dd
     * @param clean       whether it has been cleaned
     */
    RecyclableWaste(String id, String description, String material,
                    double weightKg, String date, boolean clean) {
        super(id, description, weightKg, date);
        if (material == null || material.trim().isEmpty()) {
            throw new IllegalArgumentException("Material cannot be blank.");
        }
        this.material = material.trim();
        this.clean = clean;
    }

    /** What the item is made of. */
    public String getMaterial() {
        return material;
    }

    /** Whether the item was cleaned first. */
    public boolean isClean() {
        return clean;
    }

    /** Called "Recyclable" everywhere in the app and the file. */
    @Override
    public String getType() {
        return "Recyclable";
    }

    /** Dirty items have to be rinsed first, so the advice changes. */
    @Override
    public String getDisposalAdvice() {
        if (!clean) {
            return "Rinse the " + material.toLowerCase()
                    + " first, then put it in the glass or plastics bin.";
        }
        return "Clean " + material.toLowerCase()
                + " can go straight into your recycling bin.";
    }

    /** Recycling avoids about 0.50 kg CO2e per kg of waste. */
    @Override
    public double calculateImpact() {
        return getWeightKg() * 0.50;
    }

    /** Adds the two fields only a recyclable entry has. */
    @Override
    public String toFileRecord() {
        return super.toFileRecord()
                + RecordIO.SEPARATOR + material
                + RecordIO.SEPARATOR + clean;
    }
}
