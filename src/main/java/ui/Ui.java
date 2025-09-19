package ui;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

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
    private final static int INDENT_LENGTH = 4;
    private final static int LINE_LENGTH = 80;
    private final Scanner sc = new Scanner(System.in);

    /**
     * Reads a single line of text from the user.
     *
     * @return the raw line of user input
     */
    public String readLine() {
        String userInput = sc.nextLine();
        return userInput;
    }

    /**
     * Closes the underlying {@link Scanner} to release resources.
     * Should only be called once when the application is shutting down.
     */
    private void closeScanner() {
        sc.close();
    }

    /**
     * Displays one or more messages, indented and wrapped between
     * horizontal divider lines.
     *
     * @param messages the lines of text to display
     */
    public String respond(String... messages) {
        String indent = " ".repeat(INDENT_LENGTH);
        String dividerLine = "-".repeat(LINE_LENGTH);
        System.out.println(indent + dividerLine);
        for (String message : messages) {
            for (String line : message.split("\n")) {
                System.out.println(indent + line);
            }
        }
        System.out.println(indent + dividerLine);
        return (getGuiResponse(messages));
    }

    /**
     * Displays one or more messages for the GUI
     *
     * @param messages the lines of text to display
     */
    private String getGuiResponse(String[] messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append("\n");
        }
        return sb.toString().trim(); // remove trailing newline
    }

    /**
     * Displays a welcome message with the chatbot’s logo, name,
     * and a list of example commands.
     *
     * @param chatbotLogo the ASCII logo of the chatbot
     * @param chatbotName the display name of the chatbot
     */
    public String showWelcome(String chatbotLogo, String chatbotName) {
        System.out.println("Welcome to...\n" + chatbotLogo);
        return respond(
                "Hello! I'm " + chatbotName + " :) Your friendly chatbot task manager.",
                "",
                "Here are some commands you can try:",
                "-> todo <desc>                             : Add a simple task",
                "-> deadline <desc> /by <time>              : Add a task with a deadline (<yyyy-MM-dd HHmm>)",
                "-> event <desc> /from <start> /to <end>    : Add an event with a start and end time (<yyyy-MM-dd HHmm>)",
                "-> list                                    : Show all tasks",
                "-> mark <taskNumber>                       : Mark a task as done",
                "-> unmark <taskNumber>                     : Mark a task as not done",
                "-> delete <taskNumber>                     : Delete a task",
                "-> find <keyword>                          : Find tasks containing a keyword",
                "-> sort                                    : Sort tasks (Todos first, then Deadlines and Events in chronological order)",
                "-> bye                                     : Exit the program"
        );
    }

    /**
     * Displays a numbered list of items with a pretext message.
     *
     * @param list the list of items to display
     * @param pretext the header or description shown before the list
     */
    public String showList(List<String> list, String pretext) {
        List<String> lines = new ArrayList<>();
        lines.add(pretext);
        for (int i = 0; i < list.size(); i++) {
            lines.add((i + 1) + ". " + list.get(i));
        }
        return respond(lines.toArray(new String[0]));
    }

    /**
     * Displays a list of tasks with their original indexes preserved.
     * <p>
     * Each entry is printed with its original 1-based position in the master task list,
     * rather than being renumbered sequentially. This ensures consistency with commands
     * that operate by index (e.g., delete or mark).
     * </p>
     *
     * @param indexes a list of 0-based indexes corresponding to the given tasks
     * @param tasks   a list of task display strings; must be the same size as {@code indexes}
     * @param pretext a message to display above the list of tasks
     * @return a formatted string containing the pretext and numbered task list
     * @throws IllegalArgumentException if {@code indexes} and {@code tasks} differ in size,
     *                                  or if any index is negative
     */
    public String showTasksWithOriginalIndexes(
            List<Integer> indexes,
            List<String> tasks,
            String pretext
    ) {
        if (indexes.size() != tasks.size()) {
            throw new IllegalArgumentException("indexes and items must be the same length");
        }
        List<String> lines = new ArrayList<>();
        lines.add(pretext);

        for (int i = 0; i < tasks.size(); i++) {
            int taskIndex = indexes.get(i);
            if (taskIndex < 0) {
                throw new IllegalArgumentException("index must be > 0: " + taskIndex);
            }
            // Display as 1-based index to users
            lines.add((taskIndex + 1) + ". " + tasks.get(i));
        }
        return respond(lines.toArray(new String[0]));
    }

    /**
     * Displays a goodbye message and closes the input scanner.
     */
    public String showExit() {
        closeScanner();
        return(respond("Bye. Hope to see you again soon!"));
    }
}
