package parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import commands.ByeCommand;
import commands.Command;
import commands.CommandType;
import commands.DeadlineCommand;
import commands.DeleteCommand;
import commands.EventCommand;
import commands.FindCommand;
import commands.ListCommand;
import commands.MarkCommand;
import commands.TodoCommand;
import commands.UnmarkCommand;
import errors.InvalidCommandFormatException;
import errors.LogosException;
import errors.UnknownCommandException;

/**
 * Parses raw user input into executable {@link Command} objects.
 *
 * Date/time fields are parsed using the pattern {@code yyyy-MM-dd HHmm}
 * (e.g., {@code 2019-12-02 1800}).
 */
public class Parser {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

     /**
      * Parses a single line of user input and returns the corresponding {@link Command}.
      * <p>
      * The first token is treated as the command keyword; the remainder (if any) is
      * parsed as the argument string. Commands that require dates/times expect the
      * format {@code yyyy-MM-dd HHmm}.
      *
      * @param userInput the raw input line from the user
      * @return a concrete {@link Command} ready to be executed
      * @throws InvalidCommandFormatException if the command is recognized but its arguments
      *         are missing or malformed (e.g., wrong date format or non-numeric index)
      * @throws UnknownCommandException if the command keyword is not recognized
      * @throws LogosException if another parsing-related error occurs
      */
    public Command parse(String userInput) throws LogosException {
        String[] parts = userInput.split(" ", 2); // split into [command, argument]
        assert parts.length >= 1 : "Parser must always find at least one word";
        String commandKeyword = parts[0];
        String argument = parts.length > 1 ? parts[1] : null;
        CommandType commandType = CommandType.fromString(commandKeyword);

        return switch (commandType) {
            case BYE     -> parseBye();
            case LIST    -> parseList();
            case TODO    -> parseTodo(argument);
            case DEADLINE-> parseDeadline(argument);
            case EVENT   -> parseEvent(argument);
            case MARK    -> parseMark(argument);
            case UNMARK  -> parseUnmark(argument);
            case DELETE  -> parseDelete(argument);
            case FIND    -> parseFind(argument);
        };
    }

    // ── Per-command parsers ──────────────────────────────────────────────
    /**
     * Parses a {@code bye} command.
     *
     * @return a {@link ByeCommand} that signals program termination
     */
    private Command parseBye() {
        return new ByeCommand();
    }

    /**
     * Parses a {@code list} command.
     *
     * @return a {@link ListCommand} that lists all tasks
     */
    private Command parseList() {
        return new ListCommand();
    }

    /**
     * Parses a {@code todo} command.
     *
     * @param arg the raw argument string containing the task description
     * @return a {@link TodoCommand} encapsulating the new task
     * @throws InvalidCommandFormatException if {@code arg} is null or blank
     */
    private Command parseTodo(String arg) throws InvalidCommandFormatException {
        String desc = requireArg(arg, "todo <desc>");
        return new TodoCommand(desc);
    }

    /**
     * Parses a {@code deadline} command in the format:
     * <pre>
     *   deadline &lt;desc&gt; /by &lt;yyyy-MM-dd HHmm&gt;
     * </pre>
     *
     * @param arg the raw argument string containing description and deadline
     * @return a {@link DeadlineCommand} with parsed description and due date
     * @throws InvalidCommandFormatException if required tokens are missing, empty,
     *         or the datetime cannot be parsed
     */
    private Command parseDeadline(String arg) throws InvalidCommandFormatException {
        String a = requireArg(arg, "deadline <desc> /by <yyyy-MM-dd HHmm>");
        int byPos = getPositionOf(
                a.toLowerCase(), 
                "/by", 
                "deadline <desc> /by <yyyy-MM-dd HHmm>");
        String desc = a.substring(0, byPos).trim();
        String when = a.substring(byPos + 3).trim(); // len("/by") = 3
        if (desc.isEmpty() || when.isEmpty()) {
            throw new InvalidCommandFormatException(
                    "deadline", 
                    "deadline <desc> /by <yyyy-MM-dd HHmm>");
        }
        LocalDateTime dt = parseDateTime(when, "Date should be yyyy-MM-dd HHmm, e.g., 2019-12-02 1800");
        return new DeadlineCommand(desc, dt);
    }

    /**
     * Parses an {@code event} command in the format:
     * <pre>
     *   event &lt;desc&gt; /from &lt;yyyy-MM-dd HHmm&gt; /to &lt;yyyy-MM-dd HHmm&gt;
     * </pre>
     *
     * @param arg the raw argument string containing description, start, and end times
     * @return an {@link EventCommand} with parsed description and time interval
     * @throws InvalidCommandFormatException if required tokens are missing, empty,
     *         or the datetimes cannot be parsed
     */
    private Command parseEvent(String arg) throws InvalidCommandFormatException {
        String a = requireArg(arg, "event <desc> /from <start> /to <end>");
        String low = a.toLowerCase();
        int fromPos = getPositionOf(low, "/from", "event <desc> /from <start> /to <end>");
        int toPos   = getPositionOf(low, "/to",   "event <desc> /from <start> /to <end>");
        if (toPos <= fromPos) {
            throw new InvalidCommandFormatException(
                    "event", 
                    "event <desc> /from <start> /to <end>");
        }

        String desc = a.substring(0, fromPos).trim();
        String from = a.substring(fromPos + 5, toPos).trim();  // "/from"
        String to   = a.substring(toPos + 3).trim();           // "/to"
        if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new InvalidCommandFormatException(
                    "event", 
                    "event <desc> /from <start> /to <end>");
        }

