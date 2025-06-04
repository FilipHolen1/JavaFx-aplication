package hr.javafx.fleetmanagementandmaintanancescheadule.model;

import java.time.LocalDate;
/**
 * Represents a van entity that can be maintained and serialized.
 * This class provides the basic attributes and behavior for different types of vans.
 */
public class Van extends Vehicle{
    private float maxCargoWeight;

    /**
     * Private constructor that initializes a {@code Van} instance using the {@link VanBuilder}.
     *
     * @param builder The {@link VanBuilder} instance containing the van's properties.
     */
    private Van(Van.VanBuilder builder){
        super(builder.vin, builder.manufacturer, builder.model,
                builder.year, builder.registrationNumber,builder.registrationExpiryDate,
                builder.driver, builder.vehicleCondition);
        this.maxCargoWeight=builder.maxCargoWeight;
    }
    /**
     * Sets the maximum cargo weight capacity of the van.
     *
     * @param maxCargoWeight The maximum cargo weight (in kilograms).
     */
    public void setMaxCargoWeight(float maxCargoWeight) {
        this.maxCargoWeight = maxCargoWeight;
    }
    /**
     * Builder class for constructing {@link Van} instances.
     */
    public static class VanBuilder {
        private String vin;
        private String manufacturer;
        private String model;
        private Integer year;
        private String registrationNumber;
        private LocalDate registrationExpiryDate;
        private Driver driver;
        private VehicleCondition vehicleCondition;
        private float maxCargoWeight;
        /**
         * Builder class for setting vin.
         * using builder pattern
         */
        public VanBuilder vin(String vin) {
            this.vin = vin;
            return this;
        }
        /**
         * Builder class for setting manufacturer.
         * using builder pattern
         */
        public VanBuilder manufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }
        /**
         * Builder class for setting model.
         * using builder pattern
         */
        public VanBuilder model(String model) {
            this.model = model;
            return this;
        }
        /**
         * Builder class for setting year.
         * using builder pattern
         */
        public VanBuilder year(Integer year) {
            this.year = year;
            return this;
        }
        public VanBuilder registrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
            return this;
        }
        /**
         * Builder class for setting dare.
         * using builder pattern
         */
        public VanBuilder registrationExpiryDate(LocalDate registrationExpiryDate) {
            this.registrationExpiryDate = registrationExpiryDate;
            return this;
        }
        /**
         * Builder class for setting driver.
         * using builder pattern
         */
        public VanBuilder driver(Driver driver){
            this.driver=driver;
            return this;
        }

        public VanBuilder vehicleCondition(VehicleCondition vehicleCondition) {
            this.vehicleCondition = vehicleCondition;
            return this;
        }

        public VanBuilder maxCargoWeight(float maxCargoWeight) {
            this.maxCargoWeight = maxCargoWeight;
            return this;
        }

        public Van build() {
            return new Van(this);
        }
    }

    @Override
    public float getFloat(){
        return maxCargoWeight;
    }

    @Override
    public String toString() {
        return getManufacturer() + " " + getModel() + "  ("+ getYear() + ")"+ " "+getVin();
    }
}
