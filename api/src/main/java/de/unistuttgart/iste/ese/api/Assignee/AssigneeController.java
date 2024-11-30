package de.unistuttgart.iste.ese.api.Assignee;

import de.unistuttgart.iste.ese.api.ApiVersion1;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Assignee entities.
 * Provides CRUD operations for Assignees.
 */
@RestController
@ApiVersion1
public class AssigneeController {

    @Autowired
    private AssigneeService assigneeService;

    /**
     * Initializes default assignees if none exist.
     */
    @PostConstruct
    public void init() {
        assigneeService.initializeDefaultAssignees();
    }

    /**
     * Retrieves all assignees.
     *
     * @return ResponseEntity containing a list of all Assignees
     */
    @GetMapping("/assignees")
    public ResponseEntity<List<Assignee>> getAssignees() {
        return ResponseEntity.ok(assigneeService.getAllAssignees());
    }

    /**
     * Retrieves a specific assignee by ID.
     *
     * @param id The ID of the assignee to retrieve
     * @return ResponseEntity containing the requested Assignee
     */
    @GetMapping("/assignees/{id}")
    public ResponseEntity<Assignee> getAssignee(@PathVariable("id") long id) {
        return ResponseEntity.ok(assigneeService.getAssigneeById(id));
    }

    /**
     * Deletes a specific assignee by ID.
     *
     * @param id The ID of the assignee to delete
     * @return ResponseEntity containing the deleted Assignee
     */
    @DeleteMapping("/assignees/{id}")
    public ResponseEntity<Assignee> deleteAssignee(@PathVariable("id") long id) {
        return ResponseEntity.ok(assigneeService.deleteAssignee(id));
    }

    /**
     * Creates a new assignee.
     *
     * @param requestBody The Assignee object to create
     * @return ResponseEntity containing the created Assignee
     */
    @PostMapping("/assignees")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Assignee> createAssignee(@Valid @RequestBody Assignee requestBody) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(assigneeService.createAssignee(requestBody));
    }

    /**
     * Updates an existing assignee.
     *
     * @param id The ID of the assignee to update
     * @param requestBody The updated Assignee object
     * @return ResponseEntity containing the updated Assignee
     */
    @PutMapping("/assignees/{id}")
    public ResponseEntity<Assignee> updateAssignee(@PathVariable("id") long id,
                                                   @Valid @RequestBody Assignee requestBody) {
        return ResponseEntity.ok(assigneeService.updateAssignee(id, requestBody));
    }
}
