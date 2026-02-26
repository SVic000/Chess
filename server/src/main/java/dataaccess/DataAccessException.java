package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    public int statusCode;

    public DataAccessException(String message, int status) {
        super(message);
        this.statusCode = status;
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }
}
