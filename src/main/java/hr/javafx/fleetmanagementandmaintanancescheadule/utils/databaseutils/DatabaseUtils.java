package hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.*;
import hr.javafx.fleetmanagementandmaintanancescheadule.model.Driver;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Manages database operations for all type data, including adding, deleting, and retrieving vehicles.
 */
public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final String DATABASE_FILE = "conf/database.properties";
    private static Boolean DATABASE_ACCESS_IN_PROGRESS = false;
    public static synchronized boolean isDatabaseAccessInProgress() {
        return DATABASE_ACCESS_IN_PROGRESS;
    }
    /**
     * set Database progress for threads
     */
    public static synchronized void setDatabaseAccessInProgress(boolean status) {
        DATABASE_ACCESS_IN_PROGRESS = status;
    }
    /**
     * Use details from file
     * connect to database
     * throws SqlException
     */

    private static Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(DATABASE_FILE));
        String urlBD = properties.getProperty("databaseUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        //String urlBD="jdbc:h2:E:/~/contactsDB";
        //String username="student";
        //String password="student";
        //System.out.println(urlBD+" "+username+ " "+password);
        Connection veza = DriverManager.getConnection(urlBD,
                username, password);
        return veza;
    }
    public static void addVehicle(Vehicle vehicle) {
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "INSERT INTO VEHICLE(VIN, MANUFACTURER, MODEL, VEHICLE_YEAR, " +
                    "REGISTRATION_NUMBER, REGISTRATION_EXPIRY_DATE, DRIVER_LICENSE_NUMBER, VEHICLE_CONDITION) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?);";

            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            // Obavezni atributi
            preparedStatement.setString(1, vehicle.getIdentifier());  // VIN
            preparedStatement.setString(2, vehicle.getManufacturer()); // MANUFACTURER
            preparedStatement.setString(3, vehicle.getModel()); // MODEL

            // Opcioni atributi - postavi NULL ako nisu definirani
            if (vehicle.getYear() != null) {
                preparedStatement.setInt(4, vehicle.getYear());
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }
            if (vehicle.getRegistrationNumber() != null) {
                preparedStatement.setString(5, vehicle.getRegistrationNumber());
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }

            if (vehicle.getRegistrationExpiryDate() != null) {
                preparedStatement.setDate(6, new Date(vehicle.getRegistrationExpiryDate()
                        .atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            } else {
                preparedStatement.setNull(6, Types.DATE);
            }

            String driverId = (vehicle.getDriver() != null) ? vehicle.getDriver().getIdentifier() : null;
            if (driverId != null) {
                preparedStatement.setString(7, driverId);
            } else {
                preparedStatement.setNull(7, Types.VARCHAR);
            }

            if (vehicle.getVehicleCondition() != null) {
                preparedStatement.setString(8, vehicle.getVehicleCondition().toString());
            } else {
                preparedStatement.setNull(8, Types.VARCHAR);
            }
            System.out.print("VEHICLE ");
            System.out.println(sqlQuery);
            preparedStatement.executeUpdate();

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
            WarningMessageUtils.changeAlert("Success","Vehicle was successfully added","", Alert.AlertType.CONFIRMATION);
        }
         catch (Exception ex) {
            //ex.printStackTrace();
            String message = "Dogodila se pogreska kod spremanja podataka (Vehicle) u bazu";
            logger.error(message,ex);
            System.out.println(message);
        }
    }
    public static void addDriver(Driver driver ) {
        String sql = "INSERT INTO DRIVER (LICENSE_NUMBER, FIRST_NAME, SECOND_NAME, DATE_OF_BIRTH) VALUES (?, ?, ?, ?);";
        try (Connection connection = connectToDatabase()){
             PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, driver.getLicenseNumber());
            statement.setString(2, driver.getFirstName());
            statement.setString(3, driver.getSecondName());
            statement.setObject(4, driver.getDateOfBirth());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Vozač uspješno dodan.");
            } else {
                System.out.println("Neuspješno dodavanje vozača.");
            }
        } catch (SQLException | IOException e) {
            logger.error("Greška pri dodavanju vozača:",e);
        }
    }


    public static List<Vehicle> getVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            // Fetch all Cars
            String sqlCarQuery = "SELECT V.*, C.RANGE FROM VEHICLE V JOIN CAR C ON V.VIN = C.VIN";
            PreparedStatement carStatement = connection.prepareStatement(sqlCarQuery);
            ResultSet carResultSet = carStatement.executeQuery();

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

                Car vehicle = new Car.CarBuilder().vin(vin).manufacturer(manufacturer).model(model)
                                .year(year).registrationNumber(registrationNumber).registrationExpiryDate(registrationExpiryDate)
                                .driver(getDriverById(driverLicenseNumber)).vehicleCondition(VehicleCondition.valueOf(vehicleCondition)).range(range).build();
                vehicles.add(vehicle);
            }
            carResultSet.close();
            carStatement.close();

            // Fetch all Vans
            String sqlVanQuery = "SELECT V.*, VAN.MAX_CARGO_WEIGHT FROM VEHICLE V JOIN VAN ON V.VIN = VAN.VIN";
            PreparedStatement vanStatement = connection.prepareStatement(sqlVanQuery);
            ResultSet vanResultSet = vanStatement.executeQuery();

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

                Van vehicle = new Van.VanBuilder().vin(vin).manufacturer(manufacturer).model(model).year(year)
                        .registrationNumber(registrationNumber).registrationExpiryDate(registrationExpiryDate).driver(getDriverById(driverLicenseNumber)).vehicleCondition(VehicleCondition.valueOf(vehicleCondition))
                        .maxCargoWeight(maxCargoWeight).build();
                vehicles.add(vehicle);
            }
            vanResultSet.close();
            vanStatement.close();

        } catch (Exception ex) {
            String message = "Dogodila se pogreska kod dohvata podataka (Vehicle) iz baze";
            logger.error(message, ex);
            System.out.println(message);
        }

        return vehicles;
    }

    public static Driver getDriverById(String licenseNumber) {
        Driver driver = null;

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM DRIVER WHERE license_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, licenseNumber);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String secondName = resultSet.getString("second_name");
                LocalDate dateOfBirth = (resultSet.getDate("date_of_birth") != null)
                        ? resultSet.getDate("date_of_birth").toLocalDate()
                        : null;

                driver = new Driver.DriverBuilder().licenseNumber(licenseNumber).firstName(firstName).secondName(secondName).dateOfBirth(dateOfBirth).build();
            }

            resultSet.close();
            preparedStatement.close();

        } catch (Exception ex) {
            String message = "Dogodila se pogreška kod dohvata podataka (Driver) iz baze";
            logger.error(message, ex);
            System.out.println(message);
        }

        return driver;
    }

    public static List<Driver> getDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT LICENSE_NUMBER, FIRST_NAME, SECOND_NAME, DATE_OF_BIRTH FROM DRIVER";

        try (Connection connection = connectToDatabase()){

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String licenseNumber = resultSet.getString("LICENSE_NUMBER");
                    String firstName = resultSet.getString("FIRST_NAME");
                    String secondName = resultSet.getString("SECOND_NAME");
                    LocalDate dateOfBirth = resultSet.getObject("DATE_OF_BIRTH", LocalDate.class);

                    drivers.add(new Driver.DriverBuilder().licenseNumber(licenseNumber)
                            .firstName(firstName).secondName(secondName).dateOfBirth(dateOfBirth).build());
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Greška pri dohvaćanju vozača: " + e.getMessage());
            }

            return drivers;
        }
    public static List<MaintenanceSchedule> getMaintenanceSchedule() {
        List<MaintenanceSchedule> maintenanceList = new ArrayList<>();
        String sql = "SELECT id, vin, date, description, mechanic_name FROM MaintenanceSchedule";

        try (Connection connection = connectToDatabase()){
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String vin = resultSet.getString("vin");
                Date date = resultSet.getDate("date");
                LocalDate localDate = date.toLocalDate();
                String description = resultSet.getString("description");
                String mechanicName = resultSet.getString("mechanic_name");

                MaintenanceSchedule maintenance = new MaintenanceSchedule(vin, localDate, description, mechanicName);
                maintenanceList.add(maintenance);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exception properly in production
        }
        return maintenanceList;
    }

    public static List<MaintenanceLog> getMaintenanceLogsByVin(String vin){
        List<MaintenanceLog> maintenanceLogs = new ArrayList<>();
        String sqlQuery = "SELECT id, vin, date, description, mechanic_name FROM MaintenanceLog WHERE vin = ?";
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, vin);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String retrievedVin = resultSet.getString("vin");

                // Convert java.sql.Date to java.time.LocalDate
                Date sqlDate = resultSet.getDate("date");
                LocalDate localDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                String description = resultSet.getString("description");
                String mechanicName = resultSet.getString("mechanic_name");

                MaintenanceLog log = new MaintenanceLog(retrievedVin, localDate, description,  mechanicName);
                maintenanceLogs.add(log);
            }
        }catch (Exception e) {
        e.printStackTrace();
        }
        return maintenanceLogs;
    }




    public static void addMaintenanceSchedule(MaintenanceSchedule maintenanceSchedule) {
            String sqlQuery = "INSERT INTO MaintenanceSchedule (vin, date, description, mechanic_name) VALUES (?, ?, ?, ?)";

        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, maintenanceSchedule.getVin());
            preparedStatement.setObject(2, maintenanceSchedule.getDate());  // java.sql.Date should be used
            preparedStatement.setString(3, maintenanceSchedule.getDescription());
            preparedStatement.setString(4, maintenanceSchedule.getMechanicName());

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Maintenance record inserted successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void addMaintenanceLog(MaintenanceLog maintenanceLog) {
        String sqlQuery = "INSERT INTO MaintenanceLog (vin, date, description, mechanic_name) VALUES (?, ?, ?, ?)";

        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, maintenanceLog.vin());
            preparedStatement.setObject(2, maintenanceLog.date());  // java.sql.Date should be used
            preparedStatement.setString(3, maintenanceLog.description());
            preparedStatement.setString(4, maintenanceLog.mechanicName());

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Maintenance Log inserted successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeDriverByLicenseNumber(String licenseNumber) {
        String sql = "DELETE FROM DRIVER WHERE LICENSE_NUMBER = ?;";

        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, licenseNumber);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Vozač uspješno uklonjen.");
            } else {
                System.out.println("Vozač s unesenim brojem dozvole ne postoji.");
            }
        } catch (SQLException | IOException e) {
            logger.error("Greška pri uklanjanju vozača:", e);
        }
    }

    public static boolean deleteVehicleByVin(String vin) {
        boolean returnBoolean = false;
        String sql = "DELETE FROM VEHICLE WHERE VIN = ?;";

        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, vin);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Vozilo uspješno uklonjen.");
                returnBoolean = true;
            } else {
                System.out.println("Vozilo s unesenim brojem dozvole ne postoji.");
            }
        } catch (SQLException | IOException e) {
            logger.error("Greška pri uklanjanju vozila:", e);
        }
        return  returnBoolean;
    }

    public static void deleteMaintenanceScheduleDescripton(String description) {
        String sql = "DELETE FROM MaintenanceSchedule WHERE DESCRIPTION = ?;";

        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, description);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Schedule uspješno uklonjen.");
            } else {
                System.out.println("Schedule s unesenim brojem dozvole ne postoji.");
            }
        } catch (SQLException | IOException e) {
            logger.error("Greška pri uklanjanju schedule:", e);
        }
    }

    public static void removeVehicleByVin(String identifier) {
    }

    public synchronized Vehicle findById(Long id) {
        while (DATABASE_ACCESS_IN_PROGRESS) {
            try {
                wait(); // Čekamo dok se ne završi pristup bazi
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalArgumentException("Thread interrupted while waiting for database access", e);
            }
        }

        DATABASE_ACCESS_IN_PROGRESS = true;
        Vehicle vehicle = null;

        try (Connection connection = connectToDatabase()) {

            // Pripremi upit za dohvaćanje vozila po ID-u
            String sqlQuery = "SELECT * FROM VEHICLE WHERE ID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sqlQuery)) {
                stmt.setLong(1, id);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next()) {
                        String vin = resultSet.getString("VIN");
                        String manufacturer = resultSet.getString("MANUFACTURER");
                        String model = resultSet.getString("MODEL");
                        Integer year = resultSet.getObject("VEHICLE_YEAR", Integer.class);
                        String registrationNumber = resultSet.getString("REGISTRATION_NUMBER");
                        LocalDate registrationExpiryDate = (resultSet.getDate("REGISTRATION_EXPIRY_DATE") != null)
                                ? resultSet.getDate("REGISTRATION_EXPIRY_DATE").toLocalDate()
                                : null;
                        String driverLicenseNumber = resultSet.getString("DRIVER_LICENSE_NUMBER");
                        String vehicleCondition = resultSet.getString("VEHICLE_CONDITION");

                        // Provjera je li vozilo automobil ili kombi
                        if (isVan(connection, vin)) {
                            vehicle = fetchVanDetails(connection, vin, manufacturer, model, year, registrationNumber, registrationExpiryDate, driverLicenseNumber, vehicleCondition);
                        } else {
                            vehicle = new Car.CarBuilder()
                                    .vin(vin)
                                    .manufacturer(manufacturer)
                                    .model(model)
                                    .year(year)
                                    .registrationNumber(registrationNumber)
                                    .registrationExpiryDate(registrationExpiryDate)
                                    .driver(getDriverById(driverLicenseNumber))
                                    .vehicleCondition(VehicleCondition.valueOf(vehicleCondition))
                                    .range(resultSet.getFloat("RANGE"))
                                    .build();
                        }
                    } else {
                        throw new IllegalArgumentException("Vehicle with ID " + id + " not found!");
                    }
                }
            }

        } catch (IOException | SQLException e) {
            logger.error("Error accessing database in thread: ", e);
        } finally {
            DATABASE_ACCESS_IN_PROGRESS = false;
            notifyAll(); // Obavještavamo druge niti da je baza sada slobodna
        }

        return vehicle;
    }

    private boolean metoda(MaintenanceLog maintenanceLog){
        Car car = new Car.CarBuilder().build();
        car.performMaintenance(maintenanceLog);
        return false;
    }
    private boolean isVan(Connection connection, String vin) throws SQLException {
        String checkVanQuery = "SELECT 1 FROM VAN WHERE VIN = ?";
        try (PreparedStatement stmt = connection.prepareStatement(checkVanQuery)) {
            stmt.setString(1, vin);
            try (ResultSet resultSet = stmt.executeQuery()) {
                return resultSet.next(); // Ako postoji zapis, vozilo je kombi
            }
        }
    }
    private Van fetchVanDetails(Connection connection, String vin, String manufacturer, String model, Integer year,
                                String registrationNumber, LocalDate registrationExpiryDate, String driverLicenseNumber,
                                String vehicleCondition) throws SQLException {

        String sqlVanQuery = "SELECT MAX_CARGO_WEIGHT FROM VAN WHERE VIN = ?";
        try (PreparedStatement vanStatement = connection.prepareStatement(sqlVanQuery)) {
            vanStatement.setString(1, vin);
            try (ResultSet vanResultSet = vanStatement.executeQuery()) {
                if (vanResultSet.next()) {
                    return new Van.VanBuilder()
                            .vin(vin)
                            .manufacturer(manufacturer)
                            .model(model)
                            .year(year)
                            .registrationNumber(registrationNumber)
                            .registrationExpiryDate(registrationExpiryDate)
                            .driver(getDriverById(driverLicenseNumber))
                            .vehicleCondition(VehicleCondition.valueOf(vehicleCondition))
                            .maxCargoWeight(vanResultSet.getFloat("MAX_CARGO_WEIGHT"))
                            .build();
                }
            }
        }
        return null; // Ako nema dodatnih podataka, vraća null
    }


}
