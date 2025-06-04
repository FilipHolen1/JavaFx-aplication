package hr.javafx.fleetmanagementandmaintanancescheadule.controller;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.Change;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.fileutils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * Controller class for displaying a list of changes in the Fleet Management and Maintenance Schedule application.
 * This class loads data from serialized change logs and presents it in a table view.
 */
public class ShowChangesScreenController {
    private static final Logger logger = LoggerFactory.getLogger(ShowChangesScreenController.class);
    @FXML
    private TableView<Change> changesTableView;

    @FXML
    private TableColumn<Change,String> changesTypeTableColumn;

    @FXML
    private TableColumn<Change,String> changesOldValueTableColumn;

    @FXML
    private TableColumn<Change,String> changesNewValueTableColumn;

    @FXML
    private TableColumn<Change,String> changesUsernameTableColumn;
    @FXML
    private TableColumn<Change,String> changesTimeOfChangeTableColumn;
    /**
     * Initializes the table view and binds column data from the list of changes.
     * Also sets up dynamic row styling based on change type.
     */
    @FXML
    public void initialize(){
        changesTypeTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Change,
                String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Change, String> itemStringCellDataFeatures) {
                String resultString="";
                if(itemStringCellDataFeatures.getValue().getOldAndNew().getFirst()!=null){
                    resultString=itemStringCellDataFeatures.getValue().getOldAndNew().getSecond().getClass().getSimpleName();
                    if(resultString.contains("Str")){
                        resultString = itemStringCellDataFeatures.getValue().getOldAndNew().getFirst().getClass().getSimpleName();
                    }
                }
                else{
                    resultString=itemStringCellDataFeatures.getValue().getOldAndNew().getFirst().getClass().getSimpleName();
                }
                return new ReadOnlyStringWrapper(resultString);
            }
        });

        changesOldValueTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Change,
                String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Change, String> itemStringCellDataFeatures) {
                String resultString="";
                if(itemStringCellDataFeatures.getValue()!=null && itemStringCellDataFeatures.getValue().getOldAndNew()!=null && itemStringCellDataFeatures.getValue().getOldAndNew().getFirst()!=null){
                    resultString = itemStringCellDataFeatures.getValue().getOldAndNew().getFirst().toString();

                }
                return new ReadOnlyStringWrapper(resultString);
            }
        });

        changesNewValueTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Change,
                String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Change, String> itemStringCellDataFeatures) {
                String resultString="";
                if(itemStringCellDataFeatures.getValue()!=null && itemStringCellDataFeatures.getValue().getOldAndNew()!=null && itemStringCellDataFeatures.getValue().getOldAndNew().getSecond()!=null){
                    resultString = itemStringCellDataFeatures.getValue().getOldAndNew().getSecond().toString();
                }
                return new ReadOnlyStringWrapper(resultString);
            }
        });

        changesUsernameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Change, String>,
                ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Change, String> itemStringCellDataFeatures) {
                return new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().getUser().getContent());
            }
        });

        changesTimeOfChangeTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Change, String>,
                ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Change, String> itemStringCellDataFeatures) {
                return new ReadOnlyStringWrapper(itemStringCellDataFeatures.getValue().getLocalDateTime().toLocalDate().toString() + "\n" + itemStringCellDataFeatures.getValue().getLocalDateTime().toLocalTime().toString());
            }
        });


        changesTableView.setRowFactory(tv -> new TableRow<Change>() {
            @Override
            protected void updateItem(Change change, boolean empty) {
                super.updateItem(change, empty);

                if (change == null || change.getOldAndNew() == null) {
                    setStyle("");
                } else {
                    try {
                        String style = "";
                        if (!hasContent(change.getOldAndNew().getFirst().toString())) {
                            style = "-fx-background-color: rgba(0,180,0,0.7);";
                        } else if (!hasContent(change.getOldAndNew().getSecond().toString())) {
                            style = "-fx-background-color: rgba(180,0,0,0.7);";
                        } else {
                            style = "-fx-background-color: #e0e0e0;";
                        }
                        setStyle(style);
                    } catch (RuntimeException e) {
                        logger.error("Error updating row style: " , e);
                    }
                }
            }
        });

    }
    private static boolean hasContent(String str) {
        return !str.trim().isEmpty();
    }
    public void showChangesAction(ActionEvent actionEvent) {

        List<Change> changeList = FileUtils.deserializeList().reversed();

        ObservableList observableItemsList=
                FXCollections.observableArrayList(changeList);

        changesTableView.setItems(observableItemsList);


    }
}


