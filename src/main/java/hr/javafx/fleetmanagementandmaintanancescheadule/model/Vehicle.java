package hr.javafx.fleetmanagementandmaintanancescheadule.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Represents an abstract vehicle entity that can be maintained and serialized.
 * This class provides the basic attributes and behavior for different types of vehicles.
 */
public abstract non-sealed class Vehicle extends Entity implements Maintainable, Serializable {
    private String vin;
    private String manufacturer;
    private String model;
    private Integer year;
    private String registrationNumber;
    private LocalDate registrationExpiryDate;
    private Driver driver;
    private VehicleCondition vehicleCondition;
    private List<MaintenanceLog> maintenanceLogs;

    /**
     * Constructs a {@code Vehicle} with the specified attributes.
     *
     * @param vin                   The vehicle identification number (VIN).
     * @param manufacturer          The manufacturer of the vehicle.
     * @param model                 The model of the vehicle.
     * @param year                  The manufacturing year of the vehicle.
     * @param registrationNumber    The vehicle registration number.
     * @param registrationExpiryDate The expiration date of the vehicle's registration.
     * @param driver                The assigned driver for the vehicle.
     * @param vehicleCondition      The current condition of the vehicle.
     */
    protected Vehicle(String vin, String manufacturer, String model, Integer year,String registrationNumber, LocalDate registrationExpiryDate,Driver driver,VehicleCondition vehicleCondition) {
        this.vin = vin;
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
        this.driver = driver;
        this.registrationNumber=registrationNumber;
        this.registrationExpiryDate = registrationExpiryDate;
        this.vehicleCondition = vehicleCondition;
        this.maintenanceLogs = new ArrayList<>();
    }

    /**
     * Gets the vehicle's VIN.
     *
     * @return The vehicle's unique identification number.
     */
    public String getVin() {
        return vin;
    }
    /**
     * Gets the manufacturer of the vehicle.
     *
     * @return The vehicle manufacturer.
     */
    public String getManufacturer() {
        return manufacturer;
    }
    /**
     * Gets the manufacturer of the vehicle.
     *
     * @return The vehicle model.
     */
    public String getModel() {
        return model;
    }
    /**
     * Gets the manufacturer of the vehicle.
     *
     * @return The vehicle condition.
     */
    public VehicleCondition getVehicleCondition() {
        return vehicleCondition;
    }
    /**
     * Gets the manufacturer of the vehicle.
     *
     * @return The vehicle year.
     */
    public Integer getYear() {
        return year;
    }
    /**
     * Gets the manufacturer of the vehicle.
     *
     * @return The vehicle logs.
     */
    public List<MaintenanceLog> getMaintenanceLogs() {
        return maintenanceLogs;
    }
    /**
     * Gets the manufacturer of the vehicle.
     *
     * @return The vehicle driver.
     */
    public Driver getDriver() {
        return driver;
    }
    /**
     * Gets the manufacturer of the vehicle.
     *
     * @return The vehicle date.
     */
    public LocalDate getRegistrationExpiryDate() {
        return registrationExpiryDate;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setVehicleCondition(VehicleCondition vehicleCondition) {
        this.vehicleCondition = vehicleCondition;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setMaintenanceLogs(List<MaintenanceLog> maintenanceLogs) {
        this.maintenanceLogs = maintenanceLogs;
    }

    public void setDriver(Driver drivers) {
        this.driver = driver;
    }

    public void setRegistrationExpiryDate(LocalDate registrationExpiryDate) {
        this.registrationExpiryDate = registrationExpiryDate;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public abstract float getFloat();
    @Override
    public void performMaintenance(MaintenanceLog log) {
        getMaintenanceLogs().add(log);
    }
    @Override
    public String getIdentifier() {
        return vin; // VIN se koristi kao jedinstveni identifikator
    }

    @Override
    public String toString() {
        return manufacturer + " " + model +  "   (" + year +")  " + vin;
    }
}