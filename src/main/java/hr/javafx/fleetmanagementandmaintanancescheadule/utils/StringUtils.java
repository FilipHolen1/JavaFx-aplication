package hr.javafx.fleetmanagementandmaintanancescheadule.utils;

import hr.javafx.fleetmanagementandmaintanancescheadule.exceptions.InvalidContentLength;
import javafx.scene.control.Alert;
/**
 * Utility class for string manipulation and validation.
 */
public final class StringUtils {

    private StringUtils() {}
    /**
     * Capitalizes the first letter of the given string.
     * Converts the rest of the string to lowercase.
     *
     * @param input The string to be capitalized.
     * @return The input string with the first letter capitalized, or {@code null} if input is {@code null}.
     */
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String newString = input.toLowerCase();
        return newString.substring(0, 1).toUpperCase() + newString.substring(1);
    }
    /**
     * Check Sting length
     */
    public static void checkStringLength(String description) {
        if(description.length()>99){
            WarningMessageUtils.changeAlert("Error!","Description must be less than 100 charachters!","", Alert.AlertType.WARNING);
            throw  new InvalidContentLength();
        }
    }}

