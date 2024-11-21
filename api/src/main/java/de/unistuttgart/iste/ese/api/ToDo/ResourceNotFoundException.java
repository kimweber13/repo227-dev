package de.unistuttgart.iste.ese.api.ToDo;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
