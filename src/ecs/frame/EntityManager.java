package src.ecs.frame;

import java.util.function.IntConsumer;

/**
 * This class encapsulates the lifecycle of entities
 * within the ecs.frame architecture.
 * It requires a maximum number of entities to be stipulated,
 * and provided centralised control for creating and destroying
 * entities, as well as modifying their composition of components.
 */
public class EntityManager {
    //CHECKME - should we add logic for providing component reading?
    
    private ComponentManager comMan;

    private int[] entities;
    private int maxEntities;
    private static final int EMPTY_CELL = -1;
    private int entityCount = 0;
    private int currentIndex = -1;

    /**
     * 
     * @param max - the maximum number of entities permitted to be alive at a time
     */
    public EntityManager(ComponentManager cm, int max) {
        comMan = cm;
        maxEntities = max;
        entities = new int[maxEntities];
        for (int i = 0; i < maxEntities; i++) {
            entities[i] = -1;
        }
    }

    /**
     * 
     * @param componentMask - a bitmask with 1s reflecting components the entity should have
     * @return the id associated with the created entity
     * @throws IndexOutOfBoundsException thrown if the entity cap has already been hit, thus no valid id exists.
     */
    public int createEntity(int componentMask) throws IndexOutOfBoundsException {
        if (entityCount < maxEntities) {
            entityCount++;
            
            currentIndex++;

            int indexedEntity = entities[currentIndex];
            while (indexedEntity != EMPTY_CELL) {
                currentIndex = (currentIndex + 1) % maxEntities;
                indexedEntity = entities[currentIndex];
            }

            entities[currentIndex] = componentMask;
            createComponents(currentIndex, componentMask);
            return currentIndex;
        } else {
            throw new IndexOutOfBoundsException("Entity cap hit: no more permitted.");
        }
    }

    /**
     * Can be called to add a single component to an entity.
     * @param entityId the id of the entity to modify
     * @param storeIndex the index/id of the store to produce a component
     */
    public void addComponent(int entityId, int storeIndex) {
        int mask = entities[entityId];
        mask = mask | (1 << storeIndex);
        entities[entityId] = mask;
        comMan.addComponent(storeIndex, entityId);
    }

    /**
     * Can be called to remove a single component from an entity
     * @param entityId the id of the entity to modify
     * @param storeIndex the index/id of the store to remove a component from
     */
    public void removeComponent(int entityId, int storeIndex) {
        int mask = entities[entityId];
        mask = mask & ~(1 << storeIndex);
        entities[entityId] = mask;
        comMan.removeComponent(storeIndex, entityId);
    }

    /**
     * Can be called to destroy and create several entity components at once.
     * This method is not yet guaranteed to be any faster than
     * using individual addComponent() or removeComponent() calls.
     * CHECKME - to confirm.
     * @param id the id of the entity to edit
     * @param newMask - the new component bitmask to apply to the entity
     */
    public void editEntity(int id, int newMask) {
        int oldMask = entities[id];
        int additions = ~oldMask & newMask;
        int removals = ~newMask & oldMask;
        destroyComponents(id, removals);
        createComponents(id, additions);
    }

    /**
     * called to dereference an entity and all its components
     * from this class.
     * @param id the id of the entity to remove.
     */
    public void destroyEntity(int id) {
        destroyComponents(id, entities[id]);
        entities[id] = EMPTY_CELL;
        entityCount--;
    }

    private void createComponents(int entityIndex, int componentMask) {
        MaskIterator mit = new MaskIterator(componentMask);

        IntConsumer creator =
        (index) -> {
            comMan.addComponent(index, entityIndex);
        };

        while (mit.hasNext()) {
            mit.loadNext();
            mit.doIfTrue(creator);
        }
    }

    private void destroyComponents(int entityIndex, int componentMask) {
        MaskIterator mit = new MaskIterator(componentMask);

        IntConsumer destroyer =
        (index) -> {
            comMan.removeComponent(index, entityIndex);
        };

        while (mit.hasNext()) {
            mit.loadNext();
            mit.doIfTrue(destroyer);
        }
    }

    /**
     * @param id the id of the entity to check
     * @return whether or not a live entity is associated with the passed id
     */
    public boolean hasEntity(int id) {
        if (id >= 0 && id < maxEntities) {
            if (entities[id] != EMPTY_CELL) {
                return true;
            }
        }
        return false;
    }

}
