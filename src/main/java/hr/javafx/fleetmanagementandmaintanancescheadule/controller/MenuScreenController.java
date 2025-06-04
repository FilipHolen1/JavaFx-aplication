package hr.javafx.fleetmanagementandmaintanancescheadule.controller;

import hr.javafx.fleetmanagementandmaintanancescheadule.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
/**
 * Controller class for handling menu navigation in the Fleet Management and Maintenance Schedule application.
 * This class provides methods for opening different screens such as vehicles, drivers, maintenance schedules, and changes.
 */

public class MenuScreenController {
    private static final Logger logger = LoggerFactory.getLogger(MenuScreenController.class);


   public void showScreenVehicles(){
           FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("vehiclesScreen.fxml"));
           try {
               Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
               HelloApplication.getMainStage().setTitle("Vehicles");
               HelloApplication.getMainStage().setScene(scene);
               HelloApplication.getMainStage().show();
           } catch (RuntimeException | IOException e) {
               logger.error("Error opening screen!!", e);
           }
   }

    public void showScreenAddVehicle(){
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addVehicleScreen.fxml"));
            try {
                Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
                HelloApplication.getMainStage().setTitle("Add new vehicle");
                HelloApplication.getMainStage().setScene(scene);
                HelloApplication.getMainStage().show();
            } catch (RuntimeException | IOException e) {
                logger.error("Error opening screen!!!", e);
            }
        }

    public void showScreenAddDriver(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addDriverScreen.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
            HelloApplication.getMainStage().setTitle("Drivers");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (RuntimeException | IOException e) {
            logger.error("Error opening screen!!!!", e);
        }
    }

    public void showScreenDrivers(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("driversScreen.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
            HelloApplication.getMainStage().setTitle("Drivers");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (RuntimeException | IOException e) {
            logger.error("Error opening screen!!!!!", e);
        }
    }

    public void showScreeMaintenanceSchedule(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("maintenanceScheduleScreen.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
            HelloApplication.getMainStage().setTitle("Maintenance Schedule");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (RuntimeException | IOException e) {
            logger.error("Error opening scren", e);
        }
    }

    public void showScreeAddMaintenanceSchedule(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addMaintenanceScheduleScreen.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
            HelloApplication.getMainStage().setTitle("Add Maintenance Schedule");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (RuntimeException | IOException e) {
            logger.error("Error opening screen", e);
        }
    }

    public void showLoginScreen(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginScreen.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
            HelloApplication.getMainStage().setTitle("Login");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            String message = "Nemogu otvoriti ekran changes";
            logger.error(message, e);
        }
    }
    public void showChanges(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("showChangesScreen.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
            HelloApplication.getMainStage().setTitle("Changes");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (RuntimeException | IOException e) {
            logger.error("Error opening screen", e);
        }
    }
}
