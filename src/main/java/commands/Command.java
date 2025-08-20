package commands;

import errors.UnknownCommandException;

public enum Command {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    BYE("bye");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    // Factory method to parse user input into a CommandType
    public static Command fromString(String input) throws UnknownCommandException {
        for (Command cmd : values()) {
            if (cmd.keyword.equalsIgnoreCase(input)) {
                return cmd;
            }
        }
        throw new UnknownCommandException(input);
    }
}
