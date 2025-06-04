package hr.javafx.fleetmanagementandmaintanancescheadule.utils;

import hr.javafx.fleetmanagementandmaintanancescheadule.exceptions.InvalidDateFormatException;


import java.time.LocalDate;
/**
 * Utility class for string LocalDate validation.
 */
public class LocalDateUtils {
    /**
     *Validate date
     * checks date by some rules
     */

    public static void validateDate(LocalDate date) throws InvalidDateFormatException {
        if (date == null) {
            throw new InvalidDateFormatException("Date cannot be null.");
        }

        LocalDate hundredYearsAgo = LocalDate.now().minusYears(100);

        if (date.isBefore(hundredYearsAgo)) {
            WarningMessageUtils.wrongDateFormatAlert();

            throw new InvalidDateFormatException("Date is older than 100 years.");
        }
    }
}
