import java.util.Scanner;

public class Logos {

    private static Integer IndentLength = 4;
    private static Integer LineLength = 40;
    private static String ChatbotName = "Logos";

    public static void main(String[] args) {
        // Welcome!
        String logo = " _                           \n"
                + "| |    ___   __ _  ___  ___  \n"
                + "| |   / _ \\ / _` |/ _ \\/ __| \n"
                + "| |__| (_) | (_| | (_) \\__ \\ \n"
                + "|_____\\___/ \\__, |\\___/|___/ \n"
                + "            |___/            \n";
        System.out.println("Welcome to...\n" + logo);
        Logos.respond("Hello! I'm " + Logos.ChatbotName,
                "What can I do for you?");

        // Echo
        Scanner sc = new Scanner(System.in);
        Boolean chatActive = true;
        while (chatActive) {
            String inputLine = sc.nextLine();
            if (inputLine.equals("bye")) {
                chatActive = false;
                break;
            }
            Logos.respond(inputLine);
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
}