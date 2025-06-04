package hr.javafx.fleetmanagementandmaintanancescheadule.controller;

import hr.javafx.fleetmanagementandmaintanancescheadule.HelloApplication;
import hr.javafx.fleetmanagementandmaintanancescheadule.model.*;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.DatabaseUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.DriverDatabaseManager;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static hr.javafx.fleetmanagementandmaintanancescheadule.controller.LoginScreenController.currentUser;
import static hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils.confirmationDialog;
/**
 * Controller for managing the drivers screen.
 * Provides functionalities to display, edit, and delete drivers in the fleet management system.
 */
public class DriversScreenController {

    private static final Logger logger = LoggerFactory.getLogger(DriversScreenController.class);
    @FXML
    private TableView<Driver> contactsTableView;
    @FXML
    private TableColumn<Driver,String> firstNameTableColumn;
    @FXML
    private TableColumn<Driver,String> secondNameTableColumn;
    @FXML
    private TableColumn<Driver,String> licenseNumberTableColumn;
    @FXML
    private TableColumn<Driver,String> birthdayTableColumn;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField licenseNumberTextField;
    /**
     * Initializes the driver screen by setting up table columns and cell value factories.
     */
    @FXML
    public void initialize() {
        firstNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        secondNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("secondName"));
        licenseNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        birthdayTableColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));

        firstNameTableColumn.setCellValueFactory(itemStringCellDataFeatures->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().getFirstName()));
        secondNameTableColumn.setCellValueFactory(itemStringCellDataFeatures->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().getSecondName()));
        licenseNumberTableColumn.setCellValueFactory(itemStringCellDataFeatures->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().getLicenseNumber()));

        birthdayTableColumn.setCellValueFactory(itemStringCellDataFeatures -> {
            String birthdayString = itemStringCellDataFeatures.getValue().getDateOfBirth() != null ?
                    itemStringCellDataFeatures.getValue().getDateOfBirth().toString() : "N/A";
            //String yearString = Optional.ofNullable(itemStringCellDataFeatures.getValue().getYear().toString()).orElse("N/A");
            return new ReadOnlyStringWrapper(birthdayString);
        });

    }
    /**
     * Filters and displays drivers based on input criteria from text fields.
     *
     * @param actionEvent The event triggered when the user clicks the "Show Drivers" button.
     */
    public void showDriversButton(ActionEvent actionEvent){
        //List<Driver> drivers = DatabaseUtils.getDrivers();
        DriverDatabaseManager driverDatabaseManager = new DriverDatabaseManager();
        List<Driver> drivers = driverDatabaseManager.getAll();
        String firstName = Optional.ofNullable(firstNameTextField.getText()).orElse("");
        String licenseNumber = Optional.ofNullable(licenseNumberTextField.getText()).orElse("");

        List<Driver> filteredDriverList = drivers.stream()
                .filter(v -> firstName.isEmpty() || Optional.ofNullable(v.getFirstName()).map(Object::toString).orElse("").contains(firstName)) // Sigurna provjer
                .filter(v -> licenseNumber.isEmpty() || Optional.ofNullable(v.getLicenseNumber()).map(Object::toString).orElse("").contains(licenseNumber))
                .collect(Collectors.toList());

        ObservableList<Driver> observableDriverList = FXCollections.observableArrayList(filteredDriverList);
        contactsTableView.setItems(observableDriverList);
    }
    /**
     * Opens the driver editing screen if an admin user is logged in.
     *
     * @param actionEvent The event triggered when the user clicks the "Edit Driver" button.
     */
    public void editDriverButton(ActionEvent actionEvent) {
        if (currentUser.equals("admin")) {
            Driver selectedDriver = null;
            try {
                selectedDriver = contactsTableView.getSelectionModel().getSelectedItem();
            } catch (RuntimeException e) {
                logger.error("No contact selected for editing", e);
            }

            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("editDriver.fxml"));
                Parent root = loader.load();

                EditDriverScreenController editDriverScreenController = loader.getController();
                editDriverScreenController.setDriver(selectedDriver);

                Scene scene = new Scene(root);
                HelloApplication.getMainStage().setTitle("Edit Driver");
                HelloApplication.getMainStage().setScene(scene);
                HelloApplication.getMainStage().show();

            } catch (Exception e) {
                logger.error("Error opening Edit Driver screen", e);
            }
        }
        else{
            WarningMessageUtils.changeAlert("Error", "Only admin can make changes!", " ", Alert.AlertType.ERROR);
        }

    }
    public void deleteDriverButton(ActionEvent actionEvent) {
        if (currentUser.equals("admin")) {
            Driver selectedDriver = null;
            try {
                selectedDriver = contactsTableView.getSelectionModel().getSelectedItem();
            } catch (RuntimeException e) {
                logger.error("No contact selected for editing", e);
            }
            if(confirmationDialog("Delete Driver","","Are you sure you want to delete driver?") && selectedDriver!=null){
                //DatabaseUtils.removeDriverByLicenseNumber(selectedDriver.getLicenseNumber());
                DriverDatabaseManager driverDatabaseManager = new DriverDatabaseManager();
                driverDatabaseManager.findById(selectedDriver.getLicenseNumber());
                PairBox<Driver, String> pairBox = new PairBox<>(selectedDriver, "");
                LocalDateTime currentDateTime = LocalDateTime.now();
                Change change = new Change(pairBox, new Box<>(currentUser), currentDateTime);
                List<Change> changeList = FileUtils.deserializeList();
                changeList.add(change);
                FileUtils.serializeList(changeList);
            }

        } else {
            WarningMessageUtils.changeAlert("Error", "Only admin can make changes!", " ", Alert.AlertType.ERROR);
        }
    }
}
