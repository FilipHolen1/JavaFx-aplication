package hr.javafx.fleetmanagementandmaintanancescheadule.exceptions;
/**
 * Exception thrown when a content to long.
 */
public class InvalidContentLength extends RuntimeException{
    /**
     * Constructs a new exception without a message or cause.
     */
    public InvalidContentLength() {
    }
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message The detail message.
     */
    public InvalidContentLength(String message) {
        super(message);
    }
    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public InvalidContentLength(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause The cause of the exception.
     */
    public InvalidContentLength(Throwable cause) {
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
    public InvalidContentLength(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
