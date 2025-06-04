package hr.javafx.fleetmanagementandmaintanancescheadule.model;

import java.io.Serializable;
/**
 * A generic class that represents a pair of two objects of different types.
 *
 * @param <T1> The type of the first object in the pair.
 * @param <T2> The type of the second object in the pair.
 */
public class PairBox<T1, T2> implements Serializable {
    private T1 first;
    private T2 second;
    /**
     * Constructs a new {@code PairBox} with the specified values.
     *
     * @param first  The first object in the pair.
     * @param second The second object in the pair.
     */
    public PairBox(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
    /**
     * Gets the first object in the pair.
     *
     * @return The first object.
     */
    public T1 getFirst() {
        return first;
    }
/**
 * Sets the first object in the pair.
 *
 * @param first The first object to set
 */
    public void setFirst(T1 first) {
        this.first = first;
    }
    /**
     * Gets the first object in the pair.
     *
     * @return The second object.
     */
    public T2 getSecond() {
        return second;
    }
    /**
     * Sets the first object in the pair.
     *
     * @param second The first object to set
     */
    public void setSecond(T2 second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "PairBox{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