        LocalDateTime start = parseDateTime(from, "Date should be yyyy-MM-dd HHmm, e.g., 2019-12-02 1800");
        LocalDateTime end   = parseDateTime(to,   "Date should be yyyy-MM-dd HHmm, e.g., 2019-12-02 1800");
        if (end.isBefore(start)) {
            throw new InvalidCommandFormatException(
                    "event", 
                    "End time must not be before start time");
        }
        return new EventCommand(desc, start, end);
    }

    /**
     * Parses a {@code mark} command.
     *
     * @param arg the raw argument string containing a 1-based task index
     * @return a {@link MarkCommand} with the parsed index
     * @throws InvalidCommandFormatException if {@code arg} is null, empty,
     *         or not a positive integer
     */
    private Command parseMark(String arg) throws InvalidCommandFormatException {
        int idx = parseIndex(arg, "mark <taskNumber>");
        return new MarkCommand(idx);
    }

    /**
     * Parses an {@code unmark} command.
     *
     * @param arg the raw argument string containing a 1-based task index
     * @return an {@link UnmarkCommand} with the parsed index
     * @throws InvalidCommandFormatException if {@code arg} is null, empty,
     *         or not a positive integer
     */
    private Command parseUnmark(String arg) throws InvalidCommandFormatException {
        int idx = parseIndex(arg, "unmark <taskNumber>");
        return new UnmarkCommand(idx);
    }

    /**
     * Parses a {@code delete} command.
     *
     * @param arg the raw argument string containing a 1-based task index
     * @return a {@link DeleteCommand} with the parsed index
     * @throws InvalidCommandFormatException if {@code arg} is null, empty,
     *         or not a positive integer
     */
    private Command parseDelete(String arg) throws InvalidCommandFormatException {
        int idx = parseIndex(arg, "delete <taskNumber>");
        return new DeleteCommand(idx);
    }

    /**
     * Parses a {@code find} command.
     *
     * @param arg the raw argument string containing a search keyword
     * @return a {@link FindCommand} with the search query
     * @throws InvalidCommandFormatException if {@code arg} is null or blank
     */
    private Command parseFind(String arg) throws InvalidCommandFormatException {
        String q = requireArg(arg, "find <keyword>");
        return new FindCommand(q);
    }

    // ── Shared helpers ────────────────────────────────────────────────────
    /**
     * Ensures that a required argument string is present and non-blank.
     *
     * @param arg   the raw argument string (may be null)
     * @param usage the usage string to include in exception messages
     * @return the trimmed argument
     * @throws InvalidCommandFormatException if {@code arg} is null or blank
     */
    private static String requireArg(String arg, String usage) throws InvalidCommandFormatException {
        if (arg == null || arg.trim().isEmpty()) {
            throw new InvalidCommandFormatException(extractVerb(usage), usage);
        }
        return arg.trim();
    }

    /**
     * Parses a task index argument.
     *
     * @param arg   the raw argument string
     * @param usage the usage string to include in exception messages
     * @return the parsed 1-based task index
     * @throws InvalidCommandFormatException if {@code arg} is missing, not numeric,
     *         or less than 1
     */
    private static int parseIndex(String arg, String usage) throws InvalidCommandFormatException {
        String s = requireArg(arg, usage);
        try {
            int n = Integer.parseInt(s.trim());
            if (n <= 0) throw new NumberFormatException();
            return n;
        } catch (NumberFormatException e) {
            throw new InvalidCommandFormatException(extractVerb(usage), usage);
        }
    }

    /**
     * Finds the index of a required token in a command string.
     *
     * @param fullString     the lowercased input string to search
     * @param searchString   the token to search for (e.g., {@code "/by"})
     * @param commandFormat  the usage string to include in exception messages
     * @return the index of {@code searchString} in {@code fullString}
     * @throws InvalidCommandFormatException if {@code searchString} is not found
     */
    private static int getPositionOf(
            String fullString, 
            String searchString, 
            String commandFormat) throws InvalidCommandFormatException {
        int p = fullString.indexOf(searchString);
        if (p < 0) throw new InvalidCommandFormatException(extractVerb(commandFormat), commandFormat);
        return p;
    }

    /**
     * Parses a datetime argument using the standard {@code yyyy-MM-dd HHmm} pattern.
     *
     * @param s     the raw datetime string
     * @param error the error message to use in case of failure
     * @return a parsed {@link LocalDateTime}
     * @throws InvalidCommandFormatException if parsing fails
     */
    private static LocalDateTime parseDateTime(String s, String error) throws InvalidCommandFormatException {
        try {
            return LocalDateTime.parse(s, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new InvalidCommandFormatException(extractVerb(error), error);
        }
    }

    /**
     * Extracts the "verb" (first token) from a string.
     * <p>
     * For example, given {@code "deadline <desc> /by <yyyy-MM-dd HHmm>"}, this
     * method returns {@code "deadline"}. If the string contains no spaces,
     * the entire string is returned.
     *
     * @param fullString the usage string (e.g., {@code "mark <taskNumber>"})
     * @return the first word of the string, or the whole string if no spaces exist
     */
    private static String extractVerb(String fullString) {
        int spacePosition = fullString.indexOf(' ');
        return spacePosition > 0 ? fullString.substring(0, spacePosition) : fullString;
    }
}
