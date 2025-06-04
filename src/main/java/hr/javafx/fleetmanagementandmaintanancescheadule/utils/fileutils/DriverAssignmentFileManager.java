package hr.javafx.fleetmanagementandmaintanancescheadule.utils.fileutils;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.DriverAssignment;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DriverAssignmentFileManager {
    private DriverAssignmentFileManager() {
        throw new IllegalStateException("Utility class");
    }

    private static final String FILE_PATH = "dat/driver_assignments.txt";

    // Spremanje u datoteku
    public static void saveAssignment(DriverAssignment assignment) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(assignment.getVin() + "," + assignment.getLicenseNumber() + "," + assignment.getAssignmentDate());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Učitavanje svih zapisa iz datoteke
    public static List<DriverAssignment> loadAssignments() {
        List<DriverAssignment> assignments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    assignments.add(new DriverAssignment(parts[0], parts[1], LocalDate.parse(parts[2])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return assignments;
    }
}
