package model.test;

import model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Event class
 */
public class EventTest {
    private Event e;
    private Date d;

    @BeforeEach
    public void runBefore() {
        e = new Event("Add a new cosmetic product.");
        d = Calendar.getInstance().getTime();
    }

    @Test
    public void testEvent() {
        assertEquals("Add a new cosmetic product.", e.getDescription());
        assertFalse(d.equals(e.getDate()));
    }

    @Test
    public void testToString() {
        assertEquals(d.toString() + "\n" + "Sensor open at door", e.toString());
    }

    @Test
    public void testEqualsAndHashCodeAndNotEqual() {
        Event event1 = new Event("Event 1");
        Event event2 = new Event("Event 1");
        Event event3 = new Event("Event 2");

        assertEquals(event1.hashCode(), event2.hashCode());

        assertEquals(event1.getDate(),event2.getDate());
        assertEquals(event1.getDescription(),event2.getDescription());

        assertFalse(event1.equals(event3));
        assertFalse(event1.equals(null));

    }

    @Test
    public void notSameClass() {
        Event event = new Event("Sample Event");
        Object differentObject = new Object();

        assertFalse(event.equals(differentObject));
    }

}
