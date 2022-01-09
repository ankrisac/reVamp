package src.ecs.frame;

/**
 * Provides utility methods for manipulating bitmasks of the
 * length as stipulated by the constant MASK_LENGTH.
 * This value is not expected to change within the scope of this
 * framework's operation, but if change is required, it should be
 * done by modifying this class.
 */
public class Masker {


    public static final int MASK_LENGTH = 32;
    
    /**
     * Returns whether the value of the bitmask at the current
     * index counting from the right is 1 (representing true).
     */
    public static boolean isTrue(int mask, int index) {
        return ((mask >>> index) & 1) == 1;
    }

}
