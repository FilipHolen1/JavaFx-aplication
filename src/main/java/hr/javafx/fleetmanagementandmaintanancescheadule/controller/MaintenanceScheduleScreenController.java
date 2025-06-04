package hr.javafx.fleetmanagementandmaintanancescheadule.controller;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.*;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.MaintenanceScheduleDatabaseManager;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.fileutils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static hr.javafx.fleetmanagementandmaintanancescheadule.controller.LoginScreenController.currentUser;
import static hr.javafx.fleetmanagementandmaintanancescheadule.utils.WarningMessageUtils.confirmationDialog;

public class MaintenanceScheduleScreenController {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceScheduleScreenController.class);
    @FXML
    private TableView<MaintenanceSchedule> contactsTableView;
    @FXML
    private TableColumn<MaintenanceSchedule,String> vinTableColumn,dateTableColumn,descriptionTableColumn,mechanicTableColumn;

    @FXML
    private TextField mechanicTextField,
            vinTextField;

    public void initialize(){
        vinTableColumn.setCellValueFactory(new PropertyValueFactory<>("vin"));
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        mechanicTableColumn.setCellValueFactory(new PropertyValueFactory<>("mechanic"));

        vinTableColumn.setCellValueFactory(itemStringCellDataFeatures->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().getVin()));
        dateTableColumn.setCellValueFactory(itemStringCellDataFeatures->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().getDate().toString()));
        descriptionTableColumn.setCellValueFactory(itemStringCellDataFeatures->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().getDescription()));
        mechanicTableColumn.setCellValueFactory(itemStringCellDataFeatures->
                new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().getMechanicName()));
    }

    public void showMaintenanceButton(ActionEvent actionEvent) {
        MaintenanceScheduleDatabaseManager maintenanceManager = new MaintenanceScheduleDatabaseManager();
        List<MaintenanceSchedule> maintenanceSchedules = maintenanceManager.getAll();

        String mechanic = Optional.ofNullable(mechanicTextField.getText()).orElse("");
        String vin = Optional.ofNullable(vinTextField.getText()).orElse("");

        List<MaintenanceSchedule> filtereDMaintenanceList = maintenanceSchedules.stream()
                .filter(v -> mechanic.isEmpty() || Optional.ofNullable(v.getMechanicName()).map(Object::toString).orElse("").contains(mechanic)) // Sigurna provjer
                .filter(v -> vin.isEmpty() || Optional.ofNullable(v.getVin()).map(Object::toString).orElse("").contains(vin))
                .collect(Collectors.toList());

        ObservableList<MaintenanceSchedule> observableMaintenanceList = FXCollections.observableArrayList(filtereDMaintenanceList);
        contactsTableView.setItems(observableMaintenanceList);
    }

    public void deleteScheduleButton(ActionEvent actionEvent) {
        if (currentUser.equals("admin")) {
            MaintenanceSchedule maintenanceSchedule = null;
            try {
                maintenanceSchedule = contactsTableView.getSelectionModel().getSelectedItem();
            } catch (RuntimeException e) {
                logger.error("No schedule selected for editing", e);
            }
            if(confirmationDialog("Delete Maintenance Schedule","","Are you sure you want to delete schedule?")){
                try{
                    MaintenanceScheduleDatabaseManager maintenanceManager = new MaintenanceScheduleDatabaseManager();
                    maintenanceManager.delete(maintenanceSchedule.getDescription());
                }catch (NullPointerException e){
                    logger.error("Pogreska prilikom brisanja podatka", e);
                }

                PairBox<MaintenanceSchedule, String> pairBox = new PairBox<>(maintenanceSchedule, "");
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
        } else {
            WarningMessageUtils.changeAlert("Error", "Only admin can make changes!", " ", Alert.AlertType.ERROR);
        }
    }
}
