package hr.javafx.fleetmanagementandmaintanancescheadule.controller;

import hr.javafx.fleetmanagementandmaintanancescheadule.HelloApplication;
import hr.javafx.fleetmanagementandmaintanancescheadule.exceptions.InvalidContentLength;
import hr.javafx.fleetmanagementandmaintanancescheadule.model.*;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.DatePickerUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.StringUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.DatabaseUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.MaintenanceLogsDatabaseManager;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.fileutils.FileUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static hr.javafx.fleetmanagementandmaintanancescheadule.controller.LoginScreenController.currentUser;
import static hr.javafx.fleetmanagementandmaintanancescheadule.controller.VehiclesScreenController.theVehicle;
/**
 * Controller class for handling the addition of new maintenance logs in the fleet management system.
 */
public class AddMaintenanceLogScreenController {

    private static final Logger logger = LoggerFactory.getLogger(AddMaintenanceLogScreenController.class);
    @FXML
    private TextField mechanicTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private DatePicker dateDatePicker;
    @FXML
    private Label carLabel;
    /**
     * Initializes the vehicle addition form.
     * Sets up text field validation, loads drivers, and configures UI elements.
     */
    public void initialize(){
        carLabel.setText(theVehicle.getManufacturer() + " "+theVehicle.getModel() + "  (" + theVehicle.getYear() +")");
        DatePickerUtils.setupDatePickerFormat(dateDatePicker);
    }
    /**
     * Save new Maintenance log to database
     *
     */
    public void saveMaintenanceLogButton(ActionEvent actionEvent) {
        String vin = theVehicle.getVin();
        String mechanic = mechanicTextField.getText();
        String description = descriptionTextArea.getText();
        try{
            StringUtils.checkStringLength(description);
        }catch (InvalidContentLength ex){
            descriptionTextArea.setText("");
            description="";
            logger.error(ex.getMessage(),ex);
        }
        LocalDate date = dateDatePicker.getValue();
        if (!description.isEmpty()){
            try{
                MaintenanceLog maintenanceLog = new MaintenanceLog(vin,date,description,mechanic);
                //DatabaseUtils.addMaintenanceLog(maintenanceLog);
                MaintenanceLogsDatabaseManager maintenanceLogsDatabaseManager = new MaintenanceLogsDatabaseManager();
                maintenanceLogsDatabaseManager.add(maintenanceLog);
                PairBox<String, MaintenanceLog> pairBox = new PairBox<>("", maintenanceLog);
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
                WarningMessageUtils.changeAlert("Saved successfull!","","Maintenance Schedule saved successfully!",Alert.AlertType.CONFIRMATION);

            }
            catch(IllegalArgumentException ex){
                logger.error(ex.getMessage(),ex);
            }
        }
        else{
            WarningMessageUtils.showWarningDialog("Description error","Description cannot be empty!");
        }

    }

    public void logsButton(ActionEvent actionEvent) {
        String selectedVehicle = theVehicle.getIdentifier();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("maintenanaceLogsScreen.fxml"));
            Parent root = fxmlLoader.load();
            //MaintenanceLogScreenController maintenanceLogScreenController = fxmlLoader.getController();
            //maintenanceLogScreenController.setVin(theVehicle.getVin());
            Scene scene = new Scene(root);
            HelloApplication.getMainStage().setTitle("Maintenance Logs");
            HelloApplication.getMainStage().setScene(scene);

            HelloApplication.getMainStage().show();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        System.out.println("Selected Vehicle "+ selectedVehicle);
    }

}


