package src.SGFX;

public interface Resource {
    public void destroy();

    public static void destroy(Resource... objs) {
        for(Resource r: objs) {
            r.destroy();
        }
    }
}

