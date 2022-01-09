package src.ecs.engine;


import src.ecs.frame.Event;

public class RemoveComponentEvent implements Event<Integer> {

    private int type;
    private int storeIndex;

    public RemoveComponentEvent(int type, int storeIndex) {
        this.type = type;
        this.storeIndex = storeIndex;
    }

    @Override
    public Integer getData() {
        return storeIndex;
    }

    @Override
    public int getType() {
        return type;
    }
    


}
