package ui;

import java.util.List;
import java.util.Scanner;

/**
 * Handles all user interface interactions in the terminal.
 * <p>
 * The {@code Ui} class is responsible for:
 * <ul>
 *   <li>Reading user input from the console.</li>
 *   <li>Displaying formatted responses, lists, and menus.</li>
 *   <li>Showing welcome and exit messages.</li>
 * </ul>
 * Text is automatically indented and surrounded with divider lines
 * for readability.
 */
public class Ui {
    private final int INDENT_LENGTH = 4;
    private final int LINE_LENGTH = 80;
    private final Scanner sc = new Scanner(System.in);;

    /**
     * Reads a single line of text from the user.
     *
     * @return the raw line of user input
     */
    public String readLine() {
        String userInput = sc.nextLine();
        return userInput;
    };

    /**
     * Closes the underlying {@link Scanner} to release resources.
     * Should only be called once when the application is shutting down.
     */
    private void closeScanner() {
        sc.close();
    };

    /**
     * Displays one or more messages, indented and wrapped between
     * horizontal divider lines.
     *
     * @param messages the lines of text to display
     */
    public void respond(String... messages) {
        String indent = " ".repeat(INDENT_LENGTH);
        String dividerLine = "-".repeat(LINE_LENGTH);
        System.out.println(indent + dividerLine);
        for (String message : messages) {
            for (String line : message.split("\n")) {
                System.out.println(indent + line);
            }
        }
        System.out.println(indent + dividerLine);
    }

    /**
     * Displays a welcome message with the chatbot’s logo, name,
     * and a list of example commands.
     *
     * @param chatbotLogo the ASCII logo of the chatbot
     * @param chatbotName the display name of the chatbot
     */
    public void showWelcome(String chatbotLogo, String chatbotName) {
        System.out.println("Welcome to...\n" + chatbotLogo);
        this.respond(
                "Hello! I'm " + chatbotName + " :) Your friendly terminal task manager.",
                "",
                "Here are some commands you can try:",
                "-> todo <desc>                             : Add a simple task",
                "-> deadline <desc> /by <time>              : Add a task with a deadline (<yyyy-MM-dd HHmm>)",
                "-> event <desc> /from <start> /to <end>"
                        + "    : Add an event with a start and end time (<yyyy-MM-dd HHmm>)",
                "-> list                                    : Show all tasks",
                "-> mark <taskNumber>                       : Mark a task as done",
                "-> unmark <taskNumber>                     : Mark a task as not done",
                "-> bye                                     : Exit the program");
    };

    /**
     * Displays a numbered list of items with a pretext message.
     *
     * @param list the list of items to display
     * @param pretext the header or description shown before the list
     */
    public void showList(List<String> list, String pretext) {
        String indent = " ".repeat(INDENT_LENGTH);
        String dividerLine = "-".repeat(LINE_LENGTH);
        System.out.println(indent + dividerLine);
        System.out.println(indent + pretext);
        for (int i = 0; i < list.size(); i++) {
            String currentLine = list.get(i);
            System.out.printf("%s%d. %s\n", indent, i + 1, currentLine);
        }
        System.out.println(indent + dividerLine);
    }

    /**
     * Displays a goodbye message and closes the input scanner.
     */
    public void showExit() {
        closeScanner();
        respond("Bye. Hope to see you again soon!");
    }
}
