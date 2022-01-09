package src.ecs.frame;

/**
 * Used as a container to pass EventBroadcaster instances
 * to an eventManager. The order of iteration should be consistent
 * with {@link ecs.frame.Event}.getType() for any
 * returned EventBroadcasters with types matching the event type.
 */
public interface BroadcasterFactory {

    public boolean hasNext();
    public EventBroadcaster next();
    
}
