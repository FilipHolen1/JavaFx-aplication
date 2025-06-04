package hr.javafx.fleetmanagementandmaintanancescheadule.model;

import java.io.Serializable;
import java.time.LocalDate;
/**
 * Represents a record that is tied to Maintenance Log
 * Cannot be edited
 */
public record MaintenanceLog(String vin, LocalDate date, String description, String mechanicName) implements Serializable {

    private static final long serialVersionUID = 1L; // Must be static final
    /**
     * rules of record
     * date cannot be null
     * decription cannot be empty or null
     * mechanic name cannot be empty or null
     */
    public MaintenanceLog {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (mechanicName == null || mechanicName.isBlank()) {
            throw new IllegalArgumentException("Mechanic Name cannot be empty");
        }
    }

    @Override
    public String toString() {
        return vin + "   " + date + "   "+ description + "   " + mechanicName;
    }
}
