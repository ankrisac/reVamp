package src.SGFX;

public interface Bindable {
    public void bind();

    public void unbind();

    public static void bind(Bindable... objs) {
        for(Bindable b: objs) {
            b.bind();
        }
    }
    public static void unbind(Bindable... objs) {
        for(Bindable b: objs) {
            b.unbind();
        }
    }
}
