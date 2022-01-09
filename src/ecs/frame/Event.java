package src.ecs.frame;

/**
 * Represents an event carrying a payload of type T.
 * The getType() method of any implementing classes should
 * be consistent with the order of iteration used by any
 * corresponding {@link ecs.frame.BroadcasterFactory} instances
 * (that is whenever the factory is used by the same
 * {@link ecs.frame.EventManager} as implementing classes of this
 * interface.)
 */
public interface Event<T> {
    public T getData();
    public int getType();
}
