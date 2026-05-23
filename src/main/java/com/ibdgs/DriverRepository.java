package com.ibdgs;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * DriverRepository class for the Intelligent Bus Driver Guidance System.
 * Handles Add, Retrieve, Update, and Count operations for Driver objects.
 * All data is stored in and retrieved from a TXT file.
 *
 * TXT file format (one driver per line):
 * driverID,name,experienceYears,licenseType,address,birthdate
 */
public class DriverRepository {

    // Path to the TXT file used for storage
    private final String filePath;

    // ── Constructor ──────────────────────────────────────────────────────────

    public DriverRepository(String filePath) {
        this.filePath = filePath;
        // Create the file if it does not exist
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create driver storage file: " + filePath, e);
        }
    }

    // ── Add ──────────────────────────────────────────────────────────────────

    /**
     * Adds a new driver to the TXT file.
     * D1: Rejects duplicate driverIDs.
     *
     * @param driver the Driver object to add
     * @return true if added successfully, false if duplicate ID
     */
    public boolean add(Driver driver) {
        // Check for duplicate driverID (D1)
        if (retrieve(driver.getDriverID()) != null) {
            return false; // Duplicate ID rejected
        }
        // Append driver to file
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath, true))) {
            writer.write(driver.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to add driver to file.", e);
        }
    }

    // ── Retrieve ─────────────────────────────────────────────────────────────

    /**
     * Retrieves a driver by their driverID from the TXT file.
     *
     * @param driverID the ID to search for
     * @return the Driver object if found, null otherwise
     */
    public Driver retrieve(String driverID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // Check if this line's driverID matches
                String[] parts = line.split(",", 2);
                if (parts[0].equals(driverID)) {
                    return Driver.fromString(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read driver file.", e);
        }
        return null; // Not found
    }

    /**
     * Retrieves all drivers from the TXT file.
     *
     * @return a list of all Driver objects
     */
    public List<Driver> retrieveAll() {
        List<Driver> drivers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                drivers.add(Driver.fromString(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read driver file.", e);
        }
        return drivers;
    }

    // ── Update ───────────────────────────────────────────────────────────────

    /**
     * Updates an existing driver's details in the TXT file.
     * D5: driverID and name cannot be changed — only other fields are updated.
     * D4: licenseType cannot be changed if experience > 10 years.
     *
     * @param updatedDriver the Driver object with updated details
     * @return true if updated successfully, false if driver not found
     */
    public boolean update(Driver updatedDriver) {
        List<Driver> allDrivers = retrieveAll();
        boolean found = false;

        for (int i = 0; i < allDrivers.size(); i++) {
            Driver existing = allDrivers.get(i);
            if (existing.getDriverID().equals(updatedDriver.getDriverID())) {
                // D5: Cannot change driverID or name — they stay the same
                // D4: If experience > 10, cannot change licenseType
                if (existing.getExperienceYears() > 10 &&
                    !existing.getLicenseType().equals(updatedDriver.getLicenseType())) {
                    throw new IllegalStateException(
                        "Cannot change licenseType for drivers with more than 10 years experience (D4)."
                    );
                }
                allDrivers.set(i, updatedDriver);
                found = true;
                break;
            }
        }

        if (!found) return false;

        // Rewrite all drivers to the file
        rewriteFile(allDrivers);
        return true;
    }

    // ── Count ────────────────────────────────────────────────────────────────

    /**
     * Returns the number of drivers currently stored in the TXT file.
     *
     * @return count of stored drivers
     */
    public int count() {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) count++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read driver file.", e);
        }
        return count;
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    /**
     * Rewrites the entire TXT file with the given list of drivers.
     */
    private void rewriteFile(List<Driver> drivers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Driver d : drivers) {
                writer.write(d.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to rewrite driver file.", e);
        }
    }

    /**
     * Clears all data from the TXT file.
     * Used for resetting state in integration tests.
     */
    public void clearAll() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            // Write nothing — clears the file
        } catch (IOException e) {
            throw new RuntimeException("Failed to clear driver file.", e);
        }
    }
}
