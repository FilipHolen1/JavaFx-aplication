package hr.javafx.fleetmanagementandmaintanancescheadule.controller;

import hr.javafx.fleetmanagementandmaintanancescheadule.HelloApplication;
import hr.javafx.fleetmanagementandmaintanancescheadule.model.*;
import hr.javafx.fleetmanagementandmaintanancescheadule.threads.DeleteVehicleThread;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.VehicleDatabaseManager;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.fileutils.FileUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyStringWrapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static hr.javafx.fleetmanagementandmaintanancescheadule.controller.LoginScreenController.currentUser;
import static hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils.confirmationDialog;

/**
 * Controller class for managing the vehicle screen in the Fleet Management System.
 * Provides functionalities such as displaying, filtering, editing, and deleting vehicles.
 */
public class VehiclesScreenController {
    private static final Logger logger = LoggerFactory.getLogger(VehiclesScreenController.class);
    @FXML
    private TableView<Vehicle> contactsTableView;
    @FXML
    private TableColumn<Vehicle, String> vehicleManufacturerTableColumn,
            vehicleModelTableColumn,
            vehicleYearTableColumn,
            vehicleRegistrationTableColumn,
            vehicleRegistrationExpiryTableColumn;
    @FXML
    private TextField registrationTextField;
    @FXML
    private Label mainLabel;
    @FXML
    private Label vinLabel;
    @FXML
    private Label registrationLabel;
    @FXML
    private Label registrationExpireLabel;
    @FXML
    private Label driverLabel;
    @FXML
    private Label floatLabel;
    @FXML
    private Label conditionLabel;

    @FXML
    private ComboBox<String> yearComboBox;
    @FXML
    private ComboBox<String> manufacturerComboBox;
    @FXML
    private ImageView vehicleImageView;

    @FXML
    private Label earliestExpityLabel;

    @FXML
    private Label statusLabel;

    private final ObservableList<Vehicle> observableVehicles = FXCollections.observableArrayList();

    public static Vehicle theVehicle;

