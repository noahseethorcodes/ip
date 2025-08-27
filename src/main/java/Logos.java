import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import ui.Ui;

import errors.UnknownCommandException;
import localstorage.Storage;
import tasklist.TaskList;
import errors.InvalidCommandFormatException;
import errors.InvalidIndexException;
import errors.LogosException;
import commands.ByeCommand;
import commands.Command;
import commands.CommandType;
import commands.DeadlineCommand;
import commands.DeleteCommand;
import commands.EventCommand;
import commands.ListCommand;
import commands.MarkCommand;
import commands.TodoCommand;
import commands.UnmarkCommand;

public class Logos {
    private static TaskList taskList;

    private static String LOCAL_STORAGE_FILE_PATH = "./data/tasks.txt";
    public static Storage storage;

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    public static void main(String[] args) {
        // Initialise Tasks
        Logos.storage = new Storage(LOCAL_STORAGE_FILE_PATH);
        Logos.taskList = new TaskList(storage);
        Logos.taskList.loadFromStorgae();

        // Initialise Ui
        Ui ui = new Ui();

        // Welcome!
        String logo = " _                           \n"
                + "| |    ___   __ _  ___  ___  \n"
                + "| |   / _ \\ / _` |/ _ \\/ __| \n"
                + "| |__| (_) | (_| | (_) \\__ \\ \n"
                + "|_____\\___/ \\__, |\\___/|___/ \n"
                + "            |___/            \n";
        ui.showWelcome(logo, "Logos");

        // Input and Response
        Boolean chatActive = true;
        while (chatActive) {
            String userInput = ui.readLine();
            String[] parts = userInput.split(" ", 2); // split into [command, argument]
            String commandKeyword = parts[0];
            String argument = parts.length > 1 ? parts[1] : null;
            try {
                CommandType commandType = CommandType.fromString(commandKeyword);
                Command command = null;
                switch (commandType) {
                    case BYE -> {
                        chatActive = false;
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
                                ui.respond("Invalid task number!");
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
                                ui.respond("Invalid task number!");
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
                                    "deadline <desc> /by <yyyy-MM-dd HHmm>"
                            );
                        } 

                        String description = argument.substring(0, byPos).trim();
                        String by = argument.substring(byPos + 3).trim(); // len("/by") = 3

                        LocalDateTime deadline;
                        try {
                            deadline = LocalDateTime.parse(by, INPUT_FORMAT);
                        } catch (DateTimeParseException e) {
                            throw new InvalidCommandFormatException(
                                    commandType.getKeyword(), 
                                    "Date should be in yyyy-MM-dd HHmm format, e.g., 2019-12-02 1800"
                            );
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
                                    "Date should be in yyyy-MM-dd HHmm format, e.g., 2019-12-02 1800"
                            );
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
                                ui.respond("Invalid task number!");
                            }
                        } else {
                            throw new InvalidCommandFormatException(commandType.getKeyword(), "delete <taskNumber>");
                        }
                    }
                    default -> {
                        throw new UnknownCommandException(commandType.getKeyword());
                    }
                }

                if (command != null) {
                    command.execute(taskList, ui);
                }
            } catch (UnknownCommandException e) {
                ui.respond(e.getMessage());
            } catch (InvalidCommandFormatException e) {
                ui.respond(e.getMessage());
            } catch (InvalidIndexException e) {
                ui.respond(e.getMessage());
            } catch (IOException e) {
                ui.respond("Error handling local storage: " + e.getMessage());
            } catch (LogosException e) {
                ui.respond(e.getMessage());
            }
        }
    }
}