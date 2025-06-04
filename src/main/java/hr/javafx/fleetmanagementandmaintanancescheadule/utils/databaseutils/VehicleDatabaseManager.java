package hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.*;
import hr.javafx.fleetmanagementandmaintanancescheadule.model.Driver;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * Manages database operations for vehicle-related data, including adding, deleting, and retrieving vehicles.
 */
public class VehicleDatabaseManager extends DatabaseManager<Vehicle> {
    /**
     * Adds a new vehicle to the database, including specific handling for Cars and Vans.
     *
     * @param vehicle The vehicle to be added.
     */
    @Override
    public void add(Vehicle vehicle) {
        synchronized (DatabaseManager.class) {
            while (isDatabaseAccessInProgress()) {
                try {
                    DatabaseManager.class.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            setDatabaseAccessInProgress(true);

            try (Connection connection = connectToDatabase()) {
                String sqlQuery = "INSERT INTO VEHICLE (VIN, MANUFACTURER, MODEL, VEHICLE_YEAR, " +
                        "REGISTRATION_NUMBER, REGISTRATION_EXPIRY_DATE, DRIVER_LICENSE_NUMBER, VEHICLE_CONDITION) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, vehicle.getIdentifier());  // VIN
                preparedStatement.setString(2, vehicle.getManufacturer()); // Manufacturer
                preparedStatement.setString(3, vehicle.getModel()); // Model

                if (vehicle.getYear() != null) {
                    preparedStatement.setInt(4, vehicle.getYear());
                } else {
                    preparedStatement.setNull(4, Types.INTEGER);
                }

                preparedStatement.setString(5, vehicle.getRegistrationNumber());

                if (vehicle.getRegistrationExpiryDate() != null) {
                    preparedStatement.setDate(6, Date.valueOf(vehicle.getRegistrationExpiryDate()));
                } else {
                    preparedStatement.setNull(6, Types.DATE);
                }

                if (vehicle.getDriver() != null) {
                    preparedStatement.setString(7, vehicle.getDriver().getIdentifier());
                } else {
                    preparedStatement.setNull(7, Types.VARCHAR);
                }

                preparedStatement.setString(8, vehicle.getVehicleCondition().toString());
                preparedStatement.executeUpdate();

                logger.info("Vehicle added: " + vehicle.getManufacturer() + " " + vehicle.getModel());

                if (vehicle instanceof Car) {
                    Car car = (Car) vehicle;
                    String sqlQueryCar = "INSERT INTO CAR(VIN, RANGE) VALUES(?, ?);";

                    PreparedStatement preparedStatementCar = connection.prepareStatement(sqlQueryCar);
                    preparedStatementCar.setString(1, car.getIdentifier()); // VIN (FK)
                    if (!Float.isNaN(car.getRange())) {
                        preparedStatementCar.setFloat(2, car.getRange());
                    } else {
                        preparedStatementCar.setNull(2, Types.FLOAT);
                    }
                    System.out.println("Inserting into CAR table...");
                    preparedStatementCar.executeUpdate();
                }
                if (vehicle instanceof Van van) {
                    String sqlQueryCar = "INSERT INTO VAN(VIN, MAX_CARGO_WEIGHT) VALUES(?, ?);";

                    PreparedStatement preparedStatementCar = connection.prepareStatement(sqlQueryCar);
                    preparedStatementCar.setString(1, van.getIdentifier()); // VIN (FK)
                    if (!Float.isNaN(van.getFloat())) {
                        preparedStatementCar.setFloat(2, van.getFloat());
                    } else {
                        preparedStatementCar.setNull(2, Types.FLOAT);
                    }
                    System.out.println("Inserting into VAN table...");
                    preparedStatementCar.executeUpdate();
                }

            } catch (IOException | SQLException e) {
                logger.error("Error inserting vehicle into database", e);
            } finally {
                setDatabaseAccessInProgress(false);
                DatabaseManager.class.notifyAll();
            }
        }
    }


    @Override
    public boolean delete(String vin) {
        synchronized (DatabaseManager.class) {
            while (isDatabaseAccessInProgress()) {
                try {
                    DatabaseManager.class.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }

            setDatabaseAccessInProgress(true);
            boolean deleted = false;

            try (Connection connection = connectToDatabase()) {
                String sql = "DELETE FROM VEHICLE WHERE VIN = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, vin);
                int rowsDeleted = statement.executeUpdate();
                deleted = rowsDeleted > 0;
            } catch (IOException | SQLException e) {
                logger.error("Error deleting vehicle", e);
            } finally {
                setDatabaseAccessInProgress(false);
                DatabaseManager.class.notifyAll();
            }

            return deleted;
        }
    }

    @Override
    public List<Vehicle> getAll() {
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            DriverDatabaseManager driverDatabaseManager = new DriverDatabaseManager();

            // Fetch all Cars
            String sqlCarQuery = "SELECT V.*, C.RANGE FROM VEHICLE V JOIN CAR C ON V.VIN = C.VIN";
            try (PreparedStatement carStatement = connection.prepareStatement(sqlCarQuery);
                 ResultSet carResultSet = carStatement.executeQuery()) {

                while (carResultSet.next()) {
                    String vin = carResultSet.getString("VIN");
                    String manufacturer = carResultSet.getString("MANUFACTURER");
                    String model = carResultSet.getString("MODEL");
                    Integer year = carResultSet.getObject("VEHICLE_YEAR", Integer.class);
                    String registrationNumber = carResultSet.getString("REGISTRATION_NUMBER");
                    LocalDate registrationExpiryDate = (carResultSet.getDate("REGISTRATION_EXPIRY_DATE") != null)
                            ? carResultSet.getDate("REGISTRATION_EXPIRY_DATE").toLocalDate()
                            : null;
                    String driverLicenseNumber = carResultSet.getString("DRIVER_LICENSE_NUMBER");
                    String vehicleCondition = carResultSet.getString("VEHICLE_CONDITION");
                    float range = carResultSet.getFloat("RANGE");

                    Driver driver = (driverLicenseNumber != null) ? driverDatabaseManager.findById(driverLicenseNumber) : null;

                    Car vehicle = new Car.CarBuilder()
                            .vin(vin)
                            .manufacturer(manufacturer)
                            .model(model)
                            .year(year)
                            .registrationNumber(registrationNumber)
                            .registrationExpiryDate(registrationExpiryDate)
                            .driver(driver)
                            .vehicleCondition(VehicleCondition.valueOf(vehicleCondition))
                            .range(range)
                            .build();

                    vehicles.add(vehicle);
                }
            }

            // Fetch all Vans
            String sqlVanQuery = "SELECT V.*, VAN.MAX_CARGO_WEIGHT FROM VEHICLE V JOIN VAN ON V.VIN = VAN.VIN";
            try (PreparedStatement vanStatement = connection.prepareStatement(sqlVanQuery);
                 ResultSet vanResultSet = vanStatement.executeQuery()) {

                while (vanResultSet.next()) {
                    String vin = vanResultSet.getString("VIN");
                    String manufacturer = vanResultSet.getString("MANUFACTURER");
                    String model = vanResultSet.getString("MODEL");
                    Integer year = vanResultSet.getObject("VEHICLE_YEAR", Integer.class);
                    String registrationNumber = vanResultSet.getString("REGISTRATION_NUMBER");
                    LocalDate registrationExpiryDate = (vanResultSet.getDate("REGISTRATION_EXPIRY_DATE") != null)
                            ? vanResultSet.getDate("REGISTRATION_EXPIRY_DATE").toLocalDate()
                            : null;
                    String driverLicenseNumber = vanResultSet.getString("DRIVER_LICENSE_NUMBER");
                    String vehicleCondition = vanResultSet.getString("VEHICLE_CONDITION");
                    float maxCargoWeight = vanResultSet.getFloat("MAX_CARGO_WEIGHT");

                    Driver driver = (driverLicenseNumber != null) ? driverDatabaseManager.findById(driverLicenseNumber) : null;

                    Van vehicle = new Van.VanBuilder()
                            .vin(vin)
                            .manufacturer(manufacturer)
                            .model(model)
                            .year(year)
                            .registrationNumber(registrationNumber)
                            .registrationExpiryDate(registrationExpiryDate)
                            .driver(driver)
                            .vehicleCondition(VehicleCondition.valueOf(vehicleCondition))
                            .maxCargoWeight(maxCargoWeight)
                            .build();

                    vehicles.add(vehicle);
                }
            }

        } catch (IOException | SQLException e) {
            logger.error("Error retrieving vehicles from database", e);
        }

        return vehicles;
    }

    @Override
    public Vehicle findById(String vin) {
        Vehicle vehicle = null;
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM VEHICLE WHERE VIN = ?";
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, vin);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String manufacturer = resultSet.getString("MANUFACTURER");
                String model = resultSet.getString("MODEL");
                Integer year = resultSet.getObject("VEHICLE_YEAR", Integer.class);
                String registrationNumber = resultSet.getString("REGISTRATION_NUMBER");
                LocalDate registrationExpiryDate = (resultSet.getDate("REGISTRATION_EXPIRY_DATE") != null)
                        ? resultSet.getDate("REGISTRATION_EXPIRY_DATE").toLocalDate()
                        : null;
                String vehicleCondition = resultSet.getString("VEHICLE_CONDITION");

                vehicle = new Car.CarBuilder()
                        .vin(vin)
                        .manufacturer(manufacturer)
                        .model(model)
                        .year(year)
                        .registrationNumber(registrationNumber)
                        .registrationExpiryDate(registrationExpiryDate)
                        .vehicleCondition(VehicleCondition.valueOf(vehicleCondition))
                        .build();
            }

        } catch (IOException | SQLException e) {
            logger.error("Error retrieving vehicle by VIN", e);
        }

