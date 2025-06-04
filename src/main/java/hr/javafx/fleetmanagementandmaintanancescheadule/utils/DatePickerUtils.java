package hr.javafx.fleetmanagementandmaintanancescheadule.utils;

import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static hr.javafx.fleetmanagementandmaintanancescheadule.constants.Constants.DATE_FORMAT;

/**
 * Utility class for date.
 */
public class DatePickerUtils extends LocalDateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DatePickerUtils.class);
    /**
     * Date formater
     */
    public static void setupDatePickerFormat(DatePicker datePicker) {
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);


            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try{
                        return LocalDate.parse(string, dateFormatter);
                    }catch (DateTimeParseException e){
                        logger.error(e.getMessage(),e);
                        return null;
                    }
                } else {
                    return null;
                }
            }
        };


        datePicker.setConverter(converter);
    }
}
