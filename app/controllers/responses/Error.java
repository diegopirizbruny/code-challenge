package controllers.responses;

public class Error extends Response {

    public final String message;

    public Error(String message) {
        super(Status.ERROR);
        this.message = message;
    }


}
