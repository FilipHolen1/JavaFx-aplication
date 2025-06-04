package hr.javafx.fleetmanagementandmaintanancescheadule.controller;

import hr.javafx.fleetmanagementandmaintanancescheadule.exceptions.InvalidDateFormatException;
import hr.javafx.fleetmanagementandmaintanancescheadule.model.*;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.DatePickerUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.LocalDateUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.DatabaseUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.DriverDatabaseManager;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.fileutils.FileUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import static hr.javafx.fleetmanagementandmaintanancescheadule.controller.LoginScreenController.currentUser;
/**
 * Controller class for handling the addition of new driver in the fleet management system.
 */
public class AddDriverScreenController {
    private static final Logger logger = LoggerFactory.getLogger(AddDriverScreenController.class);
    @FXML
    private TextField firstNameTextField,secondNameTextField,licenseNumberTextField;
    @FXML
    private Label firstNameErrorLabel,secondNameErrorLabel,licenseNumberErrorLabel;

    @FXML
    private DatePicker birthdayDatePicker;

    @FXML
    /**
     * Initializes the vehicle addition form.
     * Sets up text field validation, loads drivers, and configures UI elements.
     */
    public void initialize(){
        DatePickerUtils.setupDatePickerFormat(birthdayDatePicker);
    }
    /**
     * Saves driver to datbase
     */
    public void saveDriverButton(ActionEvent actionEvent) {
        if(badInputForRequiredField()){
            String firstName = firstNameTextField.getText();
            String secondName = secondNameTextField.getText();
            String licenseNumber = licenseNumberTextField.getText();
            LocalDate birthdayDate = birthdayDatePicker.getValue();

            if(birthdayDate!=null){
                try {
                    LocalDateUtils.validateDate(birthdayDate);
                } catch (InvalidDateFormatException ex) {
                    //WarningMessageUtils.wrongDateFormatAlert();
                    logger.error(ex.getMessage(),ex);
                }
            }
            Driver driver = new Driver.DriverBuilder()
                    .firstName(firstName).secondName(secondName)
                    .licenseNumber(licenseNumber).dateOfBirth(birthdayDate).build();
            //DatabaseUtils.addDriver(driver);
            DriverDatabaseManager driverDatabaseManager = new DriverDatabaseManager();
            driverDatabaseManager.add(driver);
            PairBox<String , Driver> pairBox = new PairBox<>("",driver);
            LocalDateTime currentDateTime = LocalDateTime.now();
            Change change = new Change(pairBox, new Box<>(currentUser), currentDateTime);
            List<Change> changeList = FileUtils.deserializeList();
            changeList.add(change);
            FileUtils.serializeList(changeList);
        }

    }

    private Boolean badInputForRequiredField(){
        Boolean returnValue = true;
        firstNameErrorLabel.setText("");
        secondNameErrorLabel.setText("");
        licenseNumberErrorLabel.setText("");

        if(firstNameTextField.getText().isEmpty()){
            firstNameErrorLabel.setText("First name is a required field!");
            returnValue=false;
        }
        if(secondNameTextField.getText().isEmpty()){
            secondNameErrorLabel.setText("Second name is a required field!");
            returnValue= false;
        }
        if(licenseNumberTextField.getText().isEmpty()){
            licenseNumberErrorLabel.setText("License is a required field!");
            returnValue=false;
        }
        return returnValue;
    }


}
