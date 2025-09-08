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
        Command command = null;
        switch (commandType) {
            case BYE -> {
                command = new ByeCommand();
            }
            case LIST -> {
                command = new ListCommand();
            }
            case MARK -> {
                if (argument != null) {
                    try {
                        int taskNumber = Integer.parseInt(argument);
                        command = new MarkCommand(taskNumber);
                    } catch (NumberFormatException e) {
                        throw new InvalidCommandFormatException(commandType.getKeyword(), "mark <taskNumber>");
                    }
                } else {
                    throw new InvalidCommandFormatException(commandType.getKeyword(), "mark <taskNumber>");
                }
            }
            case UNMARK -> {
                if (argument != null) {
                    try {
                        int taskNumber = Integer.parseInt(argument);
                        command = new UnmarkCommand(taskNumber);
                    } catch (NumberFormatException e) {
                        throw new InvalidCommandFormatException(commandType.getKeyword(), "unmark <taskNumber>");
                    }
                } else {
                    throw new InvalidCommandFormatException(commandType.getKeyword(), "unmark <taskNumber>");
                }
            }
            case TODO -> {
                command = new TodoCommand(argument);
            }
            case DEADLINE -> {
                int byPos = argument.toLowerCase().indexOf("/by");
                if (byPos < 0) {
                    throw new InvalidCommandFormatException(
                            commandType.getKeyword(),
                            "deadline <desc> /by <yyyy-MM-dd HHmm>");
                }

                String description = argument.substring(0, byPos).trim();
                String by = argument.substring(byPos + 3).trim(); // len("/by") = 3

                LocalDateTime deadline;
                try {
                    deadline = LocalDateTime.parse(by, INPUT_FORMAT);
                } catch (DateTimeParseException e) {
                    throw new InvalidCommandFormatException(
                            commandType.getKeyword(),
                            "Date should be in yyyy-MM-dd HHmm format, e.g., 2019-12-02 1800");
                }

                if (description.isEmpty()) {
                    throw new InvalidCommandFormatException(commandType.getKeyword(),
                            "deadline <desc> /by <yyyy-MM-dd HHmm>");
                }
                command = new DeadlineCommand(description, deadline);
            }
            case EVENT -> {
                int fromPos = argument.indexOf("/from");
                int toPos = argument.indexOf("/to");
                if (fromPos < 0 || toPos < 0 || toPos <= fromPos) {
                    throw new InvalidCommandFormatException(commandType.getKeyword(),
                            "event <desc> /from <start> /to <end>");
                }

                String desc = argument.substring(0, fromPos).trim();
                String from = argument.substring(fromPos + 5, toPos).trim(); // 5 = len("/from")
                String to = argument.substring(toPos + 3).trim(); // 3 = len("/to")

                LocalDateTime startDateTime;
                LocalDateTime endDateTime;
                try {
                    startDateTime = LocalDateTime.parse(from, INPUT_FORMAT);
                    endDateTime = LocalDateTime.parse(to, INPUT_FORMAT);
                } catch (DateTimeParseException e) {
                    throw new InvalidCommandFormatException(
                            commandType.getKeyword(),
                            "Date should be in yyyy-MM-dd HHmm format, e.g., 2019-12-02 1800");
                }

                if (desc.isEmpty()) {
                    throw new InvalidCommandFormatException(commandType.getKeyword(),
                            "event <desc> /from <start> /to <end>");
                }
                command = new EventCommand(desc, startDateTime, endDateTime);
            }
            case DELETE -> {
                if (argument != null) {
                    try {
                        int taskNumber = Integer.parseInt(argument);
                        command = new DeleteCommand(taskNumber);
                    } catch (NumberFormatException e) {
                        throw new InvalidCommandFormatException(commandType.getKeyword(), "delete <taskNumber>");
                    }
                } else {
                    throw new InvalidCommandFormatException(commandType.getKeyword(), "delete <taskNumber>");
                }
            }
            case FIND -> {
                command = new FindCommand(argument);
            }
            default -> {
                throw new UnknownCommandException(commandType.getKeyword());
            }
        }
        return command;
    }
}
