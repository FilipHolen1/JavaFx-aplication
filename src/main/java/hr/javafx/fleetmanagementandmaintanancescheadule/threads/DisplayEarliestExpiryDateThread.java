package hr.javafx.fleetmanagementandmaintanancescheadule.threads;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.Vehicle;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.DatabaseUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
/**
 * A thread that retrieves the vehicle with the earliest registration expiry date and updates a JavaFX label.
 */
public class DisplayEarliestExpiryDateThread implements Runnable {

    private List<Vehicle> vehicles;
    private Label earliestExpiryDateLabel;

    /**
     * Constructs a new thread for displaying the earliest vehicle registration expiry date.
     *
     * @param earliestExpiryDateLabel The JavaFX {@link Label} to be updated with the earliest expiry date information.
     */
    public DisplayEarliestExpiryDateThread(Label earliestExpiryDateLabel) {
        this.vehicles = DatabaseUtils.getVehicles();
        this.earliestExpiryDateLabel = earliestExpiryDateLabel;
    }
    /**
     * Runs the thread to find and display the vehicle with the earliest registration expiry date.
     * The result is set on the provided JavaFX label using {@link Platform#runLater(Runnable)}.
     */

    @Override
    public void run() {
        Optional<Vehicle> earliestExpiryVehicle = vehicles.stream()
                .min(Comparator.comparing(Vehicle::getRegistrationExpiryDate));

        if (earliestExpiryVehicle.isPresent()) {
            Vehicle vehicle = earliestExpiryVehicle.get();
            String expiryMessage = "Najraniji istek registracije: " + vehicle.getManufacturer() + " " +
                    vehicle.getModel()+ " (" + vehicle.getYear() +")"+
                    " (" + vehicle.getRegistrationExpiryDate() + ")";

            Platform.runLater(() -> earliestExpiryDateLabel.setText(expiryMessage));
        }
    }
}
