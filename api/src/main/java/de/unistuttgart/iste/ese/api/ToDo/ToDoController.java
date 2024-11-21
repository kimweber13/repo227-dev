package de.unistuttgart.iste.ese.api.ToDo;

import de.unistuttgart.iste.ese.api.ApiVersion1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@ApiVersion1
public class ToDoController {

    @Autowired
    private ToDoService toDoService;

    @GetMapping("/todos")
    public ResponseEntity<List<ToDo>> getAllToDos() {
        List<ToDo> todos = toDoService.getAllToDos();
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<ToDo> getToDoById(@PathVariable Long id) {
        try {
            ToDo todo = toDoService.getToDoById(id);
            return ResponseEntity.ok(todo);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/todos")
    public ResponseEntity<ToDo> createToDo(@Valid @RequestBody ToDoDTO request) {
        try {
            ToDo createdTodo = toDoService.createToDo(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/todos/{id}")
    public ResponseEntity<ToDo> updateToDo(@PathVariable Long id, @Valid @RequestBody ToDoDTO request) {
        try {
            ToDo updatedTodo = toDoService.updateToDo(id, request);
            return ResponseEntity.ok(updatedTodo);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/todos/{id}")
    public void deleteToDo(@PathVariable Long id) {

        toDoService.deleteToDo(id);

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
