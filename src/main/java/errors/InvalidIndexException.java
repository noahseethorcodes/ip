package errors;

public class InvalidIndexException extends LogosException {
    public InvalidIndexException(int index) {
        super(String.format("There is no task at index %d. Did you mean to input another number?", index));
    }
}
