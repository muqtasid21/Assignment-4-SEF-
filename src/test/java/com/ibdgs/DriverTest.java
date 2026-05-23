package com.ibdgs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Driver class conditions D1-D5.
 * Tests cover normal cases, invalid inputs, and edge cases.
 * Total: 15 unit test cases
 */
public class DriverTest {

    // ── Condition D1: Driver ID Rules ─────────────────────────────────────────

    /**
     * D1 - Normal case: Valid driverID that meets all requirements.
     * First two chars: digits 2-9
     * Two special chars in positions 3-8
     * Last two chars: uppercase letters
     */
    @Test
    @DisplayName("D1 - Normal: Valid driver ID should be accepted")
    void testD1_validDriverID() {
        // "34@#56abAB" - 34 (digits 2-9), @# (2 special chars), 56ab (middle), AB (uppercase)
        assertTrue(Driver.isValidDriverID("34@#56abAB"),
            "A valid driverID matching all D1 rules should return true.");
    }

    /**
     * D1 - Invalid: driverID shorter than 10 characters.
     */
    @Test
    @DisplayName("D1 - Invalid: Driver ID shorter than 10 chars should be rejected")
    void testD1_tooShortDriverID() {
        assertFalse(Driver.isValidDriverID("34@#56AB"),
            "A driverID shorter than 10 characters should return false.");
    }

    /**
     * D1 - Edge case: First two chars are '1' which is not in range 2-9.
     */
    @Test
    @DisplayName("D1 - Edge: First char is digit 1 (out of range 2-9) should be rejected")
    void testD1_firstCharOutOfRange() {
        assertFalse(Driver.isValidDriverID("14@#56abAB"),
            "driverID starting with '1' should be rejected as first digit must be 2-9.");
    }

    /**
     * D1 - Invalid: Last two characters are lowercase, not uppercase.
     */
    @Test
    @DisplayName("D1 - Invalid: Last two chars lowercase should be rejected")
    void testD1_lastCharsLowercase() {
        assertFalse(Driver.isValidDriverID("34@#56abab"),
            "driverID with lowercase last two chars should return false.");
    }

    /**
     * D1 - Edge case: Only one special character in positions 3-8 (need at least 2).
     */
    @Test
    @DisplayName("D1 - Edge: Only one special char in positions 3-8 should be rejected")
    void testD1_oneSpecialChar() {
        assertFalse(Driver.isValidDriverID("34@bc56aAB"),
            "driverID with only one special char in positions 3-8 should return false.");
    }

    // ── Condition D2: Address Format ─────────────────────────────────────────

    /**
     * D2 - Normal case: Valid address with exactly 5 pipe-separated parts.
     */
    @Test
    @DisplayName("D2 - Normal: Valid address format should be accepted")
    void testD2_validAddress() {
        String address = "12|Main Street|Melbourne|VIC|Australia";
        assertTrue(Driver.isValidAddress(address),
            "A valid address with 5 pipe-separated parts should return true.");
    }

    /**
     * D2 - Invalid: Address missing one part (only 4 parts).
     */
    @Test
    @DisplayName("D2 - Invalid: Address with only 4 parts should be rejected")
    void testD2_missingPart() {
        String address = "12|Main Street|Melbourne|VIC";
        assertFalse(Driver.isValidAddress(address),
            "An address with only 4 parts should return false.");
    }

    /**
     * D2 - Edge case: Address has 5 parts but one is empty.
     */
    @Test
    @DisplayName("D2 - Edge: Address with an empty part should be rejected")
    void testD2_emptyPart() {
        String address = "12|Main Street||VIC|Australia";
        assertFalse(Driver.isValidAddress(address),
            "An address with an empty part should return false.");
    }

    // ── Condition D3: Birthdate Format ────────────────────────────────────────

    /**
     * D3 - Normal case: Valid birthdate in DD-MM-YYYY format.
     */
    @Test
    @DisplayName("D3 - Normal: Valid birthdate DD-MM-YYYY should be accepted")
    void testD3_validBirthdate() {
        assertTrue(Driver.isValidBirthdate("15-06-1990"),
            "A valid birthdate in DD-MM-YYYY format should return true.");
    }

    /**
     * D3 - Invalid: Birthdate in wrong format (YYYY-MM-DD instead of DD-MM-YYYY).
     */
    @Test
    @DisplayName("D3 - Invalid: Wrong date format YYYY-MM-DD should be rejected")
    void testD3_wrongFormat() {
        assertFalse(Driver.isValidBirthdate("1990-06-15"),
            "A birthdate in YYYY-MM-DD format should return false.");
    }

