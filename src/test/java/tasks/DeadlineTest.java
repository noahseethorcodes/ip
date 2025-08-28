package tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    @DisplayName("getDeadline returns formatted date in 'MMM dd yyyy, h:mma' format")
    void getDeadline_formatsCorrectly() {
        LocalDateTime dt = LocalDateTime.of(2025, 8, 28, 14, 30); // Aug 28 2025, 2:30PM
        Deadline d = new Deadline("submit report", dt);

        assertEquals("Aug 28 2025, 2:30pm", d.getDeadline());
    }

    @Test
    @DisplayName("getTaskType returns TaskType.DEADLINE")
    void getTaskType_returnsDeadlineEnum() {
        Deadline d = new Deadline("finish hw", LocalDateTime.now());
        assertEquals(TaskType.DEADLINE, d.getTaskType());
    }

    @Test
    @DisplayName("getTaskTypeIcon returns 'D'")
    void getTaskTypeIcon_returnsD() {
        Deadline d = new Deadline("finish hw", LocalDateTime.now());
        assertEquals("D", d.getTaskTypeIcon());
    }

    @Test
    @DisplayName("getAsListItem reflects undone status with correct formatting")
    void getAsListItem_undone() {
        LocalDateTime dt = LocalDateTime.of(2025, 12, 25, 9, 0);
        Deadline d = new Deadline("open presents", dt);

        String expected = String.format("[D] [ ] open presents (by: %s)",
                dt.format(DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma")));
        assertEquals(expected, d.getAsListItem());
    }

    @Test
    @DisplayName("getAsListItem reflects done status with X icon")
    void getAsListItem_done() {
        LocalDateTime dt = LocalDateTime.of(2025, 1, 1, 0, 0);
        Deadline d = new Deadline("party", dt);
        d.markAsDone();

        String expected = String.format("[D] [X] party (by: %s)",
                dt.format(DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma")));
        assertEquals(expected, d.getAsListItem());
    }

    @Test
    @DisplayName("toStorageLine serializes into 'D | <doneFlag> | <desc> | <ISO_LOCAL_DATE_TIME>'")
    void toStorageLine_serializesCorrectly() {
        LocalDateTime dt = LocalDateTime.of(2025, 3, 15, 10, 0);
        Deadline d = new Deadline("meet boss", dt);
        d.markAsDone();

        String expected = String.format("D | 1 | meet boss | %s", dt.toString());
        assertEquals(expected, d.toStorageLine());
    }

    @Test
    @DisplayName("fromStorageLine parses undone Deadline correctly")
    void fromStorageLine_parsesUndone() {
        LocalDateTime dt = LocalDateTime.of(2025, 7, 20, 18, 0);
        String line = String.format("D | 0 | grocery shopping | %s", dt);

        Deadline d = Deadline.fromStorageLine(line);
        assertNotNull(d);
        assertEquals("grocery shopping", d.getDescription());
        assertFalse(d.isDone());
        assertEquals("Jul 20 2025, 6:00pm", d.getDeadline());
    }

    @Test
    @DisplayName("fromStorageLine parses done Deadline correctly")
    void fromStorageLine_parsesDone() {
        LocalDateTime dt = LocalDateTime.of(2025, 11, 5, 23, 59);
        String line = String.format("D | 1 | fireworks | %s", dt);

        Deadline d = Deadline.fromStorageLine(line);
        assertNotNull(d);
        assertEquals("fireworks", d.getDescription());
        assertTrue(d.isDone());
    }

    @Test
    @DisplayName("fromStorageLine with invalid done flag returns null")
    void fromStorageLine_invalidDoneFlag() {
        String badLine = "D | notANumber | desc | 2025-08-28T14:30";
        Deadline d = Deadline.fromStorageLine(badLine);
        assertNull(d);
    }

    @Test
    @DisplayName("toStorageLine and fromStorageLine are consistent round-trip")
    void roundTrip_consistency() {
        LocalDateTime dt = LocalDateTime.of(2026, 1, 1, 12, 0);
        Deadline original = new Deadline("celebrate", dt);
        original.markAsDone();

        String line = original.toStorageLine();
        Deadline restored = Deadline.fromStorageLine(line);

        assertNotNull(restored);
        assertEquals(original.getDescription(), restored.getDescription());
        assertEquals(original.isDone(), restored.isDone());
        assertEquals(original.getDeadline(), restored.getDeadline());
    }
}
