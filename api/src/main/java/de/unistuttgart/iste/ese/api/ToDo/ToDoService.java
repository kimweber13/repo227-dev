package de.unistuttgart.iste.ese.api.ToDo;

import de.unistuttgart.iste.ese.api.Assignee.AssigneeService;
import de.unistuttgart.iste.ese.api.Assignee.Assignee;
import de.unistuttgart.iste.ese.api.TodoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing ToDo-related operations.
 */
@Service
public class ToDoService {
    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private AssigneeService assigneeService;

    @Autowired
    private TodoModel todoModel;

    /**
     * Creates a new ToDo based on the provided DTO.
     *
     * @param dto The DTO containing ToDo details
     * @return The created ToDo entity
     * @throws IllegalArgumentException if the request is invalid
     */
    public ToDo createToDo(ToDoDTO dto) {
        validateToDoDTO(dto);
        ToDo todo = new ToDo();
        updateToDoFromDTO(todo, dto);

        try {
            String category = todoModel.predictClass(dto.getTitle());
            todo.setCategory(category);
        } catch (Exception e) {
            // Setzen Sie einen Standardwert, wenn die Kategorisierung fehlschlägt
            todo.setCategory("work");
        }
        return toDoRepository.save(todo);
    }

    /**
     * Updates an existing ToDo based on the provided DTO.
     *
     * @param id The ID of the ToDo to update
     * @param dto The DTO containing updated ToDo details
     * @return The updated ToDo entity
     * @throws ResponseStatusException if the ToDo is not found
     * @throws IllegalArgumentException if the request is invalid
     */
    public ToDo updateToDo(Long id, ToDoDTO dto) {
        ToDo todo = toDoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found"));
        validateToDoDTO(dto);
        updateToDoFromDTO(todo, dto);

        try {
            String category = todoModel.predictClass(dto.getTitle());
            todo.setCategory(category);
        } catch (Exception e) {
            // Behalte die existierende Kategorie bei, wenn die Vorhersage fehlschlägt
            if (todo.getCategory() == null) {
                todo.setCategory("work");
            }
        }
        return toDoRepository.save(todo);
    }

    /**
     * Updates a ToDo entity with data from a ToDoDTO.
     *
     * @param todo The ToDo entity to update
     * @param dto The DTO containing the new data
     */
    private void updateToDoFromDTO(ToDo todo, ToDoDTO dto) {
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setFinished(dto.isFinished());
        todo.setDueDate(dto.getDueDate());
        if (dto.getAssigneeIdList() != null) {
            List<Assignee> assignees = assigneeService.getAssigneesByIds(dto.getAssigneeIdList());
            todo.setAssigneeList(assignees);
        } else {
            todo.setAssigneeList(new ArrayList<>());
        }
    }

    /**
     * Validates a ToDoDTO.
     *
     * @param dto The DTO to validate
     * @throws ResponseStatusException if the DTO is invalid
     */
    private void validateToDoDTO(ToDoDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        if (dto.getDueDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Due date is required");
        }
        if (dto.getAssigneeIdList() != null) {
            assigneeService.validateAssigneeIds(dto.getAssigneeIdList());
        }
    }

    /**
     * Retrieves all ToDos.
     *
     * @return List of all ToDos
     */
    public List<ToDo> getAllToDos() {
        return toDoRepository.findAll();
    }

    /**
     * Retrieves a specific ToDo by ID.
     *
     * @param id The ID of the ToDo to retrieve
     * @return The requested ToDo
     * @throws ResponseStatusException if the ToDo is not found
     */
    public ToDo getToDoById(Long id) {
        return toDoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found"));
    }

    /**
     * Deletes a specific ToDo by ID.
     *
     * @param id The ID of the ToDo to delete
     * @throws ResponseStatusException if the ToDo is not found
     */
    public void deleteToDo(Long id) {
        ToDo todo = getToDoById(id);
        if (!toDoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found");
        }
        toDoRepository.deleteById(id);
    }

    /**
     * Retrieves all ToDos associated with a specific Assignee.
     *
     * @param assigneeId The ID of the Assignee
     * @return List of ToDos associated with the Assignee
     */
    public List<ToDo> getToDosByAssigneeId(Long assigneeId) {
        return toDoRepository.findByAssigneeListId(assigneeId);
    }

    /**
     * Removes an Assignee from all associated ToDos.
     *
     * @param assigneeId The ID of the Assignee to remove
     */
    public void removeAssigneeFromAllTodos(Long assigneeId) {
        List<ToDo> todosWithAssignee = getToDosByAssigneeId(assigneeId);
        for (ToDo todo : todosWithAssignee) {
            todo.getAssigneeList().removeIf(assignee -> assignee.getId().equals(assigneeId));
            toDoRepository.save(todo);
        }
    }
}
