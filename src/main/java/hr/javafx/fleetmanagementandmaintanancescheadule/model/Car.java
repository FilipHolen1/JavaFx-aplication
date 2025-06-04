package hr.javafx.fleetmanagementandmaintanancescheadule.model;

import java.time.LocalDate;
/**
 * Represents a car entity that can be maintained and serialized.
 * This class provides the basic attributes and behavior for different types of vans.
 */
public class Car extends Vehicle  {
    private float range;
    /**
     * Private constructor that initializes a {@code Car} instance using the {@link CarBuilder}.
     *
     * @param builder The {@link CarBuilder} instance containing the car's properties.
     */
    private Car(CarBuilder builder){
        super(builder.vin, builder.manufacturer, builder.model,
                builder.year,builder.registrationNumber, builder.registrationExpiryDate, builder.driver,
                builder.vehicleCondition);
        this.range=builder.range;
    }

    /**
     * Gets the range of the car in kilometers.
     *
     * @return The range of the car.
     */
    public float getRange() {
        return range;
    }
    /**
     * Sets the range of the car in kilometers.
     *
     * @param range The new range value.
     */
    public void setRange(float range) {
        this.range = range;
    }
    /**
     * Builder class for constructing {@link Car} instances.
     */
    public static class CarBuilder {
        private String vin;
        private String manufacturer;
        private String model;
        private Integer year;
        private String registrationNumber;
        private LocalDate registrationExpiryDate;
        private Driver driver;
        private VehicleCondition vehicleCondition;
        private float range;

        public CarBuilder vin(String vin) {
            this.vin = vin;
            return this;
        }

        public CarBuilder manufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public CarBuilder model(String model) {
            this.model = model;
            return this;
        }

        public CarBuilder year(Integer year) {
            this.year = year;
            return this;
        }
        public CarBuilder registrationNumber(String registrationNumber){
            this.registrationNumber=registrationNumber;
            return this;
        }
        public CarBuilder registrationExpiryDate(LocalDate registrationExpiryDate) {
            this.registrationExpiryDate = registrationExpiryDate;
            return this;
        }
        public CarBuilder driver(Driver driver){
            this.driver=driver;
            return this;
        }

        public CarBuilder vehicleCondition(VehicleCondition vehicleCondition) {
            this.vehicleCondition = vehicleCondition;
            return this;
        }


        public CarBuilder range(float range) {
            this.range = range;
            return this;
        }

        public Car build() {
            return new Car(this);
        }
    }

    @Override
    public float getFloat(){
        return range;
    }
    @Override
    public String toString() {
        return getManufacturer() + " " + getModel() + "  ("+ getYear() + ")"+ " "+getVin();
    }
}
