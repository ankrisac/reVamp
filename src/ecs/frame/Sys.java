package src.ecs.frame;

import java.util.ArrayList;

/**
 * Defines the contract for ECS systems in this framework.
 */
public interface Sys {
    
    public void step(int millis);
    public void setComponentManager(ComponentManager cm);
    public void setEventManager(EventManager em);

}
