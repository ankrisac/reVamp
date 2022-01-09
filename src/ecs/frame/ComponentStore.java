package src.ecs.frame;

import java.util.HashMap;

/**
 * Manages the creation, destruction, storage and access of a given
 * type of entity component within the ecs.frame architecture.
 */
public abstract class ComponentStore<T extends Component> {
    
    private HashMap<Integer, T> coms = new HashMap<Integer, T>();

    /**
     * Associates a new component with the passed entity id
     */
    public void addEntity(int id) {
        coms.put(id, createComponent());
    }

    /**
     * Associates the passed component with the passed entity id.
     */
    public void addEntity(int id, T t) {
        coms.put(id, t);
    }

    /**
     * Removes and returns the component associated with the passed entity.
     */
    public T removeEntity(int id) {
        return coms.remove(id);
    }

    /**
     * Returns the component associated (if it exists) with the passed entity id
     */
    public T getComponent(int id) {
        if (id >= 0 && id < coms.size()) {
            return coms.get(id);
        } else {
            throw new IndexOutOfBoundsException("No component associated with entity " + id);
        }
    }

    /**
     * Concrete subclasses must override to provide creation logic
     * for new components. This method is used internally by
     * ComponentStore and is also suitable for external use.
     * @return a new instance of a concrete component
     */
    public abstract T createComponent();
    
}
