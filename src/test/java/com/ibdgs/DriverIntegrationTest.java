package com.ibdgs;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

/**
 * Integration tests for DriverRepository class.
 * Tests use real TXT files and real implementations.
 * Total: 4 integration test cases
 *
 * Tests verify:
 * 1. Valid drivers are stored correctly
 * 2. Invalid drivers are rejected
 * 3. Updates are persisted correctly
 * 4. Record counts are updated correctly
 */
public class DriverIntegrationTest {

    // Use a separate test file so we don't touch real data
    private static final String TEST_FILE = "src/test/resources/drivers_test.txt";
    private DriverRepository repository;

    /**
     * Set up a fresh repository before each test.
     * Ensures tests are independent of each other.
     */
    @BeforeEach
    void setUp() {
        // Create resources directory if it doesn't exist
        new File("src/test/resources").mkdirs();
        repository = new DriverRepository(TEST_FILE);
        repository.clearAll(); // Start with empty file
    }

    /**
     * Clean up the test file after each test.
     */
    @AfterEach
    void tearDown() {
        repository.clearAll();
    }

    // ── Integration Test 1: Valid driver stored correctly ─────────────────────

    /**
     * Verifies that a valid driver is correctly stored in the TXT file
     * and can be retrieved back with all fields intact.
     */
    @Test
    @DisplayName("INT-D1: Valid driver should be stored and retrieved correctly")
    void testValidDriverStoredCorrectly() {
        // Arrange
        Driver driver = new Driver(
            "34@#56abAB", "John Smith", 5,
            "Light", "12|Main St|Melbourne|VIC|Australia", "15-06-1990"
        );

        // Act — add to file
        boolean result = repository.add(driver);

        // Assert — should be added and retrievable
        assertTrue(result, "Valid driver should be added successfully.");

        Driver retrieved = repository.retrieve("34@#56abAB");
        assertNotNull(retrieved, "Driver should be retrievable after being stored.");
        assertEquals("34@#56abAB", retrieved.getDriverID(), "driverID should match.");
        assertEquals("John Smith", retrieved.getName(), "name should match.");
        assertEquals(5, retrieved.getExperienceYears(), "experienceYears should match.");
        assertEquals("Light", retrieved.getLicenseType(), "licenseType should match.");
        assertEquals("12|Main St|Melbourne|VIC|Australia", retrieved.getAddress(), "address should match.");
        assertEquals("15-06-1990", retrieved.getBirthdate(), "birthdate should match.");
    }

    // ── Integration Test 2: Invalid driver rejected ───────────────────────────

    /**
     * Verifies that a driver with a duplicate driverID is rejected
     * and not stored in the TXT file.
     */
    @Test
    @DisplayName("INT-D2: Duplicate driver ID should be rejected and not stored")
    void testInvalidDriverRejected() {
        // Arrange — add first driver
        Driver driver1 = new Driver(
            "34@#56abAB", "John Smith", 5,
            "Light", "12|Main St|Melbourne|VIC|Australia", "15-06-1990"
        );
        repository.add(driver1);

        // Act — try to add second driver with same ID
        Driver driver2 = new Driver(
            "34@#56abAB", "Jane Doe", 3,
            "Medium", "55|Park Rd|Sydney|NSW|Australia", "20-03-1995"
        );
        boolean result = repository.add(driver2);

        // Assert — second add should be rejected
        assertFalse(result, "Duplicate driverID should be rejected.");
        assertEquals(1, repository.count(),
            "Only one driver should be stored after duplicate rejection.");
    }

    // ── Integration Test 3: Update persisted correctly ────────────────────────

    /**
     * Verifies that an update to a driver's details is correctly
     * saved to the TXT file and can be retrieved with updated values.
     */
    @Test
    @DisplayName("INT-D3: Updated driver details should be persisted correctly in TXT file")
    void testUpdatePersistedCorrectly() {
        // Arrange — add a driver
        Driver driver = new Driver(
            "34@#56abAB", "John Smith", 5,
            "Light", "12|Main St|Melbourne|VIC|Australia", "15-06-1990"
        );
        repository.add(driver);

        // Act — create updated version (same ID and name — D5) with new address
        Driver updatedDriver = new Driver(
            "34@#56abAB", "John Smith", 7,
            "Medium", "99|New Road|Sydney|NSW|Australia", "15-06-1990"
        );
        boolean result = repository.update(updatedDriver);

        // Assert — update should succeed and be retrievable
        assertTrue(result, "Update should return true for existing driver.");

        Driver retrieved = repository.retrieve("34@#56abAB");
        assertNotNull(retrieved, "Driver should still exist after update.");
        assertEquals(7, retrieved.getExperienceYears(),
            "Experience years should be updated to 7.");
        assertEquals("Medium", retrieved.getLicenseType(),
            "License type should be updated to Medium.");
        assertEquals("99|New Road|Sydney|NSW|Australia", retrieved.getAddress(),
            "Address should be updated to new value.");
    }

    // ── Integration Test 4: Record count updated correctly ────────────────────

    /**
     * Verifies that the count of stored drivers is correctly updated
     * after adding multiple drivers to the TXT file.
     */
    @Test
    @DisplayName("INT-D4: Record count should update correctly after add operations")
    void testRecordCountUpdatedCorrectly() {
        // Arrange — start with empty file
        assertEquals(0, repository.count(),
            "Count should be 0 for empty repository.");

        // Act — add three drivers
        Driver driver1 = new Driver(
            "34@#56abAB", "John Smith", 5,
            "Light", "12|Main St|Melbourne|VIC|Australia", "15-06-1990"
        );
        Driver driver2 = new Driver(
            "56@#78cdCD", "Jane Doe", 3,
            "Medium", "55|Park Rd|Sydney|NSW|Australia", "20-03-1995"
        );
        Driver driver3 = new Driver(
            "78@#90efEF", "Bob Lee", 8,
            "Heavy", "77|River Ave|Brisbane|QLD|Australia", "10-11-1985"
        );

        repository.add(driver1);
        assertEquals(1, repository.count(), "Count should be 1 after first add.");

        repository.add(driver2);
        assertEquals(2, repository.count(), "Count should be 2 after second add.");

        repository.add(driver3);
        assertEquals(3, repository.count(), "Count should be 3 after third add.");
    }
}
