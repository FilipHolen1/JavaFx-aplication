package hr.javafx.fleetmanagementandmaintanancescheadule.model;
/**
 * Interface for vehicle
 *
 */
public sealed interface Maintainable permits Vehicle {
    void performMaintenance(MaintenanceLog log);
}
