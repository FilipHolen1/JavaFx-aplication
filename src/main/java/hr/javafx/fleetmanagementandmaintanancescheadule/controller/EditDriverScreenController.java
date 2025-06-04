package hr.javafx.fleetmanagementandmaintanancescheadule.controller;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.Box;
import hr.javafx.fleetmanagementandmaintanancescheadule.model.Change;
import hr.javafx.fleetmanagementandmaintanancescheadule.model.Driver;
import hr.javafx.fleetmanagementandmaintanancescheadule.model.PairBox;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.DatePickerUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.LocalDateUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.StringUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.DatabaseUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.DriverDatabaseManager;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.fileutils.FileUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static hr.javafx.fleetmanagementandmaintanancescheadule.controller.LoginScreenController.currentUser;
import static hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils.confirmationDialog;
/**
 * Controller for editing driver details in the Fleet Management and Maintenance Schedule application.
 * Allows users to modify driver information and update records in the database.
 */
public class EditDriverScreenController {
    private static final Logger logger = LoggerFactory.getLogger(EditDriverScreenController.class);
    private static Driver oldDriver;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField secondNameTextField;
    @FXML
    private TextField licenseNumberTextField;
    @FXML
    private Label firstNameErrorLabel;
    @FXML
    private Label secondNameErrorLabel;
    @FXML
    private Label licenseNumberErrorLabel;

    @FXML
    private DatePicker birthdayDatePicker;
    /**
     * Initializes the edit driver screen by setting up date formatting for the date picker.
     */
    @FXML
    public void initialize(){
        DatePickerUtils.setupDatePickerFormat(birthdayDatePicker);
    }
    /**
     * Populates the form with the existing driver information for editing.
     *
     * @param driver The driver whose details are to be edited.
     */
    public void setDriver(Driver driver){
        oldDriver = driver;
        firstNameTextField.setText(driver.getFirstName());
        secondNameTextField.setText(driver.getSecondName());
        licenseNumberTextField.setText(driver.getLicenseNumber());
        if (driver.getDateOfBirth()!=null){
            birthdayDatePicker.setValue(driver.getDateOfBirth());
        }
    }
    /**
     * Validates the required fields to ensure no mandatory field is left empty.
     *
     * @return {@code true} if all required fields are correctly filled, {@code false} otherwise.
     */
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

    public void saveDriverButton(ActionEvent actionEvent) {
        if(Boolean.TRUE.equals(badInputForRequiredField())) {
            String firstName = StringUtils.capitalizeFirstLetter(firstNameTextField.getText());
            String secondName = StringUtils.capitalizeFirstLetter(secondNameTextField.getText());
            String licenseNumber = licenseNumberTextField.getText();
            LocalDate selectedDate = null;
            try{
                if(birthdayDatePicker.getValue()!=null) {
                     selectedDate = LocalDate.of(birthdayDatePicker.getValue().getYear(),
                            birthdayDatePicker.getValue().getMonth(),
                            birthdayDatePicker.getValue().getDayOfMonth());
                    LocalDateUtils.validateDate(selectedDate);
                }
            }catch (RuntimeException e){
                String message="pogresan oblik datuma";
                logger.error(message,e);
            }
            if(confirmationDialog("Edit Driver","","Are you sure you want to edit driver?")) {
                //DatabaseUtils.removeDriverByLicenseNumber(oldDriver.getLicenseNumber());
                DriverDatabaseManager driverDatabaseManager = new DriverDatabaseManager();
                driverDatabaseManager.delete(oldDriver.getLicenseNumber());
                Driver newDriver = new Driver.DriverBuilder().firstName(firstName).secondName(secondName)
                        .licenseNumber(licenseNumber).dateOfBirth(selectedDate).build();
                //DatabaseUtils.addDriver(newDriver);
                DriverDatabaseManager driverDatabaseManager1 = new DriverDatabaseManager();
                driverDatabaseManager1.add(newDriver);
                PairBox<Driver, Driver> pairBox = new PairBox<>(oldDriver, newDriver);
                LocalDateTime currentDateTime = LocalDateTime.now();
                Change change = new Change(pairBox, new Box<>(currentUser), currentDateTime);
                if (FileUtils.deserializeList() != null) {
                    List<Change> changes = FileUtils.deserializeList();
                    changes.add(change);
                    FileUtils.serializeList(changes);
                } else {
                    List<Change> changeList = new ArrayList<>();
                    changeList.add(change);
                    FileUtils.serializeList(changeList);
                }
            }

            WarningMessageUtils.changeAlert("Success!","Driver was successfully edited!","", Alert.AlertType.INFORMATION);
        }

    }



}

