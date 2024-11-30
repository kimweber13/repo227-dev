package de.unistuttgart.iste.ese.api.ToDo;

/**
 * Exception class for representing resource not found errors.
 * This exception is thrown when a requested resource (e.g., ToDo or Assignee) is not found in the system.
 */
public class ResourceNotFoundException extends RuntimeException{
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
