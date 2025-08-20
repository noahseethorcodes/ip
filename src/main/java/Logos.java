import java.util.Scanner;

import java.util.ArrayList;

import tasks.Task;
import tasks.Todo;
import tasks.Deadline;
import tasks.Event;

import errors.UnknownCommandException;
import errors.InvalidCommandFormatException;
import errors.InvalidIndexException;

import commands.Command;

public class Logos {

    private static int INDENT_LENGTH = 4;
    private static int LINE_LENGTH = 80;
    private static String CHATBOT_NAME = "Logos";
    private static ArrayList<Task> tasks;

    public static void main(String[] args) {
        // Initialise Tasks
        Logos.tasks = new ArrayList<Task>();

        // Welcome!
        String logo = " _                           \n"
                + "| |    ___   __ _  ___  ___  \n"
                + "| |   / _ \\ / _` |/ _ \\/ __| \n"
                + "| |__| (_) | (_| | (_) \\__ \\ \n"
                + "|_____\\___/ \\__, |\\___/|___/ \n"
                + "            |___/            \n";
        System.out.println("Welcome to...\n" + logo);
        Logos.respond(
                "Hello! I'm " + Logos.CHATBOT_NAME + " :) Your friendly terminal task manager.\n",
                "Here are some commands you can try:",
                "-> todo <description>                      : Add a simple task",
                "-> deadline <desc> /by <time>              : Add a task with a deadline",
                "-> event <desc> /from <start> /to <end>    : Add an event with a start and end time",
                "-> list                                    : Show all tasks",
                "-> mark <taskNumber>                       : Mark a task as done",
                "-> unmark <taskNumber>                     : Mark a task as not done",
                "-> bye                                     : Exit the program");

        // Input and Response
        Scanner sc = new Scanner(System.in);
        Boolean chatActive = true;
        while (chatActive) {
            String userInput = sc.nextLine();
            String[] parts = userInput.split(" ", 2); // split into [command, argument]
            String commandKeyword = parts[0];
            String argument = parts.length > 1 ? parts[1] : null;
            try {
                Command command = Command.fromString(commandKeyword);
                switch (command) {
                    case BYE -> {
                        chatActive = false;
                    }
                    case LIST -> {
                        Logos.listTasks();
                    }
                    case MARK -> {
                        if (argument != null) {
                            try {
                                int taskNumber = Integer.parseInt(argument);
                                Logos.markTaskAsDone(taskNumber);
                            } catch (NumberFormatException e) {
                                Logos.respond("Invalid task number!");
                            }
                        } else {
                            throw new InvalidCommandFormatException(command.getKeyword(), "mark <taskNumber>");
                        }
                    }
                    case UNMARK -> {
                        if (argument != null) {
                            try {
                                int taskNumber = Integer.parseInt(argument);
                                Logos.markTaskAsNotDone(taskNumber);
                            } catch (NumberFormatException e) {
                                Logos.respond("Invalid task number!");
                            }
                        } else {
                            throw new InvalidCommandFormatException(command.getKeyword(), "unmark <taskNumber>");
                        }
                    }
                    case TODO -> {
                        Logos.addTodo(argument);
                    }
                    case DEADLINE -> {
                        int byPos = argument.toLowerCase().indexOf("/by");
                        if (byPos < 0) {
                            throw new InvalidCommandFormatException(command.getKeyword(), "deadline <desc> /by <when>");
                        } else {
                            String description = argument.substring(0, byPos).trim();
                            String deadline = argument.substring(byPos + 3).trim(); // len("/by") = 3
                            if (description.isEmpty() || deadline.isEmpty()) {
                                throw new InvalidCommandFormatException(command.getKeyword(),
                                        "deadline <desc> /by <when>");
                            } else {
                                Logos.addDeadline(description, deadline);
                            }
                        }
                    }
                    case EVENT -> {
                        int fromPos = argument.indexOf("/from");
                        int toPos = argument.indexOf("/to");
                        if (fromPos < 0 || toPos < 0 || toPos <= fromPos) {
                            throw new InvalidCommandFormatException(command.getKeyword(),
                                    "event <desc> /from <start> /to <end>");
                        } else {
                            String desc = argument.substring(0, fromPos).trim();
                            String from = argument.substring(fromPos + 5, toPos).trim(); // 5 = len("/from")
                            String to = argument.substring(toPos + 3).trim(); // 3 = len("/to")
                            if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
                                throw new InvalidCommandFormatException(command.getKeyword(),
                                        "event <desc> /from <start> /to <end>");
                            } else {
                                Logos.addEvent(desc, from, to);
                            }
                        }
                    }
                    case DELETE -> {
                        if (argument != null) {
                            try {
                                int taskNumber = Integer.parseInt(argument);
                                Logos.deleteTask(taskNumber);
                            } catch (NumberFormatException e) {
                                Logos.respond("Invalid task number!");
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
                Logos.respond(e.getMessage());
            } catch (InvalidCommandFormatException e) {
                Logos.respond(e.getMessage());
            } catch (InvalidIndexException e) {
                Logos.respond(e.getMessage());
            }
        }

        // Exit message on 'bye' command.
        Logos.respond("Bye. Hope to see you again soon!");
        sc.close();
    }

    // Helper Function that indents text and wraps with horizontal lines
    private static void respond(String... messages) {
        String indent = " ".repeat(Logos.INDENT_LENGTH);
        String line = "-".repeat(Logos.LINE_LENGTH);
        System.out.println(indent + line);
        for (String message : messages) {
            System.out.println(indent + message);
        }
        System.out.println(indent + line);
    }

    private static void addTodo(String taskName) {
        Todo newTodo = new Todo(taskName);
        Logos.tasks.add(newTodo);
        Logos.respond("Todo added: \"" + newTodo.getDescription() + "\"",
                String.format("Now you have %d tasks in the list~", Logos.tasks.size()),
                "Use the command 'list' to view your current task list");
    }

    private static void addDeadline(String taskName, String deadline) {
        Deadline newDeadline = new Deadline(taskName, deadline);
        Logos.tasks.add(newDeadline);
        Logos.respond(
                String.format("Deadline added: \"%s\", (by: %s)",
                        newDeadline.getDescription(),
                        newDeadline.getDeadline()),
                String.format("Now you have %d tasks in the list~", Logos.tasks.size()),
                "Use the command 'list' to view your current task list");
    }

    private static void addEvent(String taskName, String startDateTime, String endDateTime) {
        Event newEvent = new Event(taskName, startDateTime, endDateTime);
        Logos.tasks.add(newEvent);
        Logos.respond(
                String.format("Event added: \"%s\", (from: %s, to: %s)",
                        newEvent.getDescription(),
                        newEvent.getStartDateTime(),
                        newEvent.getEndDateTime()),
                String.format("Now you have %d tasks in the list~", Logos.tasks.size()),
                "Use the command 'list' to view your current task list");
    }

    private static void listTasks() {
        if (Logos.tasks.isEmpty()) {
            Logos.respond("There are no tasks in your task list currently.",
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

    private static void markTaskAsDone(int taskIndex) throws InvalidIndexException {
        if (taskIndex > Logos.tasks.size() | taskIndex <= 0) {
            throw new InvalidIndexException(taskIndex);
        }
        Task selectedTask = Logos.tasks.get(taskIndex - 1);

        if (selectedTask.isDone()) {
            Logos.respond("This task is already marked as done!", selectedTask.getAsListItem());
            return;
        }

        selectedTask.markAsDone();
        Logos.respond("Nice! I've marked this task as done:", selectedTask.getAsListItem());
    }

    private static void markTaskAsNotDone(int taskIndex) throws InvalidIndexException {
        if (taskIndex > Logos.tasks.size() | taskIndex <= 0) {
            throw new InvalidIndexException(taskIndex);
        }
        Task selectedTask = Logos.tasks.get(taskIndex - 1);

        if (!selectedTask.isDone()) {
            Logos.respond("This task is already marked as not done!", selectedTask.getAsListItem());
            return;
        }

        selectedTask.markAsNotDone();
        Logos.respond("Alright! I've marked this task as not done yet:", selectedTask.getAsListItem());
    }

    private static void deleteTask(int taskIndex) throws InvalidIndexException {
        if (taskIndex > Logos.tasks.size() | taskIndex <= 0) {
            throw new InvalidIndexException(taskIndex);
        }
        Task selectedTask = Logos.tasks.get(taskIndex - 1);
        Logos.tasks.remove(taskIndex - 1);
        Logos.respond("Todo removed: \"" + selectedTask.getDescription() + "\"",
                String.format("Now you have %d tasks in the list~", Logos.tasks.size()),
                "Use the command 'list' to view your current task list");
    }
}