package src.ecs.frame;

import java.util.ArrayList;

/**
 * Centralises the broadcasting of all types of events.
 * Expects {@link ecs.frame.Event}.getType() to be consistent with the order of
 * iteration of the BroadcasterFactory passed into this class's constructor,
 * that is events of type id 0 should correspond to the first broadcaster
 * returned by the factory, and in general the nth event type should correspond to the
 * (n+1)th broadcaster returned by the factory.
 */
public class EventManager {

    private ArrayList<EventBroadcaster> broadcasters = new ArrayList<EventBroadcaster>();

    public EventManager(BroadcasterFactory bf) {
        while (bf.hasNext()) {
            broadcasters.add(bf.next());
        }
    }

    public <T> void broadcast(Event<T> event) {
        EventBroadcaster b = broadcasters.get(event.getType());
        b.broadcast(event);
    }
    
}
