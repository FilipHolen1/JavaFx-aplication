package hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.MaintenanceSchedule;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * Manages database operations for vehicle-related data, including adding, deleting, and retrieving vehicles.
 */
public class MaintenanceScheduleDatabaseManager extends DatabaseManager<MaintenanceSchedule> {

    /**
     * Inserts a schedule entry into the schedule table.
     *
     * @throws SQLException If an error occurs while inserting the car.
     */
    @Override
    public void add(MaintenanceSchedule maintenanceSchedule) {
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
                String sqlQuery = "INSERT INTO MaintenanceSchedule (vin, date, description, mechanic_name) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

                preparedStatement.setString(1, maintenanceSchedule.getVin());
                preparedStatement.setDate(2, Date.valueOf(maintenanceSchedule.getDate()));
                preparedStatement.setString(3, maintenanceSchedule.getDescription());
                preparedStatement.setString(4, maintenanceSchedule.getMechanicName());

                preparedStatement.executeUpdate();
                System.out.println("Maintenance schedule added!");

            } catch (IOException | SQLException e) {
                logger.error("Error inserting maintenance schedule", e);
            } finally {
                setDatabaseAccessInProgress(false);
                DatabaseManager.class.notifyAll();
            }
        }
    }
    /**
     * Inserts a schedule entry into the schedule table.
     *
     *  If an throws error occurs while deleting the maintenannce.
     */
    @Override
    public boolean delete(String description) {
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
                String sql = "DELETE FROM MaintenanceSchedule WHERE DESCRIPTION = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, description);
                int rowsDeleted = statement.executeUpdate();
                deleted = rowsDeleted > 0;

            } catch (IOException | SQLException e) {
                logger.error("Error deleting maintenance schedule", e);
            } finally {
                setDatabaseAccessInProgress(false);
                DatabaseManager.class.notifyAll();
            }

            return deleted;
        }
    }
    /**
     * Inserts a schedule entry into the schedule table.
     *
     *  If an throws error occurs while gettin all the maintenannce.
     */
    @Override
    public List<MaintenanceSchedule> getAll() {
        List<MaintenanceSchedule> schedules = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sql = "SELECT * FROM MaintenanceSchedule";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String vin = resultSet.getString("VIN");
                LocalDate date = resultSet.getDate("DATE").toLocalDate();
                String description = resultSet.getString("DESCRIPTION");
                String mechanicName = resultSet.getString("MECHANIC_NAME");

                schedules.add(new MaintenanceSchedule(vin, date, description, mechanicName));
            }

        } catch (IOException | SQLException e) {
            logger.error("Error retrieving maintenance schedules", e);
        }

        return schedules;
    }

    /**
     * Inserts a schedule entry into the schedule table.
     *
     *  If an throws error occurs while finding the maintenannce.
     */
    @Override
    public MaintenanceSchedule findById(String vin) {
        MaintenanceSchedule schedule = null;

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM MaintenanceSchedule WHERE VIN = ?";
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, vin);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                LocalDate date = resultSet.getDate("DATE").toLocalDate();
                String description = resultSet.getString("DESCRIPTION");
                String mechanicName = resultSet.getString("MECHANIC_NAME");

                schedule = new MaintenanceSchedule(vin, date, description, mechanicName);
            }

        } catch (IOException | SQLException e) {
            logger.error("Error retrieving maintenance schedule by VIN", e);
        }

        return schedule;
    }
}