    /**
     * Initializes the vehicle screen by loading all vehicles and setting up event listeners.
     */
    @FXML
    public void initialize() {
        startEarliestExpiryVehicleTimeline();
        List<Vehicle> vehicles = new ArrayList<>();
        try{
            VehicleDatabaseManager vehicleManager = new VehicleDatabaseManager();
            vehicles = vehicleManager.getAll(); // Fetch all vehicles
        }catch (RuntimeException ex){
            logger.error("Pogreska pri dogvacanju ",ex);
        }

        vehicleManufacturerTableColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        vehicleModelTableColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        vehicleYearTableColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        vehicleRegistrationTableColumn.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));

        vehicleManufacturerTableColumn.setCellValueFactory(itemStringCellDataFeatures->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().getManufacturer()));
        vehicleModelTableColumn.setCellValueFactory(itemStringCellDataFeatures->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().getModel()));

        vehicleYearTableColumn.setCellValueFactory(itemStringCellDataFeatures -> {
            String yearString = itemStringCellDataFeatures.getValue().getYear() != null ?
                    itemStringCellDataFeatures.getValue().getYear().toString() : "";
            return new ReadOnlyStringWrapper(yearString);
        });
        vehicleRegistrationTableColumn.setCellValueFactory(itemStringCellFeatures->
                new ReadOnlyStringWrapper(itemStringCellFeatures.getValue().getRegistrationNumber()));

        vehicleRegistrationExpiryTableColumn.setCellValueFactory(itemStringCellDataFeatures ->{
            String dateString = itemStringCellDataFeatures.getValue().getRegistrationExpiryDate() != null ?
                    itemStringCellDataFeatures.getValue().getRegistrationExpiryDate().toString() : "";
            return new ReadOnlyStringWrapper(dateString);
        });

        observableVehicles.addAll(vehicles);
        contactsTableView.setItems(observableVehicles);

        contactsTableView.setOnMouseClicked(event -> {
            Vehicle selectedVehicle = contactsTableView.getSelectionModel().getSelectedItem();
            if (selectedVehicle != null) {
                showVehicleDetails(selectedVehicle);
                theVehicle = selectedVehicle;
            }
        });

        Set<String> yearSet = new TreeSet<>();
        Set<String> manufacturerSet = new TreeSet<>();
        for(Vehicle vehicle : vehicles){
            if (vehicle.getYear()!=null){
                yearSet.add(vehicle.getYear().toString());
            }
            manufacturerSet.add(vehicle.getManufacturer());
        }
        yearComboBox.setItems(FXCollections.observableArrayList(yearSet));
        manufacturerComboBox.setItems(FXCollections.observableArrayList(manufacturerSet));

    }
    /**
     * Displays details of the selected vehicle.
     *
     */
    public void showVehiclesButton(ActionEvent actionEvent){
        VehicleDatabaseManager vehicleManager = new VehicleDatabaseManager();
        List<Vehicle> vehicles = vehicleManager.getAll();

        String manufacturer = Optional.ofNullable(manufacturerComboBox.getSelectionModel().getSelectedItem()).orElse("");
        String year = Optional.ofNullable(yearComboBox.getSelectionModel().getSelectedItem()).orElse("");
        String registration = Optional.ofNullable(registrationTextField.getText()).orElse("");

        List<Vehicle> filteredVehicleList = vehicles.stream()
                .filter(v -> manufacturer.isEmpty() || v.getManufacturer().equals(manufacturer))
                .filter(v -> year.isEmpty() || Optional.ofNullable(v.getYear()).map(Object::toString).orElse("").equals(year))
                .filter(v -> registration.isEmpty() || Optional.ofNullable(v.getRegistrationNumber()).map(Object::toString).orElse("").contains(registration))
                .collect(Collectors.toList());

        ObservableList<Vehicle> observableVehicleList = FXCollections.observableArrayList(filteredVehicleList);
        contactsTableView.setItems(observableVehicleList);
    }

    /**
     * Displays details of the selected vehicle.
     *
     * @param vehicle The selected vehicle.
     */

    private void showVehicleDetails(Vehicle vehicle) {
        mainLabel.setText(Optional.ofNullable(vehicle.getManufacturer()).orElse("N/A") + " " +
                Optional.ofNullable(vehicle.getModel()).orElse("N/A") + " (" +
                Optional.ofNullable(vehicle.getYear()).map(String::valueOf).orElse("N/A") + ")");
        vinLabel.setText("VIN: " + Optional.ofNullable(vehicle.getVin()).orElse("N/A"));
        registrationLabel.setText("Registracija: " + Optional.ofNullable(vehicle.getRegistrationNumber()).orElse("N/A"));
        registrationExpireLabel.setText("Registritan do: " + Optional.ofNullable(vehicle.getRegistrationExpiryDate())
                .map(Object::toString).orElse("N/A"));
        driverLabel.setText("Vozač: " + Optional.ofNullable(vehicle.getDriver())
                .map(d -> d.getFirstName() + " " + d.getSecondName()).orElse("Nepoznat vozač"));
        if (vehicle instanceof Car) {
            floatLabel.setText("Range: " + vehicle.getFloat());
        } else if (vehicle instanceof Van) {
            floatLabel.setText("Max cargo weight: " + vehicle.getFloat());
        } else {
            floatLabel.setText("Value: " + vehicle.getFloat());
        }
        VehicleCondition condition = Optional.ofNullable(vehicle.getVehicleCondition()).orElse(VehicleCondition.OK);
        conditionLabel.setText("Condition: " + condition);

        switch (condition) {
            case GOOD:
                conditionLabel.setStyle("-fx-text-fill: green;");
                break;
            case OK:
                conditionLabel.setStyle("-fx-text-fill: orange;");
                break;
            case BAD:
                conditionLabel.setStyle("-fx-text-fill: red;");
                break;
            default:
                conditionLabel.setStyle("");
        }

        File file = new File("img/" + Optional.ofNullable(vehicle.getVin()).orElse("unknown") + ".jpg");
        File noImage = new File ("img/unknown.jpg");
        try{
            if (file.exists()) {
                vehicleImageView.setImage(new Image(file.toURI().toString()));
            }
            else{
                vehicleImageView.setImage(new Image(noImage.toURI().toString()));
            }
        }catch(Exception e){
            logger.error("Pogreska pri spremanju slike", e);
        }
    }
    /**
     * Opens the maintenance logs screen.
     *
     * @param actionEvent The event triggered by clicking the logs button.
     */
    public void logsButton(ActionEvent actionEvent) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("maintenanaceLogsScreen.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
                HelloApplication.getMainStage().setTitle("maintenanace");
                HelloApplication.getMainStage().setScene(scene);
                HelloApplication.getMainStage().show();
            } catch (RuntimeException | IOException ex ) {
                logger.error("Pogreska pri otvaranju MaintenanceScreen",ex);
            }
    }


    public void editVehicleButton(ActionEvent actionEvent) {
        if (currentUser.equals("admin")) {
            Vehicle selectedVehicle = null;
            try {
                selectedVehicle = contactsTableView.getSelectionModel().getSelectedItem();
            } catch (RuntimeException e) {
                logger.error("No vehicle selected for editing", e);
            }

            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("editVehicleScreen.fxml"));
                Parent root = loader.load();

                EditVehicleScreenController editVehicleScreenController = loader.getController();
                editVehicleScreenController.setVehicle(selectedVehicle);

                Scene scene = new Scene(root);
                HelloApplication.getMainStage().setTitle("Edit Vehicle");
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

    public void deleteVehicle(ActionEvent actionEvent) {
        if (currentUser.equals("admin")) {
            Vehicle selectedVehicle = null;
            try {
                selectedVehicle = contactsTableView.getSelectionModel().getSelectedItem();
            } catch (RuntimeException e) {
                logger.error("No vehicle selected for editing", e);
            }
            if(selectedVehicle != null && confirmationDialog("Delete Vehicle","","Are you sure you want to delete vehicle?")){
                PairBox<Vehicle, String> pairBox = new PairBox<>(selectedVehicle, "");
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
                deleteVehicleFromDatabase(selectedVehicle);


            }
        } else {
            WarningMessageUtils.changeAlert("Error", "Only admin can make changes!", " ", Alert.AlertType.ERROR);
        }
    }
    private synchronized void startEarliestExpiryVehicleTimeline() {
        Timeline earliestExpiryVehicleTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    Optional<Vehicle> earliestExpiryVehicle;
                    List<Vehicle> vehicles = new ArrayList<>();
                    try{
                        VehicleDatabaseManager vehicleManager = new VehicleDatabaseManager();
                        vehicles = vehicleManager.getAll();
                    }catch (RuntimeException ex){
                        logger.error("Pogreska pri dogvacanju ",ex);
                    }
                    if (vehicles != null) {
                        earliestExpiryVehicle = vehicles.stream()
                                .filter(v -> v.getRegistrationExpiryDate() != null)
                                .min(Comparator.comparing(Vehicle::getRegistrationExpiryDate));

                        if (earliestExpiryVehicle.isPresent()) {
                            Vehicle vehicle = earliestExpiryVehicle.get();
                            earliestExpityLabel.setText(
                                    "Najraniji istek registracije: " + vehicle.getManufacturer() + " " +
                                            vehicle.getModel() + " (" + vehicle.getRegistrationExpiryDate() + ")"
                            );
                        } else {
                            earliestExpityLabel.setText("Nema dostupnih vozila.");
                        }
                    }
                }),
                new KeyFrame(Duration.seconds(5))
        );

        earliestExpiryVehicleTimeline.setCycleCount(Animation.INDEFINITE);
        earliestExpiryVehicleTimeline.play();
    }
    private void deleteVehicleFromDatabase(Vehicle vehicle) {
        DeleteVehicleThread deleteThread = new DeleteVehicleThread(vehicle, statusLabel);
        Thread runner = new Thread(deleteThread);
        runner.setDaemon(true);
        runner.start();
    }

}
