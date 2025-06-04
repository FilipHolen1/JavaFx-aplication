package hr.javafx.fleetmanagementandmaintanancescheadule.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
/**
 * Driver assigment class
 *
 */
public class DriverAssignment {
    private static final Set<DriverAssignment> assignments = new HashSet<>();
    private String vin;
    private String licenseNumber;
    private LocalDate assignmentDate;
    public static Set<DriverAssignment> getAssignments() {
        return assignments;
    }

    public DriverAssignment(String vin, String licenseNumber, LocalDate assignmentDate) {
        if (!isVehicleAvailable(vin, assignmentDate)) {
            throw new IllegalArgumentException("Vehicle " + vin + " is already assigned on " + assignmentDate);
        }
        this.vin = vin;
        this.licenseNumber = licenseNumber;
        this.assignmentDate = assignmentDate;
        //assignments.add(this); // Dodajemo samo ako je sve prošlo
    }

    public static boolean isVehicleAvailable(String vin, LocalDate date) {
        return assignments.stream()
                .noneMatch(a -> a.vin.equals(vin) && a.assignmentDate.equals(date));
    }
    public String getVin() {
        return vin;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    @Override
    public String toString() {
        return "DriverAssignment{" +
                "vin='" + vin + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", assignmentDate=" + assignmentDate +
                '}';
    }
}
