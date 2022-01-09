package src.ecs.frame;

import java.util.ArrayList;

public class EventBroadcaster<T> {
    
    private ArrayList<EventListener<T>> listeners = new ArrayList<EventListener<T>>();

    public void addListener(EventListener<T> l) {
        listeners.add(l);
    }

    public void removeListener(EventListener<T> l) {
        listeners.remove(l);
    }

    public void broadcast(T event) {
        for (EventListener<T> listener : listeners) {
            listener.notify(event);
        }
    }

}
