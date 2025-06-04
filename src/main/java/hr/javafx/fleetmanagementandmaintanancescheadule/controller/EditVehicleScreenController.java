package hr.javafx.fleetmanagementandmaintanancescheadule.controller;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.*;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.DatePickerUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.LocalDateUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.DatabaseUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.DriverDatabaseManager;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.VehicleDatabaseManager;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.fileutils.FileUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static hr.javafx.fleetmanagementandmaintanancescheadule.controller.LoginScreenController.currentUser;
import static hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils.confirmationDialog;
/**
 * Controller for editing vehicle details in the Fleet Management and Maintenance Schedule application.
 * Allows users to modify vehicle attributes, update images, and store changes in the database.
 */
public class EditVehicleScreenController {
    private static final Logger logger = LoggerFactory.getLogger(EditVehicleScreenController.class);
    private static Vehicle oldVehicle;
    @FXML
    private TextField vinTextField, manufacturerTextField, modelTextField, yearTextField, registrationTextField,floatTextField;
    @FXML
    private Label floatLabel,vinErrorLabel,manufacturerErrorLabel,modelErrorLabel,yearErrorLabel,registrationNumberErrorLabel,floatErrorLabel;
    @FXML
    private DatePicker registrationExpiryDatePicker;
    @FXML
    private ComboBox<String> conditionComboBox;
    @FXML
    private ComboBox<Driver> driverComboBox;
    @FXML
    private ImageView addVehicleImageView;
    @FXML
    private RadioButton carRadioButton;
    @FXML
    private RadioButton vanRadioButton;
    @FXML
    private ToggleGroup vehicleTypeGroup;
    private static File selectedImageFile;
    /**
     * Initializes the edit vehicle screen, populates UI components,
     * and sets up necessary event listeners.
     */
    @FXML
    public void initialize() {
        conditionComboBox.setItems(FXCollections.observableArrayList("GOOD", "OK", "BAD"));
        DatePickerUtils.setupDatePickerFormat(registrationExpiryDatePicker);
        loadDrivers();
        vehicleTypeGroup = new ToggleGroup();
        carRadioButton.setToggleGroup(vehicleTypeGroup);
        vanRadioButton.setToggleGroup(vehicleTypeGroup);
        floatLabel.textProperty().bind(
                Bindings.when(carRadioButton.selectedProperty())
                        .then("Range: ")
                        .otherwise("Max cargo weight: ")
        );

    }
    /**
     * Loads the list of available drivers from the database and populates the driver ComboBox.
     */
    private void loadDrivers() {
        DriverDatabaseManager driverDatabaseManager = new DriverDatabaseManager();
        List<Driver> drivers = driverDatabaseManager.getAll();
        driverComboBox.setItems(FXCollections.observableArrayList(drivers));
    }
    /**
     * Opens a file chooser dialog for the user to select an image for the vehicle.
     */    @FXML
    private void addImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            selectedImageFile = file;
            addVehicleImageView.setImage(new Image(file.toURI().toString()));
            logger.info("Odabrana slika: " + file.getAbsolutePath());
        }
    }
    @FXML
    private void saveVehicle() {
        if(badInputForRequiredField() && confirmationDialog("Edit Vehicle","","Are you sure you want to edit vehicle?")){
            String vin = vinTextField.getText();
            String manufacturer = manufacturerTextField.getText();
            String model = modelTextField.getText();
            String yearText = yearTextField.getText();
            String registrationNumber = registrationTextField.getText();
            LocalDate registrationExpiry = registrationExpiryDatePicker.getValue();
            float floatValue;
            try {
                floatValue = Float.parseFloat(floatTextField.getText().trim());
            } catch (NumberFormatException e) {
                logger.error("Invalid number format: " + floatTextField.getText(), e);
                floatValue = 0.0f;
            }

            if(registrationExpiry!=null){
                try {
                    LocalDateUtils.validateDate(registrationExpiry);
                } catch (Exception ex) {
                    logger.error(ex.getMessage(),ex);
                }
            }
            String condition = conditionComboBox.getValue();
            VehicleCondition vehicleCondition = Optional.ofNullable(condition)
                    .map(VehicleCondition::valueOf)
                    .orElse(VehicleCondition.OK);
            Driver selectedDriver = driverComboBox.getValue();
            String vehicleType = (carRadioButton.isSelected()) ? "Car" : "Van";
            String carString = manufacturer + " " + model + "    (" + yearText +")";

            if(WarningMessageUtils.confirmationDialog("Save Vehicle?","",carString)) {
                int year;
                try {
                    year = Integer.parseInt(yearText);
                } catch (NumberFormatException e) {
                    return;
                }
                Vehicle vehicle = null;
                if (vehicleType.equals("Car")) {
                    vehicle = new Car.CarBuilder().vin(vin).manufacturer(manufacturer).model(model)
                            .year(year).registrationNumber(registrationNumber).registrationExpiryDate(registrationExpiry)
                            .vehicleCondition(vehicleCondition).driver(selectedDriver).range(floatValue).build();
                } else if (vehicleType.equals("Van")) {
                    vehicle = new Van.VanBuilder().vin(vin).manufacturer(manufacturer).model(model)
                            .year(year).registrationNumber(registrationNumber).registrationExpiryDate(registrationExpiry)
                            .vehicleCondition(vehicleCondition).driver(selectedDriver).maxCargoWeight(floatValue).build();
                }
                //DatabaseUtils.deleteVehicleByVin(oldVehicle.getVin());
                //DatabaseUtils.addVehicle(vehicle);
                VehicleDatabaseManager vehicleDatabaseManager = new VehicleDatabaseManager();
                vehicleDatabaseManager.delete(oldVehicle.getIdentifier());
                vehicleDatabaseManager.add(vehicle);

                PairBox<Vehicle, Vehicle> pairBox = new PairBox<>(oldVehicle, vehicle);
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

                try {
                    saveImage(vehicle);
                } catch (NullPointerException e) {
                    logger.error("Pogreska prilokom spremanja slike", e);
                }
            }
        }
    }

    private static void saveImage(Vehicle vehicle){
        File destinationFile = new File("img/" + vehicle.getVin() + ".jpg");
        try {
            Files.copy(selectedImageFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.info("Slika spremljena kao: " + destinationFile.getAbsolutePath());
        } catch (IOException e) {
            logger.info("Greška pri spremanju slike: " + e.getMessage());
            e.printStackTrace();
        }
        selectedImageFile=null;
    }

    private Boolean badInputForRequiredField(){
        Boolean returnValue = true;
        vinErrorLabel.setText("");
        manufacturerErrorLabel.setText("");
        modelErrorLabel.setText("");
        yearErrorLabel.setText("");
        registrationNumberErrorLabel.setText("");
        floatErrorLabel.setText("");
        if(vinTextField.getText().isEmpty()){
            vinErrorLabel.setText("VIN is a required field!");
            returnValue=false;
        }
        if(manufacturerTextField.getText().isEmpty()){
            manufacturerErrorLabel.setText("Manufacturer is a required field!");
            returnValue= false;
        }
        if(modelTextField.getText().isEmpty()){
            modelErrorLabel.setText("Model is a required field!");
            returnValue=false;
        }
        try {
            int year = Integer.parseInt(yearTextField.getText());
            if (year < 1990 || year > 2025) {
                yearErrorLabel.setText("Choose year between 1990 - 2025");
                returnValue = false;
            }
        } catch (NumberFormatException e) {
            yearErrorLabel.setText("Year must be a number!");
            returnValue = false;
        }
        if(!registrationTextField.getText().isEmpty() && !isValidFormat(registrationTextField.getText())){
            registrationNumberErrorLabel.setText("Wrong registration number format!");
            registrationTextField.setText("");
        }
        return returnValue;
    }
    public boolean isValidFormat(String input) {
        if (input == null) {
            return false;
        }
        String pattern = "^[A-Z]{2}-\\d{4}-[A-Z]{2}$";
        return input.matches(pattern);
    }

    public void setVehicle(Vehicle selectedVehicle) {
        oldVehicle = selectedVehicle;
        vinTextField.setText(selectedVehicle.getVin());
        manufacturerTextField.setText(selectedVehicle.getManufacturer());
        modelTextField.setText(selectedVehicle.getModel());
        yearTextField.setText(selectedVehicle.getYear().toString());
        registrationTextField.setText(selectedVehicle.getRegistrationNumber());
        if(selectedVehicle.getRegistrationExpiryDate()!=null){
            registrationExpiryDatePicker.setValue(selectedVehicle.getRegistrationExpiryDate());
        }
        floatTextField.setText(String.valueOf(selectedVehicle.getFloat()));
        conditionComboBox.setValue(selectedVehicle.getVehicleCondition().toString());
        driverComboBox.setValue(selectedVehicle.getDriver());
        if (selectedVehicle instanceof Car car) {
            carRadioButton.setSelected(true);
        } else if (selectedVehicle instanceof Van van) {
            vanRadioButton.setSelected(true);
        }
    }
}
