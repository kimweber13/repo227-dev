package de.unistuttgart.iste.ese.api.Assignee;

import de.unistuttgart.iste.ese.api.ToDo.ToDo;
import de.unistuttgart.iste.ese.api.ToDo.ToDoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@Service
public class AssigneeService {

    @Autowired
    private AssigneeRepository assigneeRepository;

    @Autowired
    private ToDoRepository toDoRepository;

    public void initializeDefaultAssignees() {
        long numberOfAssignees = assigneeRepository.count();
        if (numberOfAssignees == 0) {
            Assignee octocat = new Assignee("Octocatshevdevdev", "Paul", "paul@uni-stuttgart.de");
            assigneeRepository.save(octocat);

            Assignee grumpyAssignee = new Assignee("Grumpy Assignee", "John", "john@uni-stuttgart.de");
            assigneeRepository.save(grumpyAssignee);
        }
    }

    public List<Assignee> getAllAssignees() {
        List<Assignee> assignees = new ArrayList<>();
        assigneeRepository.findAll().forEach(assignees::add);
        return assignees;
    }

    public Assignee getAssigneeById(Long id) {
        return assigneeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Assignee with ID %s not found!", id)));
    }

    public List<Assignee> getAssigneesByIds(List<Long> ids) {
        Iterable<Assignee> assigneeIterable = assigneeRepository.findAllById(ids);
        List<Assignee> assignees = new ArrayList<>();
        assigneeIterable.forEach(assignees::add);
        return assignees;
    }

    public void validateAssigneeIds(List<Long> ids) {
        List<Assignee> foundAssignees = getAssigneesByIds(ids);
        if (foundAssignees.size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "One or more assignee IDs are invalid");
        }
    }

    public Assignee createAssignee(Assignee requestBody) {
        validateAssignee(requestBody);
        Assignee assignee = new Assignee(
            requestBody.getName(),
            requestBody.getPrename(),
            requestBody.getEmail()
        );
        return assigneeRepository.save(assignee);
    }

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
