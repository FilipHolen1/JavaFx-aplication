package hr.javafx.fleetmanagementandmaintanancescheadule.threads;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.Vehicle;
import hr.javafx.fleetmanagementandmaintanancescheadule.utils.databaseutils.DatabaseUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;
/**
 * A thread that handles the deletion of a vehicle from the database in a synchronized manner.
 */
public class DeleteVehicleThread implements Runnable {

    private final Vehicle vehicle;
    private final Label statusLabel;
    /**
     * Constructs a thread for deleting a vehicle from the database.
     *
     * @param vehicle     The vehicle to be deleted.
     * @param statusLabel The JavaFX {@link Label} to display the status message after deletion.
     */
    public DeleteVehicleThread(Vehicle vehicle, Label statusLabel) {
        this.vehicle = vehicle;
        this.statusLabel = statusLabel;
    }
    /**
     * Runs the thread to delete the specified vehicle from the database.
     * The method ensures that database access is synchronized to prevent conflicts with other threads.
     * A status message is displayed on the JavaFX label upon successful deletion.
     */

    @Override
    public void run() {
        synchronized (DatabaseUtils.class) {
            while (DatabaseUtils.isDatabaseAccessInProgress()) {
                try {
                    DatabaseUtils.class.wait(); // cekamo dok druga nit ne zavrsi pristup bazi
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            DatabaseUtils.setDatabaseAccessInProgress(true);

            try {
                //DatabaseUtils.addVehicle(vehicle);
                DatabaseUtils.deleteVehicleByVin(vehicle.getVin());
                Platform.runLater(() -> statusLabel.setText("Vozilo " + vehicle.getManufacturer() + " " + vehicle.getModel() + " je obrisano!"));
            } finally {
                DatabaseUtils.setDatabaseAccessInProgress(false);
                DatabaseUtils.class.notifyAll(); // obavjestavamo druge niti da je baza sada slobodna
            }
        }
    }
}
