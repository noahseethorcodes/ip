import java.util.Scanner;

public class Logos {

    private static int IndentLength = 4;
    private static int LineLength = 60;
    private static String ChatbotName = "Logos";
    private static Task[] tasks;
    private static int taskCount = 0;

    public static void main(String[] args) {
        // Initialise Tasks
        tasks = new Task[100];

        // Welcome!
        String logo = " _                           \n"
                + "| |    ___   __ _  ___  ___  \n"
                + "| |   / _ \\ / _` |/ _ \\/ __| \n"
                + "| |__| (_) | (_| | (_) \\__ \\ \n"
                + "|_____\\___/ \\__, |\\___/|___/ \n"
                + "            |___/            \n";
        System.out.println("Welcome to...\n" + logo);
        Logos.respond("Hello! I'm " + Logos.ChatbotName + ":) Your friendly terminal task manager.\n",
                "You may find the following commands helpful:",
                "-> Type in the name of a task to add it to the task list",
                "-> Use the command 'list' to view your current task list",
                "-> Use the command 'mark' to mark a task as done!",
                "-> Use the command 'unmark' to mark a task as not yet done!",
                "-> Use the command 'bye' to when you're done!");

        // Input and Response
        Scanner sc = new Scanner(System.in);
        Boolean chatActive = true;
        while (chatActive) {
            String userInput = sc.nextLine();
            String[] parts = userInput.split(" ", 2); // split into [command, argument]
            String command = parts[0];
            String argument = parts.length > 1 ? parts[1] : null;
            switch (command) {
                case "bye" -> {
                    chatActive = false;
                }
                case "list" -> {
                    Logos.listTasks();
                }
                case "mark" -> {
                    if (argument != null) {
                        try {
                            int taskNumber = Integer.parseInt(argument);
                            Logos.markTaskAsDone(taskNumber);
                        } catch (NumberFormatException e) {
                            Logos.respond("Invalid task number!");
                        }
                    } else {
                        Logos.respond("Please specify a task number.");
                    }
                }
                case "unmark" -> {
                    if (argument != null) {
                        try {
                            int taskNumber = Integer.parseInt(argument);
                            Logos.markTaskAsNotDone(taskNumber);
                        } catch (NumberFormatException e) {
                            Logos.respond("Invalid task number!");
                        }
                    } else {
                        Logos.respond("Please specify a task number.");
                    }
                }
                default -> {
                    Logos.addTask(userInput);
                }
            }
        }

        // Exit message on 'bye' command.
        Logos.respond("Bye. Hope to see you again soon!");
        sc.close();
    }

    // Helper Function that indents text and wraps with horizontal lines
    private static void respond(String... messages) {
        String indent = " ".repeat(Logos.IndentLength);
        String line = "-".repeat(Logos.LineLength);
        System.out.println(indent + line);
        for (String message : messages) {
            System.out.println(indent + message);
        }
        System.out.println(indent + line);
    }

    private static void addTask(String taskName) {
        Task newTask = new Task(taskName);
        Logos.tasks[Logos.taskCount] = newTask;
        Logos.taskCount++;
        Logos.respond("Task added: \"" + newTask.getDescription() + "\"",
                "Use the command 'list' to view your current task list");
    }

    private static void listTasks() {
        if (Logos.taskCount <= 0) {
            Logos.respond("There are no tasks in your task list currently.",
                    "Type in the name of a task to add it to the task list");
            return;
        }
        String indent = " ".repeat(Logos.IndentLength);
        String line = "-".repeat(Logos.LineLength);
        System.out.println(indent + line);
        System.out.println(indent + "Here's your current tasks, in order of when they were added:");
        for (int i = 0; i < Logos.taskCount; i++) {
            System.out.printf("%s%d. %s\n", indent, i + 1, Logos.tasks[i].getAsListItem());
        }
        System.out.println(indent + line);
    }

    private static void markTaskAsDone(int taskIndex) {
        Task selectedTask = Logos.tasks[taskIndex - 1]; // Adjust for 0-index array
        if (selectedTask == null) {
            Logos.respond(
                    String.format(
                            "There is no Task %d. Did you mean to input another number?", taskIndex));
            return;
        }

        if (selectedTask.isDone()) {
            Logos.respond("This task is already marked as done!", selectedTask.getAsListItem());
            return;
        }

        selectedTask.markAsDone();
        Logos.respond("Nice! I've marked this task as done:", selectedTask.getAsListItem());
    }

    private static void markTaskAsNotDone(int taskIndex) {
        Task selectedTask = Logos.tasks[taskIndex - 1]; // Adjust for 0-index array
        if (selectedTask == null) {
            Logos.respond(
                    String.format(
                            "There is no Task %d. Did you mean to input another number?", taskIndex));
            return;
        }

        if (!selectedTask.isDone()) {
            Logos.respond("This task is already marked as not done!", selectedTask.getAsListItem());
            return;
        }

        selectedTask.markAsNotDone();
        Logos.respond("Alright! I've marked this task as not done yet:", selectedTask.getAsListItem());
    }
}