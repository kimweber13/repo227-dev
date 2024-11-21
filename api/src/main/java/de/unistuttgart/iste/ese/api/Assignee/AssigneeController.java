package de.unistuttgart.iste.ese.api.Assignee;

import de.unistuttgart.iste.ese.api.ApiVersion1;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ApiVersion1
public class AssigneeController {

    @Autowired
    private AssigneeService assigneeService;

    @PostConstruct
    public void init() {
        assigneeService.initializeDefaultAssignees();
    }

    @GetMapping("/assignees")
    public ResponseEntity<List<Assignee>> getAssignees() {
        return ResponseEntity.ok(assigneeService.getAllAssignees());
    }

    @GetMapping("/assignees/{id}")
    public ResponseEntity<Assignee> getAssignee(@PathVariable("id") long id) {
        return ResponseEntity.ok(assigneeService.getAssigneeById(id));
    }

    @DeleteMapping("/assignees/{id}")
    public ResponseEntity<Assignee> deleteAssignee(@PathVariable("id") long id) {
        return ResponseEntity.ok(assigneeService.deleteAssignee(id));
    }

    @PostMapping("/assignees")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Assignee> createAssignee(@Valid @RequestBody Assignee requestBody) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(assigneeService.createAssignee(requestBody));
    }

    @PutMapping("/assignees/{id}")
    public ResponseEntity<Assignee> updateAssignee(@PathVariable("id") long id,
                                                   @Valid @RequestBody Assignee requestBody) {
        return ResponseEntity.ok(assigneeService.updateAssignee(id, requestBody));
    }
}
