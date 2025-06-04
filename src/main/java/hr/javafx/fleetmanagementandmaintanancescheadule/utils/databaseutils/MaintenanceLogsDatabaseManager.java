package hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.MaintenanceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * Manages database operations for maintenancelog-related data, including adding, deleting, and retrieving vehicles.
 */
public class MaintenanceLogsDatabaseManager extends DatabaseManager<MaintenanceLog> {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceLogsDatabaseManager.class);
    /**
     * Delete a log including adding, deleting, and retrieving vehicles.
     */
    public boolean deleteLog(LocalDate date, String description, String mechanicName) {
        String sqlQuery = "DELETE FROM MaintenanceLog WHERE DATE = ? AND DESCRIPTION = ? AND MECHANIC_NAME = ?";

        try (Connection connection = DatabaseManager.connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setDate(1, java.sql.Date.valueOf(date));
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, mechanicName);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("Successfully deleted maintenance log for: " + description);
                return true;
            } else {
                logger.warn("No matching maintenance log found for deletion.");
            }
        } catch (IOException | SQLException e) {
            logger.error("Error deleting maintenance log", e);
        }

        return false;
    }
    @Override
    public MaintenanceLog findById(String vin) {
        MaintenanceLog maintenanceLog = null;
        String sqlQuery = "SELECT * FROM MaintenanceLog WHERE vin = ?";

        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

            statement.setString(1, vin);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                maintenanceLog = extractMaintenanceLogFromResultSet(resultSet);
            }

        } catch (IOException | SQLException e) {
            logger.error("Error retrieving maintenance log by VIN", e);
        }

        return maintenanceLog;
    }

    @Override
    public List<MaintenanceLog> getAll() {
        List<MaintenanceLog> maintenanceLogs = new ArrayList<>();
        String sqlQuery = "SELECT * FROM MaintenanceLog";

        try (Connection connection = connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                maintenanceLogs.add(extractMaintenanceLogFromResultSet(resultSet));
            }

        } catch (IOException | SQLException e) {
            logger.error("Error retrieving all maintenance logs", e);
        }

        return maintenanceLogs;
    }

    public List<MaintenanceLog> getMaintenanceLogsByVin(String vin) {
        List<MaintenanceLog> maintenanceLogs = new ArrayList<>();
        String sqlQuery = "SELECT id, vin, date, description, mechanic_name FROM MaintenanceLog WHERE vin = ?";

        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setString(1, vin);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                maintenanceLogs.add(extractMaintenanceLogFromResultSet(resultSet));
            }

        } catch (IOException | SQLException e) {
            logger.error("Error retrieving maintenance logs by VIN", e);
        }

        return maintenanceLogs;
    }

    @Override
    public void add(MaintenanceLog maintenanceLog) {
        String sqlQuery = "INSERT INTO MaintenanceLog (vin, date, description, mechanic_name) VALUES (?, ?, ?, ?)";

        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setString(1, maintenanceLog.vin());
            preparedStatement.setDate(2, Date.valueOf(maintenanceLog.date()));
            preparedStatement.setString(3, maintenanceLog.description());
            preparedStatement.setString(4, maintenanceLog.mechanicName());

            preparedStatement.executeUpdate();
            logger.info("Maintenance log added for VIN: " + maintenanceLog.vin());

        } catch (IOException | SQLException e) {
            logger.error("Error inserting maintenance log into database", e);
        }
    }

    @Override
    public boolean delete(String vin) {
        boolean returnValue = false;
        String sqlQuery = "DELETE FROM MaintenanceLog WHERE vin = ?";

        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

            statement.setString(1, vin);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                logger.info("Maintenance log deleted for VIN: " + vin);
                returnValue = true;
            } else {
                logger.warn("No maintenance log found for deletion with VIN: " + vin);
            }

        } catch (IOException | SQLException e) {
            logger.error("Error deleting maintenance log", e);
        }
        return returnValue;
    }

    private MaintenanceLog extractMaintenanceLogFromResultSet(ResultSet resultSet) throws SQLException {
        String retrievedVin = resultSet.getString("vin");
        LocalDate date = resultSet.getDate("date").toLocalDate();
        String description = resultSet.getString("description");
        String mechanicName = resultSet.getString("mechanic_name");

        return new MaintenanceLog(retrievedVin, date, description, mechanicName);
    }
}
