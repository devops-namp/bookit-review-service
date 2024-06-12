package uns.ac.rs.controlller.exception;

public class CantLeaveReview extends GenericException {
    public CantLeaveReview() {
        super("Can't leave review!");
    }

    @Override
    public int getErrorCode() {
        return 403;
    }
}
