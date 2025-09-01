package commands;

import errors.UnknownCommandException;

public enum CommandType {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    BYE("bye"),
    FIND("find");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    /** 
     * Factory method to parse user input into a CommandType
     */ 
    public static CommandType fromString(String input) throws UnknownCommandException {
        for (CommandType cmd : values()) {
            if (cmd.keyword.equalsIgnoreCase(input)) {
                return cmd;
            }
        }
        throw new UnknownCommandException(input);
    }
}
