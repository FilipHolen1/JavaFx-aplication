package hr.javafx.fleetmanagementandmaintanancescheadule.model;

import java.io.Serializable;
/**
 * Represents a van entity that can be maintained and serialized.
 * This class provides the basic attributes and behavior for different types of vans.
 */
public class Box<T> implements Serializable {
    private T content;

    /**
     * Constructs a new {@code Box} with the specified content.
     *
     * @param content The object to be stored in the box.
     */
    public Box(T content) {
        this.content = content;
    }
    /**
     * Retrieves the content stored in the box.
     *
     * @return The stored object.
     */
    public T getContent() {
        return content;
    }
    /**
     * Updates the content of the box with a new object.
     *
     * @param content The new object to store in the box.
     */
    public void setContent(T content) {
        this.content = content;
    }
}
