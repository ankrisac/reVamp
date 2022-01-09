package src.ecs.frame;

/**
 * Used as a container to pass ComponentStore instances
 * to a componentManager. The order of iteration corresponds
 * to {@link ecs.frame.ComponentStore} storeIndices passed
 * into methods.
 */
public interface StoreFactory {
    
    public boolean hasNext();
    public ComponentStore next();

}
