import java.util.Scanner;

public class Logos {

    private static int IndentLength = 4;
    private static int LineLength = 40;
    private static String ChatbotName = "Logos";
    private static String[] tasks;
    private static int taskCount = 0;

    public static void main(String[] args) {
        // Initialise Tasks
        tasks = new String[100];

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
                "-> Use the command 'bye' to when you're done!");

        // Input and Response
        Scanner sc = new Scanner(System.in);
        Boolean chatActive = true;
        while (chatActive) {
            String userInput = sc.nextLine();
            switch (userInput) {
                case "bye" -> {
                    chatActive = false;
                }
                case "list" -> {
                    Logos.listTasks();
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
        Logos.tasks[Logos.taskCount] = taskName;
        Logos.taskCount++;
        Logos.respond("Task added: \"" + taskName + "\"",
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
            System.out.printf("%s%d. %s\n", indent, i + 1, Logos.tasks[i]);
        }
        System.out.println(indent + line);
    }
}