    /**
     * D3 - Edge case: Birthdate with invalid day (32nd day does not exist).
     */
    @Test
    @DisplayName("D3 - Edge: Invalid day value 32 should be rejected")
    void testD3_invalidDay() {
        assertFalse(Driver.isValidBirthdate("32-06-1990"),
            "A birthdate with day 32 should return false as it is not a real date.");
    }

    // ── Condition D4: License Update Restriction ──────────────────────────────

    /**
     * D4 - Normal case: Driver with less than 10 years experience CAN change license.
     */
    @Test
    @DisplayName("D4 - Normal: Driver with 5 years experience can change license type")
    void testD4_canChangeLicense() {
        Driver driver = new Driver("34@#56abAB", "John Smith", 5,
            "Light", "12|Main St|Melbourne|VIC|Australia", "15-06-1990");
        // Should not throw — under 10 years
        assertDoesNotThrow(() -> driver.updateLicenseType("Medium"),
            "Driver with 5 years experience should be allowed to change license type.");
    }

    /**
     * D4 - Invalid: Driver with more than 10 years experience CANNOT change license.
     */
    @Test
    @DisplayName("D4 - Invalid: Driver with 11 years experience cannot change license type")
    void testD4_cannotChangeLicense() {
        Driver driver = new Driver("34@#56abAB", "John Smith", 11,
            "Heavy", "12|Main St|Melbourne|VIC|Australia", "15-06-1990");
        // Should throw IllegalStateException
        assertThrows(IllegalStateException.class,
            () -> driver.updateLicenseType("Light"),
            "Driver with 11 years experience should not be allowed to change license type.");
    }

    /**
     * D4 - Edge case: Driver with exactly 10 years experience CAN still change license.
     */
    @Test
    @DisplayName("D4 - Edge: Driver with exactly 10 years experience can still change license")
    void testD4_exactlyTenYears() {
        Driver driver = new Driver("34@#56abAB", "John Smith", 10,
            "Medium", "12|Main St|Melbourne|VIC|Australia", "15-06-1990");
        // Should not throw — exactly 10 is still allowed (rule says "more than 10")
        assertDoesNotThrow(() -> driver.updateLicenseType("Heavy"),
            "Driver with exactly 10 years experience should still be allowed to change license.");
    }

    // ── Condition D5: Immutable Fields ────────────────────────────────────────

    /**
     * D5 - Normal case: Updating address does not affect driverID or name.
     */
    @Test
    @DisplayName("D5 - Normal: Updating address leaves driverID and name unchanged")
    void testD5_immutableFieldsOnAddressUpdate() {
        Driver driver = new Driver("34@#56abAB", "John Smith", 5,
            "Light", "12|Main St|Melbourne|VIC|Australia", "15-06-1990");
        driver.updateAddress("99|New Road|Sydney|NSW|Australia");
        // driverID and name must remain unchanged
        assertEquals("34@#56abAB", driver.getDriverID(),
            "driverID must not change after updating address.");
        assertEquals("John Smith", driver.getName(),
            "name must not change after updating address.");
    }

    /**
     * D5 - Invalid: Attempting to create a driver with a null driverID.
     */
    @Test
    @DisplayName("D5 - Invalid: Creating driver with null driverID should throw exception")
    void testD5_nullDriverID() {
        assertThrows(IllegalArgumentException.class,
            () -> new Driver(null, "John Smith", 5,
                "Light", "12|Main St|Melbourne|VIC|Australia", "15-06-1990"),
            "Creating a driver with null driverID should throw IllegalArgumentException.");
    }

    /**
     * D5 - Edge case: Updating experience years does not change driverID or name.
     */
    @Test
    @DisplayName("D5 - Edge: Updating experience years does not change driverID or name")
    void testD5_immutableAfterExperienceUpdate() {
        Driver driver = new Driver("34@#56abAB", "Jane Doe", 3,
            "Light", "12|Main St|Melbourne|VIC|Australia", "01-01-1995");
        driver.updateExperienceYears(8);
        assertEquals("34@#56abAB", driver.getDriverID(),
            "driverID must remain unchanged after updating experience.");
        assertEquals("Jane Doe", driver.getName(),
            "name must remain unchanged after updating experience.");
    }
}
