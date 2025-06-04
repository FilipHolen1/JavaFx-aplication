package hr.javafx.fleetmanagementandmaintanancescheadule.controller;

import hr.javafx.fleetmanagementandmaintanancescheadule.HelloApplication;
import hr.javafx.fleetmanagementandmaintanancescheadule.model.*;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.DatabaseUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.MaintenanceLogsDatabaseManager;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.fileutils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static hr.javafx.fleetmanagementandmaintanancescheadule.controller.LoginScreenController.currentUser;
import static hr.javafx.fleetmanagementandmaintanancescheadule.controller.VehiclesScreenController.theVehicle;
/**
 * Controller for managing the maintenance log screen in the Fleet Management and Maintenance Schedule application.
 * This class handles displaying, adding, editing, and deleting maintenance logs.
 */

public class MaintenanceLogScreenController {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceLogScreenController.class);
    @FXML
    private TableView<MaintenanceLog> contactsTableView;
    @FXML
    private TableColumn<MaintenanceLog, String> costTableColumn, vinTableColumn, dateTableColumn, descriptionTableColumn, mechanicTableColumn;

    @FXML
    private Label carLabel;
    private final ObservableList<MaintenanceLog> observableLogs = FXCollections.observableArrayList();

    /**
     * Initializes the maintenance log screen by loading logs from the database and populating the TableView.
     */
    public void initialize() {
        //List<MaintenanceLog> maintenanceLogs = DatabaseUtils.getMaintenanceLogsByVin(theVehicle.getVin());
        MaintenanceLogsDatabaseManager maintenanceLogsDatabaseManager = new MaintenanceLogsDatabaseManager();
        List<MaintenanceLog> maintenanceLogs = maintenanceLogsDatabaseManager.getMaintenanceLogsByVin(theVehicle.getVin());

        vinTableColumn.setCellValueFactory(new PropertyValueFactory<>("vin"));
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        mechanicTableColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));


        vinTableColumn.setCellValueFactory(itemStringCellDataFeatures ->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().vin()));
        dateTableColumn.setCellValueFactory(itemStringCellDataFeatures ->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().date().toString()));
        mechanicTableColumn.setCellValueFactory(itemStringCellDataFeatures ->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().mechanicName()));
        descriptionTableColumn.setCellValueFactory(itemStringCellDataFeatures ->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().description()));

        // Popuniti TableView s podacima
        carLabel.setText(theVehicle.getManufacturer() + " " + theVehicle.getModel() + "   (" + theVehicle.getYear() + ")");
        observableLogs.addAll(maintenanceLogs);
        contactsTableView.setItems(observableLogs);
    }


    public void setVin(String selectedVehicle) {
        logger.info(selectedVehicle);
    }
    /**
     * Opens the add maintenance log screen.
     *
     * @param actionEvent The event that triggered this action.
     */
    public void addLogButton(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addMaintenanceLogScreen.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            HelloApplication.getMainStage().setTitle("Add Maintenance Logs");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (RuntimeException | IOException e) {
            logger.error("Error opening screen", e);
        }
    }


    public void editLogButton(ActionEvent actionEvent) {
        if (currentUser.equals("admin")) {
            try {
            } catch (RuntimeException e) {
                logger.error("No vehicle selected for editing", e);
            }
            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("editMaintenanceLogs.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                HelloApplication.getMainStage().setTitle("Edit Log");
                HelloApplication.getMainStage().setScene(scene);
                HelloApplication.getMainStage().show();

            } catch (Exception e) {
                logger.error("Error opening Edit Maintenance Log screen", e);
            }
        }
        else{
            WarningMessageUtils.changeAlert("Error", "Only admin can make changes!", " ", Alert.AlertType.ERROR);
        }
    }

    public void deleteLog(ActionEvent actionEvent) {
        if (currentUser.equals("admin")) {
            MaintenanceLog selectedLog = contactsTableView.getSelectionModel().getSelectedItem();

            if (selectedLog == null) {
                WarningMessageUtils.changeAlert("Error!", "No log selected!", "Please select a maintenance log first.", Alert.AlertType.ERROR);
                return;
            }
            boolean confirm = WarningMessageUtils.confirmationDialog(
                    "Delete Log",
                    "Are you sure?",
                    "Do you really want to delete this maintenance log?"
            );

            if (confirm) {
                MaintenanceLogsDatabaseManager logsManager = new MaintenanceLogsDatabaseManager();
                boolean success = logsManager.deleteLog(selectedLog.date(), selectedLog.description(), selectedLog.mechanicName());

                if (success) {
                    WarningMessageUtils.changeAlert("Success", "Log Deleted", "The maintenance log has been removed successfully.", Alert.AlertType.INFORMATION);
                    observableLogs.remove(selectedLog);
                    PairBox<MaintenanceLog, String> pairBox = new PairBox<>(selectedLog, "");
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
                } else {
                    WarningMessageUtils.changeAlert("Error", "Deletion Failed", "Could not delete the maintenance log.", Alert.AlertType.ERROR);
                }
            }
        }
        else{
            WarningMessageUtils.changeAlert("Error", "Only admin can make changes!", " ", Alert.AlertType.ERROR);
        }
    }
}


