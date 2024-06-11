package uns.ac.rs.controlller.exception;

public abstract class GenericException extends RuntimeException {
    public GenericException(String message) {
        super(message);
    }

    public abstract int getErrorCode();
}
