package hr.javafx.fleetmanagementandmaintanancescheadule.model;

import java.io.Serializable;
import java.time.LocalDate;
/**
 * Represents a maintenance schedule tied to a specific vehicle.
 * This class stores information about a scheduled maintenance event, including the vehicle VIN,
 * date of maintenance, description of the work performed, and the mechanic responsible.
 */
public class MaintenanceSchedule implements Serializable {
        private String vin;
        private LocalDate date;
        private String description;
        private String mechanicName;

    /**
     * Constructs a {@code MaintenanceSchedule} with the specified parameters.
     *
     * @param vin          The vehicle identification number (VIN) of the maintained vehicle.
     * @param date         The date of the scheduled maintenance.
     * @param description  A brief description of the maintenance work.
     * @param mechanicName The name of the mechanic performing the maintenance.
     */
    public MaintenanceSchedule(String vin, LocalDate date, String description, String mechanicName) {
        this.vin = vin;
        this.date = date;
        this.description = description;
        this.mechanicName = mechanicName;
    }
    /**
     * Private constructor that initializes a {@code MaintenanceSchedule} instance using the {@link MaintenanceScheduleBuilder}.
     *
     * @param builder The {@link MaintenanceScheduleBuilder} instance containing the maintenance schedule properties.
     */
    private MaintenanceSchedule(MaintenanceScheduleBuilder builder){
        this.vin=builder.vin;
        this.date=builder.date;
        this.description=builder.description;
        this.mechanicName=builder.mechanicName;
    }
/**
 * Gets the VIN of the vehicle associated with this maintenance schedule.
 *
 */
    public String getVin() {
        return vin;
    }
    /**
     * Gets the date of the vehicle associated with this maintenance schedule.
     *
     */
    public LocalDate getDate() {
        return date;
    }
    /**
     * Gets the description of the vehicle associated with this maintenance schedule.
     *
     */
    public String getDescription() {
        return description;
    }
    /**
     * Gets the mechanic of the vehicle associated with this maintenance schedule.
     *
     */
    public String getMechanicName() {
        return mechanicName;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMechanicName(String mechanicName) {
        this.mechanicName = mechanicName;
    }
    /**
     * Builder class
     *
     */
    public static class MaintenanceScheduleBuilder{
        private String vin;
        private LocalDate date;
        private String description;
        private String mechanicName;

        public MaintenanceScheduleBuilder vin(String vin){
            this.vin=vin;
            return this;
        }
        public MaintenanceScheduleBuilder date(LocalDate date){
            this.date=date;
            return this;
        }
        public MaintenanceScheduleBuilder description(String description){
            this.description=description;
            return this;
        }
        public MaintenanceScheduleBuilder mechanicName(String mechanicName){
            this.mechanicName=mechanicName;
            return this;
        }
        public MaintenanceSchedule build(){
            return new MaintenanceSchedule(this);
        }
    }

    @Override
    public String toString() {
        return vin + "   " +
                date + "   " +
           description + "   " +
               mechanicName;
    }
}
