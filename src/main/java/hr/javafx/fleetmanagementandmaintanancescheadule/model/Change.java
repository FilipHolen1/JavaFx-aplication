package hr.javafx.fleetmanagementandmaintanancescheadule.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Class represents a serializable change that is saved into a file
 *
 */
public class Change implements Serializable {

    //private static final long serialVersionUID = 3345810030467029264L;
    private static final long serialVersionUID = 3345810030467029264L;

    PairBox<?,?> oldAndNew;
    Box<String> user;
    LocalDateTime localDateTime;
    /**
     * Constructs a new {@code Change} instance with the given old and new values,
     * the user who made the change, and the timestamp of the change.
     *
     * @param oldAndNew     A {@link PairBox} containing the old and new values of the change.
     * @param user          A {@link Box} containing the username of the user who made the change.
     * @param localDateTime The timestamp when the change occurred.
     */
    public Change(PairBox<?, ?> oldAndNew, Box<String> user, LocalDateTime localDateTime) {
        this.oldAndNew = oldAndNew;
        this.user = user;
        this.localDateTime = localDateTime;
    }
    /**
     * Gets the pair of old and new values representing the change.
     *
     * @return A {@link PairBox} containing the old and new values.
     */
    public PairBox<?, ?> getOldAndNew() {
        return oldAndNew;
    }
    /**
     * Gets the user who made the change.
     *
     * @return A {@link Box} containing the username of the user.
     */
    public Box<String> getUser() {
        return user;
    }
    /**
     * Gets the timestamp when the change occurred.
     *
     * @return A {@link LocalDateTime} representing the change time.
     */
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
    /**
     * Writes the object to an {@link ObjectOutputStream} for serialization.
     *
     * @param out The output stream to write the object.
     * @throws IOException If an I/O error occurs during writing.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    /**
     * Reads the object from an {@link ObjectInputStream} for deserialization.
     *
     * @param in The input stream to read the object from.
     * @throws IOException            If an I/O error occurs during reading.
     * @throws ClassNotFoundException If the class cannot be found during deserialization.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
