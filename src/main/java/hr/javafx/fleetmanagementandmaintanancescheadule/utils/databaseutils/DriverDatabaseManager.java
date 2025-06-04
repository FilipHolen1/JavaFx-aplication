package hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.Driver;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * Manages database operations for vehicle-related data, including adding, deleting, and retrieving vehicles.
 */
public class DriverDatabaseManager extends DatabaseManager<Driver> {
    /**
     * Inserts a car-specific entry into the CAR table.
     *
     * @param driver        The driver to be added.
     *
     * @throws SQLException If an error occurs while inserting the car.
     */
    @Override
    public void add(Driver driver) {
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
                String sqlQuery = "INSERT INTO DRIVER (LICENSE_NUMBER, FIRST_NAME, SECOND_NAME, DATE_OF_BIRTH) VALUES (?, ?, ?, ?);";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);

                statement.setString(1, driver.getLicenseNumber());
                statement.setString(2, driver.getFirstName());
                statement.setString(3, driver.getSecondName());
                statement.setObject(4, driver.getDateOfBirth());

                statement.executeUpdate();
                System.out.println("Driver added: " + driver.getFirstName() + " " + driver.getSecondName());

            } catch (IOException | SQLException e) {
                logger.error("Error inserting driver into database", e);
            } finally {
                setDatabaseAccessInProgress(false);
                DatabaseManager.class.notifyAll();
            }
        }
    }

    @Override
    public boolean delete(String licenseNumber) {
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
                String sql = "DELETE FROM DRIVER WHERE LICENSE_NUMBER = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, licenseNumber);
                int rowsDeleted = statement.executeUpdate();
                deleted = rowsDeleted > 0;

            } catch (IOException | SQLException e) {
                logger.error("Error deleting driver", e);
            } finally {
                setDatabaseAccessInProgress(false);
                DatabaseManager.class.notifyAll();
            }

            return deleted;
        }
    }

    @Override
    public List<Driver> getAll() {
        List<Driver> drivers = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sql = "SELECT * FROM DRIVER";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String licenseNumber = resultSet.getString("LICENSE_NUMBER");
                String firstName = resultSet.getString("FIRST_NAME");
                String secondName = resultSet.getString("SECOND_NAME");
                LocalDate dateOfBirth = resultSet.getObject("DATE_OF_BIRTH", LocalDate.class);

                drivers.add(new Driver.DriverBuilder()
                        .licenseNumber(licenseNumber)
                        .firstName(firstName)
                        .secondName(secondName)
                        .dateOfBirth(dateOfBirth)
                        .build());
            }

        } catch (IOException | SQLException e) {
            logger.error("Error retrieving drivers from database", e);
        }

        return drivers;
    }

    @Override
    public Driver findById(String licenseNumber) {
        Driver driver = null;

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM DRIVER WHERE LICENSE_NUMBER = ?";
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, licenseNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("FIRST_NAME");
                String secondName = resultSet.getString("SECOND_NAME");
                LocalDate dateOfBirth = resultSet.getObject("DATE_OF_BIRTH", LocalDate.class);

                driver = new Driver.DriverBuilder()
                        .licenseNumber(licenseNumber)
                        .firstName(firstName)
                        .secondName(secondName)
                        .dateOfBirth(dateOfBirth)
                        .build();
            }

        } catch (IOException | SQLException e) {
            logger.error("Error retrieving driver by license number", e);
        }

        return driver;
    }
}
