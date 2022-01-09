package src.ecs.frame;

import java.util.function.IntConsumer;

/**
 * This class provides a way of iterating right-to-left over
 * bitmask elements for a general bitmask of the length specified
 * by {@link ecs.frame.Masker}.
 */
public class MaskIterator {
    
    private int bitmask;
    private int index = 0;

    public MaskIterator(int mask) {
        bitmask = mask;
    }

    /**
     * Returns whether the bitmask still has another element
     * after the current one.
     */
    public boolean hasNext() {
        return index + 1 < Masker.MASK_LENGTH;
    }

    /**
     * Points to the next bitmask index to the left
     */
    public void loadNext() {
        index++;
    }

    /**
     * Whether the value at the current bitmask index (counting from the right)
     * is true (represented by a 1)
     */
    public boolean isTrue() {
        return Masker.isTrue(bitmask, index);
    }

    /**
     * Applies the passed IntConsumer to the current bitmask
     * index if the current bitmask index has a true value (that is equals 1)
     */
    public void doIfTrue(IntConsumer con) {
        if (isTrue()) {
            con.accept(index);
        }
    }

}
