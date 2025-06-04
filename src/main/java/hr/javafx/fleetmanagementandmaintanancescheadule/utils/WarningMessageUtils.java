package hr.javafx.fleetmanagementandmaintanancescheadule.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.util.Optional;

/**
 * Utility class for displaying warning messages, confirmation dialogs, and alerts in a JavaFX application.
 */
public class WarningMessageUtils {
    /**
     * Displays a confirmation dialog with OK and Cancel buttons.
     *
     * @param title   The title of the dialog.
     * @param header  The header text of the dialog.
     * @param content The content message of the dialog.
     * @return {@code true} if the user clicked OK, {@code false} otherwise.
     */
    public static boolean confirmationDialog(String title, String header, String content) {
        Dialog<String> customDialog = new Dialog<>();
        customDialog.setTitle(title);

        DialogPane dialogPane = customDialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setHeaderText(header);
        dialogPane.setContentText(content);

        customDialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return "OK"; // Returning a value indicating OK button is pressed
            }
            return null; // Null means the button press was not handled
        });

        Optional<String> result = customDialog.showAndWait();

        // Update returnValue based on the result
        return result.isPresent() && result.get().equals("OK");
    }
    /**
     * Displays a confirmation dialog with OK and Cancel buttons.
     *
     * @param title   The title of the dialog.
     * @param header  The header text of the dialog.
     * @param content The content message of the dialog.
     * @return {@code true} if the user clicked OK, {@code false} otherwise.
     */
    public static void changeAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }
    /**
     * Displays a Warning dialog with OK and Cancel buttons.
     *
     */
    public static void wrongDateFormatAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Wrong date format or date older than 100 years!\n make sure to use dd/MM/yyyy");
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }
    /**
     * Displays a Warning dialog with OK and Cancel buttons.
     *
     */    public static boolean showWarningDialog(String title, String warningMessage) {
        Dialog<String> customDialog = new Dialog<>();
        customDialog.setTitle(title);

        DialogPane dialogPane = customDialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialogPane.setContentText(warningMessage);

        customDialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return "OK"; // User confirmed
            }
            return null; // User canceled
        });

        Optional<String> result = customDialog.showAndWait();
        return result.isPresent() && result.get().equals("OK");
    }

}
