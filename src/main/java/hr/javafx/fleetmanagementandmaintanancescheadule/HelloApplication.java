package hr.javafx.fleetmanagementandmaintanancescheadule;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * main
 */
public class HelloApplication extends Application {

    public static Stage mainStage;
    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
        stage.setTitle("Fleet Management");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
           }
    public static Stage getMainStage(){
        return mainStage;
    }
}