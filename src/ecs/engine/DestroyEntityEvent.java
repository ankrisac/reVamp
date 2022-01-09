package src.ecs.engine;

import src.ecs.frame.Event;

public class DestroyEntityEvent implements Event<Integer> {

    private int type;
    private int entityId;

    public DestroyEntityEvent(int type, int id) {
        this.type = type;
        entityId = id;
    }

    @Override
    public Integer getData() {
        return entityId;
    }

    @Override
    public int getType() {
        return type;
    }
    


}
