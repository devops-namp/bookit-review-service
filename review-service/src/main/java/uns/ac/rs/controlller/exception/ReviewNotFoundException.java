package uns.ac.rs.controlller.exception;

public class ReviewNotFoundException extends GenericException {
    public ReviewNotFoundException() {
        super("Review does not exist");
    }

    @Override
    public int getErrorCode() {
        return 404;
    }
}
