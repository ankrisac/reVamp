package src.ecs.engine;

import src.ecs.frame.ComponentManager;
import src.ecs.frame.EventManager;
import src.ecs.frame.Sys;

public class EntityKillerSys implements Sys  {

    private ComponentManager comMan;
    private EventManager eveMan;

    @Override
    public void step(int millis) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setComponentManager(ComponentManager cm) {
        comMan = cm;
        
    }

    @Override
    public void setEventManager(EventManager em) {
        eveMan = em;
    }
    
}
