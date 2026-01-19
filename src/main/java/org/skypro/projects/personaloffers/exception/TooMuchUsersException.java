package org.skypro.projects.personaloffers.exception;

public class TooMuchUsersException extends RuntimeException {
    
    public TooMuchUsersException() {
        super();
    }
    
    public TooMuchUsersException(String message) {
        super(message);
    }
    
    public TooMuchUsersException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public TooMuchUsersException(Throwable cause) {
        super(cause);
    }
}