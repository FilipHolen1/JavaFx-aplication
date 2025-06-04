package hr.javafx.fleetmanagementandmaintanancescheadule.exceptions;
/**
 * Exception thrown when a username does not exist in the system.
 */
public class UsernameNonExistentException extends Exception {
    /**
     * Constructs a new exception without a message or cause.
     */
    public UsernameNonExistentException() {
    }
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message The detail message.
     */

    public UsernameNonExistentException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public UsernameNonExistentException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause The cause of the exception.
     */
    public UsernameNonExistentException(Throwable cause) {
        super(cause);
    }
    /**
     * Constructs a new exception with the specified detail message, cause,
     * suppression enabled or disabled, and writable stack trace enabled or disabled.
     *
     * @param message            The detail message.
     * @param cause              The cause of the exception.
     * @param enableSuppression  Whether suppression is enabled or disabled.
     * @param writableStackTrace Whether the stack trace should be writable.
     */
    public UsernameNonExistentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
