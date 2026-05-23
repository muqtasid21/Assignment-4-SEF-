package com.ibdgs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Driver class for the Intelligent Bus Driver Guidance System.
 * Implements conditions D1-D5 as specified in Assignment 4.
 *
 * D1: driverID must be unique, exactly 10 chars
 *     - First two chars: digits 2-9
 *     - At least two special characters between positions 3-8
 *     - Last two chars: uppercase letters A-Z
 * D2: Address format: StreetNumber|StreetName|City|State|Country
 * D3: Birthdate format: DD-MM-YYYY
 * D4: If experience > 10 years, licenseType cannot be changed on update
 * D5: driverID and name are immutable (cannot be changed on update)
 */
public class Driver {

    private String driverID;
    private String name;
    private int experienceYears;
    private String licenseType; // Light, Medium, Heavy, PublicTransport
    private String address;
    private String birthdate;

    // ── Constructor ──────────────────────────────────────────────────────────

    public Driver(String driverID, String name, int experienceYears,
                  String licenseType, String address, String birthdate) {
        // Validate all fields on creation
        if (!isValidDriverID(driverID)) {
            throw new IllegalArgumentException(
                "Invalid driverID: must be 10 chars, first two digits 2-9, " +
                "at least two special chars in positions 3-8, last two uppercase letters."
            );
        }
        if (!isValidAddress(address)) {
            throw new IllegalArgumentException(
                "Invalid address format. Expected: StreetNumber|StreetName|City|State|Country"
            );
        }
        if (!isValidBirthdate(birthdate)) {
            throw new IllegalArgumentException(
                "Invalid birthdate format. Expected: DD-MM-YYYY"
            );
        }
        this.driverID = driverID;
        this.name = name;
        this.experienceYears = experienceYears;
        this.licenseType = licenseType;
        this.address = address;
        this.birthdate = birthdate;
    }

    // ── Validation Methods ───────────────────────────────────────────────────

    /**
     * Validates driverID according to condition D1.
     * - Exactly 10 characters long
     * - First two characters: digits between 2 and 9
     * - At least two special characters between positions 3 and 8 (index 2-7)
     * - Last two characters: uppercase letters A-Z
     */
    public static boolean isValidDriverID(String driverID) {
        if (driverID == null || driverID.length() != 10) {
            return false;
        }

        // First two chars must be digits 2-9
        char c0 = driverID.charAt(0);
        char c1 = driverID.charAt(1);
        if (c0 < '2' || c0 > '9' || c1 < '2' || c1 > '9') {
            return false;
        }

        // Last two chars must be uppercase letters A-Z
        char c8 = driverID.charAt(8);
        char c9 = driverID.charAt(9);
        if (!Character.isUpperCase(c8) || !Character.isUpperCase(c9)) {
            return false;
        }

        // At least two special characters in positions 3-8 (index 2 to 7)
        int specialCount = 0;
        for (int i = 2; i <= 7; i++) {
            char c = driverID.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                specialCount++;
            }
        }
        if (specialCount < 2) {
            return false;
        }

        return true;
    }

    /**
     * Validates address according to condition D2.
     * Format: StreetNumber|StreetName|City|State|Country
     * Must have exactly 5 parts separated by |
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        String[] parts = address.split("\\|");
        return parts.length == 5 && java.util.Arrays.stream(parts)
               .allMatch(p -> !p.trim().isEmpty());
    }

    /**
     * Validates birthdate according to condition D3.
     * Format: DD-MM-YYYY
     */
    public static boolean isValidBirthdate(String birthdate) {
        if (birthdate == null || birthdate.isEmpty()) {
            return false;
        }
        // Check format matches DD-MM-YYYY exactly
        if (!birthdate.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return false;
        }
        // Check it is an actual valid date
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate.parse(birthdate, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getDriverID() { return driverID; }
    public String getName() { return name; }
    public int getExperienceYears() { return experienceYears; }
    public String getLicenseType() { return licenseType; }
    public String getAddress() { return address; }
    public String getBirthdate() { return birthdate; }

    // ── Update Methods (applying D4 and D5) ──────────────────────────────────

    /**
     * Updates the driver's experience years.
     * driverID and name are immutable (D5) so they cannot be updated here.
     */
    public void updateExperienceYears(int newExperience) {
        this.experienceYears = newExperience;
    }

    /**
     * Updates the driver's license type.
     * D4: If experience > 10 years, licenseType cannot be changed.
     */
    public void updateLicenseType(String newLicenseType) {
        // D4: Cannot change license type if experience > 10 years
        if (this.experienceYears > 10) {
            throw new IllegalStateException(
                "Cannot change licenseType for drivers with more than 10 years of experience (D4)."
            );
        }
        this.licenseType = newLicenseType;
    }

    /**
     * Updates the driver's address.
     * Validates new address format (D2).
     */
    public void updateAddress(String newAddress) {
        if (!isValidAddress(newAddress)) {
            throw new IllegalArgumentException(
                "Invalid address format. Expected: StreetNumber|StreetName|City|State|Country"
            );
        }
        this.address = newAddress;
    }

    /**
     * Updates the driver's birthdate.
     * Validates new birthdate format (D3).
     */
    public void updateBirthdate(String newBirthdate) {
        if (!isValidBirthdate(newBirthdate)) {
            throw new IllegalArgumentException(
                "Invalid birthdate format. Expected: DD-MM-YYYY"
            );
        }
        this.birthdate = newBirthdate;
    }

    // ── toString for TXT file storage ────────────────────────────────────────

    /**
     * Converts driver to a single line string for TXT file storage.
     * Format: driverID,name,experienceYears,licenseType,address,birthdate
     */
    @Override
    public String toString() {
        return driverID + "," + name + "," + experienceYears + "," +
               licenseType + "," + address + "," + birthdate;
    }

    /**
     * Creates a Driver object from a stored TXT file line.
     */
    public static Driver fromString(String line) {
        String[] parts = line.split(",", 6);
        return new Driver(
            parts[0],           // driverID
            parts[1],           // name
            Integer.parseInt(parts[2]), // experienceYears
            parts[3],           // licenseType
            parts[4],           // address
            parts[5]            // birthdate
        );
    }
}
