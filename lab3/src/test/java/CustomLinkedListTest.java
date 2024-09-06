import io.github.dziodzi.CustomLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CustomLinkedListTest {

    private CustomLinkedList<Integer> list;

    @BeforeEach
    public void setUp() {
        list = new CustomLinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
    }

    @Test
    public void testAddAndGet() {
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    public void testRemove() {
        list.remove(0);
        assertEquals(2, list.size());
        assertFalse(list.contains(1));
        assertEquals(2, list.get(0));
        assertEquals(3, list.get(1));
    }

    @Test
    public void testContains() {
        assertFalse(list.contains(0));
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertTrue(list.contains(3));
        assertFalse(list.contains(4));
    }

    @Test
    public void testAddAll() {
        List<Integer> newList = Arrays.asList(4, 5);
        list.addAll(newList);
        assertEquals(5, list.size());
        assertTrue(list.contains(4));
        assertTrue(list.contains(5));
    }

    @Test
    public void testStreamToCustomLinkedList() {
        Stream<Integer> stream = Stream.of(10, 20, 30, 40, 50);
        CustomLinkedList<Integer> listFromStream = stream.reduce(
                new CustomLinkedList<>(),
                (customList, element) -> {
                    customList.add(element);
                    return customList;
                },
                (list1, list2) -> {
                    throw new UnsupportedOperationException("Combiner not supported");
                }
        );
        assertEquals(5, listFromStream.size());
        assertTrue(listFromStream.contains(10));
        assertTrue(listFromStream.contains(20));
        assertTrue(listFromStream.contains(30));
        assertTrue(listFromStream.contains(40));
        assertTrue(listFromStream.contains(50));
    }
}