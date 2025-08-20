package errors;

public class UnknownCommandException extends LogosException {
    public UnknownCommandException(String command) {
        super("Sorry, I don't recognise the command: '" + command + "'");
    }
}
