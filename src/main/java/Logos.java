import java.io.IOException;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import tasks.Task;
import tasks.Todo;
import tasks.Deadline;
import tasks.Event;

import ui.Ui;

import errors.UnknownCommandException;
import localstorage.Storage;
import tasklist.TaskList;
import errors.InvalidCommandFormatException;
import errors.InvalidIndexException;
import commands.CommandType;

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
                CommandType command = CommandType.fromString(commandKeyword);
                switch (command) {
                    case BYE -> {
                        chatActive = false;
                    }
                    case LIST -> {
                        Logos.listTasks(ui);
                    }
                    case MARK -> {
                        if (argument != null) {
                            try {
                                int taskNumber = Integer.parseInt(argument);
                                Logos.markTaskAsDone(ui, taskNumber);
                            } catch (NumberFormatException e) {
                                ui.respond("Invalid task number!");
                            }
                        } else {
                            throw new InvalidCommandFormatException(command.getKeyword(), "mark <taskNumber>");
                        }
                    }
                    case UNMARK -> {
                        if (argument != null) {
                            try {
                                int taskNumber = Integer.parseInt(argument);
                                Logos.markTaskAsNotDone(ui, taskNumber);
                            } catch (NumberFormatException e) {
                                ui.respond("Invalid task number!");
                            }
                        } else {
                            throw new InvalidCommandFormatException(command.getKeyword(), "unmark <taskNumber>");
                        }
                    }
                    case TODO -> {
                        Logos.addTodo(ui, argument);
                    }
                    case DEADLINE -> {
                        int byPos = argument.toLowerCase().indexOf("/by");
                        if (byPos < 0) {
                            throw new InvalidCommandFormatException(
                                    command.getKeyword(), 
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
                                    command.getKeyword(), 
                                    "Date should be in yyyy-MM-dd HHmm format, e.g., 2019-12-02 1800"
                            );
                        }

                        if (description.isEmpty()) {
                            throw new InvalidCommandFormatException(command.getKeyword(),
                                    "deadline <desc> /by <yyyy-MM-dd HHmm>");
                        } else {
                            Logos.addDeadline(ui, description, deadline);
                        }
                    }
                    case EVENT -> {
                        int fromPos = argument.indexOf("/from");
                        int toPos = argument.indexOf("/to");
                        if (fromPos < 0 || toPos < 0 || toPos <= fromPos) {
                            throw new InvalidCommandFormatException(command.getKeyword(),
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
                                    command.getKeyword(), 
                                    "Date should be in yyyy-MM-dd HHmm format, e.g., 2019-12-02 1800"
                            );
                        }

                        if (desc.isEmpty()) {
                            throw new InvalidCommandFormatException(command.getKeyword(),
                                    "event <desc> /from <start> /to <end>");
                        } else {
                            Logos.addEvent(ui, desc, startDateTime, endDateTime);
                        }
                    }
                    case DELETE -> {
                        if (argument != null) {
                            try {
                                int taskNumber = Integer.parseInt(argument);
                                Logos.deleteTask(ui, taskNumber);
                            } catch (NumberFormatException e) {
                                ui.respond("Invalid task number!");
                            }
                        } else {
                            throw new InvalidCommandFormatException(command.getKeyword(), "delete <taskNumber>");
                        }
                    }
                    default -> {
                        throw new UnknownCommandException(command.getKeyword());
                    }
                }
            } catch (UnknownCommandException e) {
                ui.respond(e.getMessage());
            } catch (InvalidCommandFormatException e) {
                ui.respond(e.getMessage());
            } catch (InvalidIndexException e) {
                ui.respond(e.getMessage());
            } catch (IOException e) {
                ui.respond("Error handling local storage: " + e.getMessage());
            }
        }

        // Exit message on 'bye' command.
        ui.showExit();
    }

    private static void addTodo(Ui ui, String taskName) throws IOException{
        Todo newTodo = Logos.taskList.addTodo(taskName);
        ui.respond("Todo added: \"" + newTodo.getDescription() + "\"",
                String.format("Now you have %d tasks in the list~", Logos.taskList.size()),
                "Use the command 'list' to view your current task list");
    }

    private static void addDeadline(Ui ui, String taskName, LocalDateTime deadline) throws IOException {
        Deadline newDeadline = Logos.taskList.addDeadline(taskName, deadline);
        ui.respond(
                String.format("Deadline added: \"%s\", (by: %s)",
                        newDeadline.getDescription(),
                        newDeadline.getDeadline()),
                String.format("Now you have %d tasks in the list~", Logos.taskList.size()),
                "Use the command 'list' to view your current task list");
    }

    private static void addEvent(Ui ui, String taskName, LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException {
        Event newEvent = Logos.taskList.addEvent(taskName, startDateTime, endDateTime);
        ui.respond(
                String.format("Event added: \"%s\", (from: %s, to: %s)",
                        newEvent.getDescription(),
                        newEvent.getStartDateTime(),
                        newEvent.getEndDateTime()),
                String.format("Now you have %d tasks in the list~", Logos.taskList.size()),
                "Use the command 'list' to view your current task list");
    }

    private static void listTasks(Ui ui) {
        List<String> list = Logos.taskList.listTasks();
        if (list.isEmpty()) {
            ui.respond("There are no tasks in your task list currently.",
                    "Type in the name of a task to add it to the task list");
            return;
        }
        ui.showList(Logos.taskList.listTasks(), "Here's your current tasks, in order of when they were added:");
    }

    private static void markTaskAsDone(Ui ui, int taskIndex) throws InvalidIndexException, IOException {
        Task selectedTask = Logos.taskList.markTask(taskIndex);
        ui.respond("Task marked as done:", selectedTask.getAsListItem());
    }

    private static void markTaskAsNotDone(Ui ui, int taskIndex) throws InvalidIndexException, IOException {
        Task selectedTask = Logos.taskList.unmarkTask(taskIndex);
        ui.respond("Task marked as not done yet:", selectedTask.getAsListItem());
    }

    private static void deleteTask(Ui ui, int taskIndex) throws InvalidIndexException, IOException {
        Task selectedTask = Logos.taskList.deleteTask(taskIndex);
        ui.respond("Todo removed: \"" + selectedTask.getDescription() + "\"",
                String.format("Now you have %d tasks in the list~", Logos.taskList.size()),
                "Use the command 'list' to view your current task list");
    }
}