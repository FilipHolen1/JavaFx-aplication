package hr.javafx.fleetmanagementandmaintanancescheadule.utils.fileutils;

import hr.javafx.fleetmanagementandmaintanancescheadule.model.Change;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Utility class for serialization.
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String CHANGES_SERIALIZARION_FILE_NAME = "dat/changesSerialized.txt";
    /**
     * method for serialization.
     */
    public static void serializeList(List<Change> changes) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(CHANGES_SERIALIZARION_FILE_NAME))) {
            outputStream.writeObject(changes);
            System.out.println("List successfully serialized.");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
    /**
     * method for deserialization.
     */
    public static List<Change> deserializeList() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(CHANGES_SERIALIZARION_FILE_NAME))) {
            Object object = inputStream.readObject();

            if (object instanceof List) {
                return (List<Change>) object;
            } else {
                System.err.println("Invalid format. Expected a List<Change> object.");
                logger.error("invalid format when deserializing list");
            }
        } catch (EOFException e) {
           logger.error(e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return null;
    }



}
