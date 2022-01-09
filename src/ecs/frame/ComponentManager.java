package src.ecs.frame;

import java.util.ArrayList;

/**
 * Centralises the addition, removal and access of different component types. 
 * This class uses entity ids as keys, but doesn't manage
 * entity lifecycles. For lifecycles use {@link ecs.frame.EntityManager}.
 * This class behaves correctly only with consistent use of 
 * store indices. Namely, the store indices in access, creation and removal methods
 * should be consistent with the order of iteration of the
 * StoreFactory passed into this class's constructor.
 */
public class ComponentManager {
    
    private ArrayList<ComponentStore> stores = new ArrayList<ComponentStore>();

    /**
     * 
     * @param sf provides the stores for this class to delegate
     * the control of concrete components to. Order of iteration
     * is taken as the order of store indices.
     */
    public ComponentManager(StoreFactory sf) {
        while (sf.hasNext()) {
            stores.add(sf.next());
        }
    }

    /**
     *
     * @param <T> must match up with the type held by the store under store index
     * @param storeIndex
     * @return
     */
    public <T extends Component> ComponentStore<T> getStore(int storeIndex) {
        return stores.get(storeIndex);
    }

    /**
     * Returns the component belonging to the store at storeIndex
     * that is associated with the entity of id entityIndex.
     * This method expects the type parameter to align with the storeIndex
     * otherwise runtime exceptions are expected to occur.
     */
    public <T extends Component> T getComponent(int storeIndex, int entityIndex) {
        ComponentStore<T> store = getStore(storeIndex);
        return store.getComponent(entityIndex);
    }

    /**
     * Adds a component to an entity.
     * @param <T> the type of component to be added
     * @param storeIndex the store to add the component to
     * @param entityIndex the entity to associate the component with
     * @param t the component to add to the entity
     */
    public <T extends Component> void addComponent(int storeIndex, int entityIndex, T t) {
        ComponentStore<T> store = getStore(storeIndex);
        store.addEntity(entityIndex, t);
    }

    /**
     * Creates a new component then added to an entity
     * @param <T> the type of component to be created then added.
     * @param storeIndex the store to add the component to
     * @param entityIndex the entity to associate the component with.
     */
    public <T extends Component> void addComponent(int storeIndex, int entityIndex) {
        ComponentStore<T> store = getStore(storeIndex);
        store.addEntity(entityIndex, store.createComponent());
    }

    /**
     * Removes a component from an entity
     * @param <T> the type of component to remove
     * @param storeIndex the store to remove the component from
     * @param entityIndex the entity to disassociate the component from
     */
    public <T extends Component> void removeComponent(int storeIndex, int entityIndex) {
        ComponentStore<T> store = getStore(storeIndex);
        store.removeEntity(entityIndex);
    }

}
