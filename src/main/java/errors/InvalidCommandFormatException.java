package errors;

public class InvalidCommandFormatException extends LogosException {
    public InvalidCommandFormatException(String command, String correctFormat) {
        super("It looks like you're trying to use the : '" + command + "' command. " +
                "Please follow this format: " + correctFormat);
    }
}
