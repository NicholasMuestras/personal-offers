package org.skypro.projects.personaloffers.exception;

public class WrongMessageParserException extends IllegalArgumentException {
    
    public WrongMessageParserException(String message) {
        super(message);
    }
    
    public WrongMessageParserException(String message, Throwable cause) {
        super(message, cause);
    }
}