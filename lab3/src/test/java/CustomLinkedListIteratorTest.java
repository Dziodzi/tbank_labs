import io.github.dziodzi.CustomIterator;
import io.github.dziodzi.CustomLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class CustomLinkedListIteratorTest {
    
    private CustomLinkedList<Integer> list;
    private CustomIterator<Integer> iter;
    
    @BeforeEach
    public void setUp() {
        list = new CustomLinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        iter = list.iterator();
    }
    
    @Test
    public void hasNextTest_shouldReturnTrue() {
        assertTrue(iter.hasNext(), "Iterator should have next element.");
    }
    
    @Test
    public void hasNextTest_shouldReturnFalse() {
        iter.next();
        iter.next();
        iter.next();
        assertFalse(iter.hasNext(), "Iterator should not have next element.");
    }
    
    @Test
    public void nextTest_shouldReturnElementsInCorrectOrder() {
        assertEquals(Integer.valueOf(1), iter.next());
        assertEquals(Integer.valueOf(2), iter.next());
        assertEquals(Integer.valueOf(3), iter.next());
    }
    
    @Test
    public void nextTest_shouldThrowNoSuchElementException() {
        iter.next();
        iter.next();
        iter.next();
        
        assertThrows(NoSuchElementException.class, () -> {
            iter.next();
        }, "Expected next() to throw, but it didn't.");
    }
    
    @Test
    public void forEachRemaining_shouldProcessRemainingElements() {
        final int[] count = {0};
        Consumer<Integer> action = (Integer element) -> count[0]++;
        
        iter.forEachRemaining(action);
        assertEquals(3, count[0], "forEachRemaining should process all remaining elements.");
    }
    
    @Test
    public void forEachRemaining_shouldNotProcessWhenNoRemainingElements() {
        iter.next();
        iter.next();
        iter.next();
        
        final int[] count = {0};
        Consumer<Integer> action = (Integer element) -> count[0]++;
        
        iter.forEachRemaining(action);
        assertEquals(0, count[0], "forEachRemaining should not process any elements when none are left.");
    }
    
    @Test
    public void forEachRemaining_shouldHandleEmptyList() {
        list = new CustomLinkedList<>();
        iter = list.iterator();
        
        final int[] count = {0};
        Consumer<Integer> action = (Integer element) -> count[0]++;
        
        iter.forEachRemaining(action);
        assertEquals(0, count[0], "forEachRemaining should not process any elements for an empty list.");
    }
}
