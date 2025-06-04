package hr.javafx.fleetmanagementandmaintanancescheadule.controller;


import hr.javafx.fleetmanagementandmaintanancescheadule.exceptions.FailedLoginException;
import hr.javafx.fleetmanagementandmaintanancescheadule.exceptions.UsernameNonExistentException;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.PasswordHashing;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Controller for managing the login screen in the Fleet Management and Maintenance Schedule application.
 * This class handles user authentication by checking entered credentials against stored hashed passwords.
 */
public class LoginScreenController extends PasswordHashing {

    private static final Logger logger = LoggerFactory.getLogger(LoginScreenController.class);
    private static final String USERS_FILE_PATH = "dat/users.txt";
    private static final Map<String, String> users = new HashMap<>();

    public static String currentUser="admin";

    @FXML
    private TextField usernameTextField ;

    @FXML
    private PasswordField passwordTextField;
    /**
     * Checks if the entered username and password match stored credentials.
     * If successful, navigates to the main vehicle screen.
     *
     * @throws FailedLoginException if authentication fails.
     */
    @FXML
    public void checkUsernameAndPasssword() throws FailedLoginException {
        loadUsers();
        String username=usernameTextField.getText();
        String password = doHashing(passwordTextField.getText());
        try {
            authenticateUser(username, password);
            logger.info("Uspjesna prijava!");
            currentUser = username;
            logger.info("current user: "+ currentUser);
            MenuScreenController menuScreenController = new MenuScreenController();
            menuScreenController.showScreenVehicles();

        }catch (FailedLoginException | UsernameNonExistentException e){
            wrongPasswordAlert();
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * Displays an alert for incorrect login attempts and clears the input fields.
     */
    private void wrongPasswordAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("login unsuccessful!");
        alert.setHeaderText("Wrong username or password!");
        alert.setContentText("Try again");
        alert.showAndWait();
        usernameTextField.clear();
        passwordTextField.clear();
    }
    /**
     * Loads stored users and hashed passwords from a file into a HashMap.
     */
    private static void loadUsers() {
        try {
            Path filePath = Paths.get(USERS_FILE_PATH);
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            for (String line : lines) {
                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    String username = parts[0];
                    String hashedPassword = parts[1];
                    users.put(username, hashedPassword);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * Authenticates a user by verifying their username and hashed password.
     *
     * @param username The entered username.
     * @param password The hashed password to be verified.
     * @throws FailedLoginException         if the password is incorrect.
     * @throws UsernameNonExistentException if the username does not exist in the system.
     */
    private static void authenticateUser(String username, String password) throws FailedLoginException, UsernameNonExistentException {
        if (users.containsKey(username)) {
            if (password.equals(users.get(username))) {
                return;
            } else {
                throw new FailedLoginException("Incorrect password for username '" + username + "'");
            }
        } else {
            throw new UsernameNonExistentException("Account with username '" + username + "' doesn't exist!");
        }
    }


}