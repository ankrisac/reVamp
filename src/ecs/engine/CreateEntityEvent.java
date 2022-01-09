package src.ecs.engine;


import src.ecs.frame.Event;

public class CreateEntityEvent implements Event<Integer> {

    private int type;
    private int mask;

    public CreateEntityEvent(int type, int mask) {
        this.type = type;
        this.mask = mask;
    }

    @Override
    public Integer getData() {
        return mask;
    }

    @Override
    public int getType() {
        return type;
    }
    


}
