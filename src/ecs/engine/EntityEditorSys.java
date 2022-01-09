package src.ecs.engine;

import src.ecs.frame.*;

import java.util.ArrayList;

/**
 * This class manages entity edits via events.
 * This allows systems within an ECS to interact with
 * entity lifecycles without having additional dependencies
 * injected.
 * 
 * <p>
 * 
 * It is assumed that when any CreateEntityEvent, DestroyEntityEvent,
 * AddComponentEvent or RemoveComponentEvent is broadcast:
 * no entity is attempted to be destroyed or have components
 * added to or removed from unless it has already finished
 * being created, and thus is visible via the EntityManager instance.
 * However entities may have components added to or removed
 * from if it has been added to the list of entities to destroy.
 */
public class EntityEditorSys implements Sys {

    private ComponentManager comMan;
    private EventManager eveMan;

    private Creator creator;
    private Destroyer destroyer;
    private Adder adder;
    private Remover remover;

    private EntityManager entMan;
    private ArrayList<Integer> maskBirthQueue = new ArrayList<Integer>();
    private ArrayList<Integer> entIdDeathQueue = new ArrayList<Integer>();
    private ArrayList<Integer> comIdAddQueue = new ArrayList<Integer>();
    private ArrayList<Integer> comIdRemoveQueue = new ArrayList<Integer>();

    public EntityEditorSys(EntityManager em) {
        entMan = em;
        creator = new Creator();
        destroyer = new Destroyer();
        adder = new Adder();
        remover = new Remover();
    }

    @Override
    public void step(int millis) {
        while (!comIdAddQueue.isEmpty()) {
            int id = comIdAddQueue.get(0);
            comIdAddQueue.remove(0);
        }

        while (!comIdRemoveQueue.isEmpty()) {
            int id = comIdRemoveQueue.get(0);  
            comIdRemoveQueue.remove(0);
        }
        
        while (!entIdDeathQueue.isEmpty()) {
            int id = entIdDeathQueue.get(0);
            entMan.destroyEntity(id);
            entIdDeathQueue.remove(0);
        }
        
        while (!maskBirthQueue.isEmpty()) {
            int mask = maskBirthQueue.get(0);
            entMan.createEntity(mask);
            maskBirthQueue.remove(0);
        }
    }

    @Override
    public void setComponentManager(ComponentManager cm) {
        comMan = cm;
    }

    @Override
    public void setEventManager(EventManager em) {
        eveMan = em;
    }

    private class Creator implements EventListener<CreateEntityEvent> {
        @Override
        public void notify(CreateEntityEvent e) {
            maskBirthQueue.add(e.getData());
        }
    }

    private class Destroyer implements EventListener<DestroyEntityEvent> {
        @Override
        public void notify(DestroyEntityEvent e) {
            entIdDeathQueue.add(e.getData());
        }
    }

    private class Adder implements EventListener<AddComponentEvent> {
        @Override
        public void notify(AddComponentEvent e) {
            comIdAddQueue.add(e.getData());
        }
    }

    private class Remover implements EventListener<RemoveComponentEvent> {
        @Override
        public void notify(RemoveComponentEvent e) {
            comIdRemoveQueue.add(e.getData());
        }
    }
    
}
