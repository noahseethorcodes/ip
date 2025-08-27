package ui;

import java.util.List;
import java.util.Scanner;

public class Ui {
    private final int INDENT_LENGTH = 4;
    private final int LINE_LENGTH = 80;
    private final Scanner sc = new Scanner(System.in);;

    public String readLine() {
        String userInput = sc.nextLine();
        return userInput;
    };

    private void closeScanner() {
        sc.close();
    };

    // Helper Function that indents text and wraps with horizontal lines
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

    public void showWelcome(String chatbotLogo, String chatbotName) {
        System.out.println("Welcome to...\n" + chatbotLogo);
        this.respond(
                "Hello! I'm " + chatbotName + " :) Your friendly terminal task manager.",
                "",
                "Here are some commands you can try:",
                "-> todo <desc>                             : Add a simple task",
                "-> deadline <desc> /by <time>              : Add a task with a deadline (<yyyy-MM-dd HHmm>)",
                "-> event <desc> /from <start> /to <end>    : Add an event with a start and end time (<yyyy-MM-dd HHmm>)",
                "-> list                                    : Show all tasks",
                "-> mark <taskNumber>                       : Mark a task as done",
                "-> unmark <taskNumber>                     : Mark a task as not done",
                "-> bye                                     : Exit the program");
    };

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

    public void showExit() {
        closeScanner();
        respond("Bye. Hope to see you again soon!");
    }
}
