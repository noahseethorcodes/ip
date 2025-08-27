import java.io.IOException;
import java.util.ArrayList;
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
import errors.InvalidCommandFormatException;
import errors.InvalidIndexException;
import commands.Command;

public class Logos {

    private static int INDENT_LENGTH = 4;
    private static int LINE_LENGTH = 80;
    private static ArrayList<Task> tasks;

    private static String LOCAL_STORAGE_FILE_PATH = "./data/tasks.txt";
    public static Storage storage;

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    public static void main(String[] args) {
        // Initialise Tasks
        Logos.storage = new Storage(LOCAL_STORAGE_FILE_PATH);
        Logos.tasks = new ArrayList<Task>();
        Logos.storage.loadTasks(tasks);

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
                Command command = Command.fromString(commandKeyword);
                switch (command) {
                    case BYE -> {
                        Logos.storage.saveTasks(tasks);
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
        Todo newTodo = new Todo(taskName);
        Logos.tasks.add(newTodo);
        ui.respond("Todo added: \"" + newTodo.getDescription() + "\"",
                String.format("Now you have %d tasks in the list~", Logos.tasks.size()),
                "Use the command 'list' to view your current task list");
        Logos.storage.saveTasks(tasks);
    }

    private static void addDeadline(Ui ui, String taskName, LocalDateTime deadline) throws IOException {
        Deadline newDeadline = new Deadline(taskName, deadline);
        Logos.tasks.add(newDeadline);
        ui.respond(
                String.format("Deadline added: \"%s\", (by: %s)",
                        newDeadline.getDescription(),
                        newDeadline.getDeadline()),
                String.format("Now you have %d tasks in the list~", Logos.tasks.size()),
                "Use the command 'list' to view your current task list");
        Logos.storage.saveTasks(tasks);
    }

    private static void addEvent(Ui ui, String taskName, LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException {
        Event newEvent = new Event(taskName, startDateTime, endDateTime);
        Logos.tasks.add(newEvent);
        ui.respond(
                String.format("Event added: \"%s\", (from: %s, to: %s)",
                        newEvent.getDescription(),
                        newEvent.getStartDateTime(),
                        newEvent.getEndDateTime()),
                String.format("Now you have %d tasks in the list~", Logos.tasks.size()),
                "Use the command 'list' to view your current task list");
        Logos.storage.saveTasks(tasks);
    }

    private static void listTasks(Ui ui) {
        if (Logos.tasks.isEmpty()) {
            ui.respond("There are no tasks in your task list currently.",
                    "Type in the name of a task to add it to the task list");
            return;
        }
        String indent = " ".repeat(Logos.INDENT_LENGTH);
        String line = "-".repeat(Logos.LINE_LENGTH);
        System.out.println(indent + line);
        System.out.println(indent + "Here's your current tasks, in order of when they were added:");
        for (int i = 0; i < Logos.tasks.size(); i++) {
            Task currentTask = Logos.tasks.get(i);
            System.out.printf("%s%d. %s\n", indent, i + 1, currentTask.getAsListItem());
        }
        System.out.println(indent + line);
    }

    private static void markTaskAsDone(Ui ui, int taskIndex) throws InvalidIndexException, IOException {
        if (taskIndex > Logos.tasks.size() | taskIndex <= 0) {
            throw new InvalidIndexException(taskIndex);
        }
        Task selectedTask = Logos.tasks.get(taskIndex - 1);

        if (selectedTask.isDone()) {
            ui.respond("This task is already marked as done!", selectedTask.getAsListItem());
            return;
        }

        selectedTask.markAsDone();
        ui.respond("Nice! I've marked this task as done:", selectedTask.getAsListItem());
        Logos.storage.saveTasks(tasks);
    }

    private static void markTaskAsNotDone(Ui ui, int taskIndex) throws InvalidIndexException, IOException {
        if (taskIndex > Logos.tasks.size() | taskIndex <= 0) {
            throw new InvalidIndexException(taskIndex);
        }
        Task selectedTask = Logos.tasks.get(taskIndex - 1);

        if (!selectedTask.isDone()) {
            ui.respond("This task is already marked as not done!", selectedTask.getAsListItem());
            return;
        }

        selectedTask.markAsNotDone();
        ui.respond("Alright! I've marked this task as not done yet:", selectedTask.getAsListItem());
        Logos.storage.saveTasks(tasks);
    }

    private static void deleteTask(Ui ui, int taskIndex) throws InvalidIndexException, IOException {
        if (taskIndex > Logos.tasks.size() | taskIndex <= 0) {
            throw new InvalidIndexException(taskIndex);
        }
        Task selectedTask = Logos.tasks.get(taskIndex - 1);
        Logos.tasks.remove(taskIndex - 1);
        ui.respond("Todo removed: \"" + selectedTask.getDescription() + "\"",
                String.format("Now you have %d tasks in the list~", Logos.tasks.size()),
                "Use the command 'list' to view your current task list");
        Logos.storage.saveTasks(tasks);
    }
}