package de.unistuttgart.iste.ese.api.ToDo;

import de.unistuttgart.iste.ese.api.Assignee.AssigneeService;
import de.unistuttgart.iste.ese.api.Assignee.Assignee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ToDoService {
    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private AssigneeService assigneeService;

    public ToDo createToDo(ToDoDTO request) {
        validateToDoRequest(request);
        ToDo todo = new ToDo();
        updateToDoFromRequest(todo, request);
        return toDoRepository.save(todo);
    }

    public ToDo updateToDo(Long id, ToDoDTO request) {
        ToDo todo = toDoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found"));
        validateToDoRequest(request);
        updateToDoFromRequest(todo, request);
        return toDoRepository.save(todo);
    }

    private void updateToDoFromRequest(ToDo todo, ToDoDTO request) {
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setFinished(request.isFinished());
        todo.setDueDate(request.getDueDate());
        if (request.getAssigneeIdList() != null) {
            List<Assignee> assignees = assigneeService.getAssigneesByIds(request.getAssigneeIdList());
            todo.setAssigneeList(assignees);
        } else {
            todo.setAssigneeList(new ArrayList<>());
        }
    }

    private void validateToDoRequest(ToDoDTO request) {
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        if (request.getDueDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Due date is required");
        }
        if (request.getAssigneeIdList() != null) {
            assigneeService.validateAssigneeIds(request.getAssigneeIdList());
        }
    }

    public List<ToDo> getAllToDos() {
        return toDoRepository.findAll();
    }

    public ToDo getToDoById(Long id) {
        return toDoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found"));
    }

    public void deleteToDo(Long id) {
        ToDo todo = getToDoById(id);
        if (!toDoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found");
        }
        toDoRepository.deleteById(id);
    }

    public List<ToDo> getToDosByAssigneeId(Long assigneeId) {
        return toDoRepository.findByAssigneeListId(assigneeId);
    }

    public void removeAssigneeFromAllTodos(Long assigneeId) {
        List<ToDo> todosWithAssignee = getToDosByAssigneeId(assigneeId);
        for (ToDo todo : todosWithAssignee) {
            todo.getAssigneeList().removeIf(assignee -> assignee.getId().equals(assigneeId));
            toDoRepository.save(todo);
        }
    }
}
