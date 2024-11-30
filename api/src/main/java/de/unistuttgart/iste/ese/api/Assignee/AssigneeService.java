package de.unistuttgart.iste.ese.api.Assignee;

import de.unistuttgart.iste.ese.api.ToDo.ToDo;
import de.unistuttgart.iste.ese.api.ToDo.ToDoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

/**
 * Service class for managing Assignee-related operations.
 */
@Service
public class AssigneeService {

    @Autowired
    private AssigneeRepository assigneeRepository;

    @Autowired
    private ToDoRepository toDoRepository;

    /**
     * Initializes default assignees if none exist in the database.
     */
    public void initializeDefaultAssignees() {
        long numberOfAssignees = assigneeRepository.count();
        if (numberOfAssignees == 0) {
            Assignee octocat = new Assignee("Octocatshevdevdev", "Paul", "paul@uni-stuttgart.de");
            assigneeRepository.save(octocat);

            Assignee grumpyAssignee = new Assignee("Grumpy Assignee", "John", "john@uni-stuttgart.de");
            assigneeRepository.save(grumpyAssignee);
        }
    }

    /**
     * Retrieves all assignees from the database.
     *
     * @return List of all Assignees
     */
    public List<Assignee> getAllAssignees() {
        List<Assignee> assignees = new ArrayList<>();
        assigneeRepository.findAll().forEach(assignees::add);
        return assignees;
    }

    /**
     * Retrieves a specific assignee by ID.
     *
     * @param id The ID of the assignee to retrieve
     * @return The requested Assignee
     * @throws ResponseStatusException if the assignee is not found
     */
    public Assignee getAssigneeById(Long id) {
        return assigneeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Assignee with ID %s not found!", id)));
    }

    /**
     * Retrieves multiple assignees by their IDs.
     *
     * @param ids List of assignee IDs to retrieve
     * @return List of found Assignees
     */
    public List<Assignee> getAssigneesByIds(List<Long> ids) {
        Iterable<Assignee> assigneeIterable = assigneeRepository.findAllById(ids);
        List<Assignee> assignees = new ArrayList<>();
        assigneeIterable.forEach(assignees::add);
        return assignees;
    }

    /**
     * Validates that all provided assignee IDs exist.
     *
     * @param ids List of assignee IDs to validate
     * @throws ResponseStatusException if any ID is invalid
     */
    public void validateAssigneeIds(List<Long> ids) {
        List<Assignee> foundAssignees = getAssigneesByIds(ids);
        if (foundAssignees.size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "One or more assignee IDs are invalid");
        }
    }

    /**
     * Creates a new assignee.
     *
     * @param requestBody The Assignee object to create
     * @return The created Assignee
     */
    public Assignee createAssignee(Assignee requestBody) {
        validateAssignee(requestBody);
        Assignee assignee = new Assignee(
            requestBody.getName(),
            requestBody.getPrename(),
            requestBody.getEmail()
        );
        return assigneeRepository.save(assignee);
    }

    /**
     * Updates an existing assignee.
     *
     * @param id The ID of the assignee to update
     * @param requestBody The updated Assignee object
     * @return The updated Assignee
     * @throws ResponseStatusException if the assignee is not found
     */
    public Assignee updateAssignee(Long id, Assignee requestBody) {
        validateAssignee(requestBody);
        Assignee existingAssignee = getAssigneeById(id);
        if (existingAssignee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Assignee with ID %s not found!", id));
        }
        requestBody.setId(id);
        return assigneeRepository.save(requestBody);
    }

    /**
     * Deletes an assignee and removes it from all associated ToDos.
     *
     * @param id The ID of the assignee to delete
     * @return The deleted Assignee
     * @throws ResponseStatusException if the assignee is not found
     */
    public Assignee deleteAssignee(Long id) {
        Assignee assigneeToDelete = getAssigneeById(id);
        if (assigneeToDelete == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Assignee with ID %s not found!", id));
        }

        List<ToDo> todos = toDoRepository.findAllByAssigneeListContaining(assigneeToDelete);
        for (ToDo todo : todos) {
            todo.getAssigneeList().remove(assigneeToDelete);
            toDoRepository.save(todo);
        }

        assigneeRepository.deleteById(id);
        return assigneeToDelete;
    }

    /**
     * Validates the assignee object.
     *
     * @param assignee The Assignee object to validate
     * @throws ResponseStatusException if validation fails
     */
    private void validateAssignee(Assignee assignee) {
        if (assignee.getName() == null || assignee.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be empty");
        }
        if (assignee.getPrename() == null || assignee.getPrename().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prename cannot be empty");
        }
        if (assignee.getEmail() == null || !assignee.getEmail().endsWith("uni-stuttgart.de")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Email must end with uni-stuttgart.de");
        }
    }
}
