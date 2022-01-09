package src.ecs.frame;

public interface EventListener<E> {
    public void notify(E e);
}