        return vehicle;
    }

    private void addVanToDatabase(Van van, Connection connection) throws SQLException {
        String sqlQueryVan = "INSERT INTO VAN (VIN, MAX_CARGO_WEIGHT) VALUES (?, ?)";
        try (PreparedStatement preparedStatementVan = connection.prepareStatement(sqlQueryVan)) {
            preparedStatementVan.setString(1, van.getIdentifier());
            if (!Float.isNaN(van.getFloat())) {
                preparedStatementVan.setFloat(2, van.getFloat());
            } else {
                preparedStatementVan.setNull(2, Types.FLOAT);
            }
            preparedStatementVan.executeUpdate();
            logger.info("Van added to database: " + van.getManufacturer() + " " + van.getModel());
        }
    }

    /**
     * Inserts a car-specific entry into the CAR table.
     *
     * @param car        The car to be added.
     * @param connection The database connection.
     * @throws SQLException If an error occurs while inserting the car.
     */
    private void addCarToDatabase(Car car, Connection connection) throws SQLException {
        String sqlQueryCar = "INSERT INTO CAR (VIN, RANGE) VALUES (?, ?)";
        try (PreparedStatement preparedStatementCar = connection.prepareStatement(sqlQueryCar)) {
            preparedStatementCar.setString(1, car.getIdentifier());
            if (!Float.isNaN(car.getRange())) {
                preparedStatementCar.setFloat(2, car.getRange());
            } else {
                preparedStatementCar.setNull(2, Types.FLOAT);
            }
            preparedStatementCar.executeUpdate();
            logger.info("Car added to database: " + car.getManufacturer() + " " + car.getModel());
        }
    }
}
