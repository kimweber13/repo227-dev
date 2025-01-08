package de.unistuttgart.iste.ese.api.ToDo;

import de.unistuttgart.iste.ese.api.ApiVersion1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST Controller for managing ToDo entities.
 * Provides CRUD operations for ToDos and CSV export functionality.
 */
@RestController
@ApiVersion1
public class ToDoController {

    @Autowired
    private ToDoService toDoService;

    /**
     * Retrieves all ToDos.
     *
     * @return ResponseEntity containing a list of all ToDos
     */
    @GetMapping("/todos")
    public ResponseEntity<List<ToDo>> getAllToDos() {
        List<ToDo> todos = toDoService.getAllToDos();
        return ResponseEntity.ok(todos);
    }

    /**
     * Retrieves a specific ToDo by ID.
     *
     * @param id The ID of the ToDo to retrieve
     * @return ResponseEntity containing the requested ToDo
     * @throws ResourceNotFoundException if the ToDo is not found
     */
    @GetMapping("/todos/{id}")
    public ResponseEntity<ToDo> getToDoById(@PathVariable Long id) {
        ToDo todo = toDoService.getToDoById(id);
        return ResponseEntity.ok(todo);
    }

    /**
     * Creates a new ToDo.
     *
     * @param dto The ToDoDTO object containing the new ToDo details
     * @return ResponseEntity containing the created ToDo
     * @throws IllegalArgumentException if the request is invalid
     */
    @PostMapping("/todos")
    public ResponseEntity<ToDo> createToDo(@Valid @RequestBody ToDoDTO dto) {
        ToDo createdTodo = toDoService.createToDo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    /**
     * Updates an existing ToDo.
     *
     * @param id The ID of the ToDo to update
     * @param dto The ToDoDTO object containing the updated ToDo details
     * @return ResponseEntity containing the updated ToDo
     * @throws ResourceNotFoundException if the ToDo is not found
     * @throws IllegalArgumentException if the request is invalid
     */
    @PutMapping("/todos/{id}")
    public ResponseEntity<ToDo> updateToDo(@PathVariable Long id, @Valid @RequestBody ToDoDTO dto) {
        ToDo updatedTodo = toDoService.updateToDo(id, dto);
        return ResponseEntity.ok(updatedTodo);
    }

    /**
     * Deletes a specific ToDo by ID.
     *
     * @param id The ID of the ToDo to delete
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> deleteToDo(@PathVariable Long id) {
        toDoService.deleteToDo(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Exports all ToDos to a CSV file.
     *
     * This method sets the response content type and header for CSV download,
     * and delegates the actual export logic to the ToDoService.
     *
     * @param response The HttpServletResponse to write the CSV data to
     * @return ResponseEntity with an HTTP status of OK
     */
    @GetMapping(value = "/csv-downloads/todos", produces = "text/csv;charset=UTF-8")
    public ResponseEntity<Void> exportToCsv (HttpServletResponse response){
        toDoService.exportToCsv(response);
        return ResponseEntity.ok().build();
    }

    /**
     * Handles ResourceNotFoundException and returns a 404 status.
     *
     * @param ex The ResourceNotFoundException that was thrown
     * @return ResponseEntity with NOT_FOUND status and error message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles IllegalArgumentException and returns a 400 status.
     *
     * @param ex The IllegalArgumentException that was thrown
     * @return ResponseEntity with BAD_REQUEST status and error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
