package parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import commands.ByeCommand;
import commands.Command;
import commands.DeadlineCommand;
import commands.DeleteCommand;
import commands.EventCommand;
import commands.ListCommand;
import commands.MarkCommand;
import commands.TodoCommand;
import commands.UnmarkCommand;
import errors.InvalidCommandFormatException;
import errors.LogosException;
import errors.UnknownCommandException;

public class ParserTest {

    private Parser parser;
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    @Test
    @DisplayName("parse: BYE -> ByeCommand")
    void parse_bye_returnsByeCommand() throws LogosException {
        Command cmd = parser.parse("bye");
        assertTrue(cmd instanceof ByeCommand);
    }

    @Test
    @DisplayName("parse: LIST -> ListCommand")
    void parse_list_returnsListCommand() throws LogosException {
        Command cmd = parser.parse("list");
        assertTrue(cmd instanceof ListCommand);
    }

    @Nested
    @DisplayName("MARK")
    class MarkTests {
        @Test
        @DisplayName("parse: MARK <n> -> MarkCommand")
        void parse_mark_withNumber() throws LogosException {
            Command cmd = parser.parse("mark 3");
            assertTrue(cmd instanceof MarkCommand);
        }

        @Test
        @DisplayName("parse: MARK without number -> InvalidCommandFormatException")
        void parse_mark_withoutNumber() {
            assertThrows(InvalidCommandFormatException.class, () -> parser.parse("mark"));
        }

        @Test
        @DisplayName("parse: MARK with non-numeric -> InvalidCommandFormatException")
        void parse_mark_withNonNumeric() {
            assertThrows(InvalidCommandFormatException.class, () -> parser.parse("mark abc"));
        }
    }

    @Nested
    @DisplayName("UNMARK")
    class UnmarkTests {
        @Test
        @DisplayName("parse: UNMARK <n> -> UnmarkCommand")
        void parse_unmark_withNumber() throws LogosException {
            Command cmd = parser.parse("unmark 1");
            assertTrue(cmd instanceof UnmarkCommand);
        }

        @Test
        @DisplayName("parse: UNMARK without number -> InvalidCommandFormatException")
        void parse_unmark_withoutNumber() {
            assertThrows(InvalidCommandFormatException.class, () -> parser.parse("unmark"));
        }

        @Test
        @DisplayName("parse: UNMARK with non-numeric -> InvalidCommandFormatException")
        void parse_unmark_withNonNumeric() {
            assertThrows(InvalidCommandFormatException.class, () -> parser.parse("unmark zero"));
        }
    }

    @Nested
    @DisplayName("TODO")
    class TodoTests {
        @Test
        @DisplayName("parse: TODO with description -> TodoCommand")
        void parse_todo_withDesc() throws LogosException {
            Command cmd = parser.parse("todo buy milk");
            assertTrue(cmd instanceof TodoCommand);
        }

        @Test
        @DisplayName("parse: TODO without description still creates TodoCommand (parser defers validation)")
        void parse_todo_withoutDesc() throws LogosException {
            Command cmd = parser.parse("todo");
            assertTrue(cmd instanceof TodoCommand);
        }
    }

    @Nested
    @DisplayName("DEADLINE")
    class DeadlineTests {
        @Test
        @DisplayName("parse: DEADLINE with valid '/by' and date -> DeadlineCommand")
        void parse_deadline_valid() throws LogosException {
            String when = LocalDateTime.of(2024, 1, 2, 18, 0).format(INPUT_FORMAT);
            Command cmd = parser.parse("deadline finish report /by " + when);
            assertTrue(cmd instanceof DeadlineCommand);
        }

        @Test
        @DisplayName("parse: DEADLINE missing '/by' -> InvalidCommandFormatException")
        void parse_deadline_missingBy() {
            assertThrows(InvalidCommandFormatException.class,
                    () -> parser.parse("deadline finish report by tomorrow"));
        }

        @Test
        @DisplayName("parse: DEADLINE empty description -> InvalidCommandFormatException")
        void parse_deadline_emptyDesc() {
            String when = LocalDateTime.now().format(INPUT_FORMAT);
            assertThrows(InvalidCommandFormatException.class,
                    () -> parser.parse("deadline   /by " + when));
        }

        @Test
        @DisplayName("parse: DEADLINE invalid date format -> InvalidCommandFormatException")
        void parse_deadline_badDate() {
            assertThrows(InvalidCommandFormatException.class,
                    () -> parser.parse("deadline finish /by 2024/01/02 1800"));
        }
    }

    @Nested
    @DisplayName("EVENT")
    class EventTests {
        @Test
        @DisplayName("parse: EVENT with valid '/from' and '/to' -> EventCommand")
        void parse_event_valid() throws LogosException {
            String from = LocalDateTime.of(2024, 5, 10, 9, 0).format(INPUT_FORMAT);
            String to = LocalDateTime.of(2024, 5, 10, 10, 0).format(INPUT_FORMAT);
            Command cmd = parser.parse("event standup /from " + from + " /to " + to);
            assertTrue(cmd instanceof EventCommand);
        }

        @Test
        @DisplayName("parse: EVENT missing markers -> InvalidCommandFormatException")
        void parse_event_missingMarkers() {
            assertThrows(InvalidCommandFormatException.class,
                    () -> parser.parse("event meeting from 2024-05-10 0900 to 2024-05-10 1000"));
        }

        @Test
        @DisplayName("parse: EVENT '/to' before '/from' -> InvalidCommandFormatException")
        void parse_event_toBeforeFrom() {
            String from = "2024-05-10 0900";
            String to = "2024-05-10 1000";
            assertThrows(InvalidCommandFormatException.class,
                    () -> parser.parse("event bad /to " + to + " /from " + from));
        }

        @Test
        @DisplayName("parse: EVENT empty description -> InvalidCommandFormatException")
        void parse_event_emptyDesc() {
            String from = "2024-05-10 0900";
            String to = "2024-05-10 1000";
            assertThrows(InvalidCommandFormatException.class,
                    () -> parser.parse("event   /from " + from + " /to " + to));
        }

        @Test
        @DisplayName("parse: EVENT invalid date format -> InvalidCommandFormatException")
        void parse_event_badDate() {
            assertThrows(InvalidCommandFormatException.class,
                    () -> parser.parse("event meetup /from 2024/05/10 0900 /to 2024-05-10 1000"));
        }
    }

    @Nested
    @DisplayName("DELETE")
    class DeleteTests {
        @Test
        @DisplayName("parse: DELETE <n> -> DeleteCommand")
        void parse_delete_withNumber() throws LogosException {
            Command cmd = parser.parse("delete 2");
            assertTrue(cmd instanceof DeleteCommand);
        }

        @Test
        @DisplayName("parse: DELETE without number -> InvalidCommandFormatException")
        void parse_delete_withoutNumber() {
            assertThrows(InvalidCommandFormatException.class, () -> parser.parse("delete"));
        }

        @Test
        @DisplayName("parse: DELETE with non-numeric -> InvalidCommandFormatException")
        void parse_delete_withNonNumeric() {
            assertThrows(InvalidCommandFormatException.class, () -> parser.parse("delete two"));
        }
    }

    @Test
    @DisplayName("parse: unknown command -> UnknownCommandException")
    void parse_unknownCommand_throwsUnknown() {
        assertThrows(UnknownCommandException.class, () -> parser.parse("dance 1"));
    }
}
