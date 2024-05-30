package model.exceptions;

public class LastRemoveException extends Exception {
    public LastRemoveException() {
        super("At least one cosmetic product is in the warehouse.");
    }

}
