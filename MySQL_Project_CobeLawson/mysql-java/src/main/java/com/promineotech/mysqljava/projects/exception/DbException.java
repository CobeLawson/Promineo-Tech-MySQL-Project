package com.promineotech.mysqljava.projects.exception;

//Extending RuntimeException Class
public class DbException extends RuntimeException {
    
    //Override methods that call the same method from the SuperClass
    public DbException (String message) {
        super(message);
    } 

    public DbException (Throwable cause) {
        super(cause);
    }

    public DbException (String message, Throwable cause) {
        super(message, cause);
    }
}
