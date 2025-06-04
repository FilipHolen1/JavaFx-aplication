package hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Manages database operations for all type data, including adding, deleting, and retrieving vehicles.
 * Abstract class extende by specific types
 */
public abstract class DatabaseManager<T> {
    protected static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DATABASE_FILE = "conf/database.properties";
    private static Boolean DATABASE_ACCESS_IN_PROGRESS = false;

    protected static synchronized boolean isDatabaseAccessInProgress() {
        return DATABASE_ACCESS_IN_PROGRESS;
    }

    protected static synchronized void setDatabaseAccessInProgress(boolean status) {
        DATABASE_ACCESS_IN_PROGRESS = status;
    }
    /**
     * Use details from file
     * connect to database
     * throws SqlException
     */
    protected static Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(DATABASE_FILE));
        String urlBD = properties.getProperty("databaseUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        return DriverManager.getConnection(urlBD, username, password);
    }

    // Generic methods to be implemented by subclasses
    public abstract void add(T entity);
    public abstract boolean delete(String identifier);
    public abstract List<T> getAll();
    public abstract T findById(String id);
}
