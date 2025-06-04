package hr.javafx.fleetmanagementandmaintanancescheadule.controller;

import hr.javafx.fleetmanagementandmaintanancescheadule.exceptions.InvalidContentLength;
import hr.javafx.fleetmanagementandmaintanancescheadule.exceptions.InvalidDateFormatException;
import hr.javafx.fleetmanagementandmaintanancescheadule.model.*;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.DatePickerUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.LocalDateUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.StringUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.MaintenanceScheduleDatabaseManager;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.VehicleDatabaseManager;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.fileutils.FileUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import static hr.javafx.fleetmanagementandmaintanancescheadule.controller.LoginScreenController.currentUser;
/**
 * Controller class for handling the addition of new mainrenane in the fleet management system.
 */
public class AddMaintenanceScheduleScreenController {
    private static final Logger logger = LoggerFactory.getLogger(AddMaintenanceLogScreenController.class);
    @FXML
    private ComboBox<Vehicle> vehicleComboBox;
    @FXML
    private TextField mechanicTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private DatePicker dateDatePicker;

    /**
     * Initializes the vehicle addition form.
     * Sets up text field validation, loads drivers, and configures UI elements.
     */
    public void initialize(){
        //List<Vehicle> vehicles = DatabaseUtils.getVehicles();
        VehicleDatabaseManager vehicleDatabaseManager = new VehicleDatabaseManager();
        List<Vehicle> vehicles = vehicleDatabaseManager.getAll();
        DatePickerUtils.setupDatePickerFormat(dateDatePicker);
        vehicleComboBox.setItems(FXCollections.observableArrayList(vehicles));
        try {
            vehicleComboBox.setValue(vehicles.getFirst());
        }catch (RuntimeException ex){
            logger.error("Nema nitijednog vozila",ex);
        }
    }
    public void saveMaintenanceScheduleButton(ActionEvent actionEvent) {
        if(badInputForRequired()){
            String vin = vehicleComboBox.getValue().getVin();
            String mechanic = mechanicTextField.getText();
            String description = descriptionTextArea.getText();
            LocalDate scheduleDat = dateDatePicker.getValue();

            try{
                StringUtils.checkStringLength(description);
            }catch (InvalidContentLength ex){
                logger.error(ex.getMessage(),ex);
            }
            MaintenanceSchedule maintenanceSchedule = new MaintenanceSchedule.MaintenanceScheduleBuilder().vin(vin)
                    .date(scheduleDat).description(description).mechanicName(mechanic).build();
            //DatabaseUtils.addMaintenanceSchedule(maintenanceSchedule);
            MaintenanceScheduleDatabaseManager maintenanceScheduleDatabaseManager = new MaintenanceScheduleDatabaseManager();
            maintenanceScheduleDatabaseManager.add(maintenanceSchedule);
            PairBox<String, MaintenanceSchedule> pairBox = new PairBox<>("", maintenanceSchedule);
            LocalDateTime currentDateTime = LocalDateTime.now();
            Change change = new Change(pairBox, new Box<>(currentUser), currentDateTime);
            //List<Change> changeList = FileUtils.deserializeList();
            //changeList.add(change);
            //FileUtils.serializeList(changeList);
            if (FileUtils.deserializeList() != null) {
                List<Change> changes = FileUtils.deserializeList();
                changes.add(change);
                FileUtils.serializeList(changes);
            } else {
                List<Change> changeList = new ArrayList<>();
                changeList.add(change);
                FileUtils.serializeList(changeList);
            }
            WarningMessageUtils.changeAlert("Saved successfull!","","Maintenance Schedule saved successfully!",Alert.AlertType.CONFIRMATION);
        }
    }

    private Boolean badInputForRequired() throws InvalidDateFormatException {
        Boolean returnValue = true;
        String warningMessage = "";

        if (mechanicTextField.getText().isEmpty()) {
            warningMessage += "\nMechanic Name,";
            returnValue = false;
        }
        if (descriptionTextArea.getText().isEmpty()) {
            warningMessage += "\nDescription";
            returnValue = false;
        }
        try{
            LocalDate selectedDate = LocalDate.of(dateDatePicker.getValue().getYear(),
                    dateDatePicker.getValue().getMonth(),
                    dateDatePicker.getValue().getDayOfMonth());
        }catch (Exception e){
            String message="pogresan oblik datuma";
            logger.error(message,e);
        }

        if (dateDatePicker.getValue()== null) {
            warningMessage += "\nSchedule Date,";
            returnValue = false;
        }
        else{
            try {
                LocalDate selectedDate = dateDatePicker.getValue();
                LocalDateUtils.validateDate(selectedDate);
            } catch (DateTimeParseException | InvalidDateFormatException e) {
                logger.error(e.getMessage(),e);
                dateDatePicker.setValue(null);
                warningMessage +="\nSchedule Date";
                returnValue = false;
            }
        }


        if (!warningMessage.isEmpty()) {
            WarningMessageUtils.showWarningDialog("Cannnot save withou:",warningMessage);

        }

        return returnValue;
    }


}
