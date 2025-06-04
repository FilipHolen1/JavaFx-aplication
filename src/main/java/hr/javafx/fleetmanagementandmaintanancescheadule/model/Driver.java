package hr.javafx.fleetmanagementandmaintanancescheadule.model;

import java.io.Serializable;
import java.time.LocalDate;
/**
 * Represents a class that is tied to Driver
 * Contains name, licensenumber and birthday
 */
public class Driver extends Entity implements Serializable {
    private static final long serialVerisonUID = 1L;
    private String firstName;
    private String secondName;
    private String licenseNumber;
    private LocalDate dateOfBirth;
    /**
     * Private constructor that initializes a {@code Driver} instance using the {@link DriverBuilder}.
     *
     * @param builder The {@link DriverBuilder} instance containing the driver's properties.
     */
    private Driver(DriverBuilder builder){
        this.firstName=builder.firstName;
        this.secondName=builder.secondName;
        this.licenseNumber=builder.licenseNumber;
        this.dateOfBirth=builder.dateOfBirth;
    }
    /**
     * Gets the first name of the driver.
     *
     * @return The first name of the driver.
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * Gets the last name (surname) of the driver.
     *
     * @return The last name of the driver.
     */
    public String getSecondName() {
        return secondName;
    }
    /**
     * Gets the driver's license number.
     *
     * @return The bitdhday of the driver.
     */
    public LocalDate getDateOfBirth() {return dateOfBirth; }
    /**
     * Gets the driver's license number.
     *
     * @return The license number of the driver.
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setDateOfBirth(LocalDate dateOfBirth){ this.dateOfBirth = dateOfBirth; }
    /**
     * Gets the driver's license number.
     *
     * @return The identitiy number of the driver.
     */@Override
    public String getIdentifier() {
        return licenseNumber; // VIN se koristi kao jedinstveni identifikator
    }

    @Override
    public String toString() {
        return
                    firstName + " " +
                    secondName + "   " +
                    licenseNumber + "   " +
                    dateOfBirth
                ;
    }

    public static class DriverBuilder{
        private String firstName;
        private String secondName;
        private String licenseNumber;
        private LocalDate dateOfBirth;

        public DriverBuilder firstName(String firstName){
            this.firstName=firstName;
            return this;
        }
        public DriverBuilder secondName(String secondName){
            this.secondName=secondName;
            return this;
        }
        public DriverBuilder licenseNumber(String licenseNumber){
            this.licenseNumber=licenseNumber;
            return this;
        }
        public DriverBuilder dateOfBirth(LocalDate dateOfBirth){
            this.dateOfBirth=dateOfBirth;
            return this;
        }
        public Driver build(){
            return new Driver(this);
        }
    }
}
