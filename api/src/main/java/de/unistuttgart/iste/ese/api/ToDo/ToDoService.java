package de.unistuttgart.iste.ese.api.ToDo;

import de.unistuttgart.iste.ese.api.Assignee.AssigneeService;
import de.unistuttgart.iste.ese.api.Assignee.Assignee;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * Exports all ToDos to a CSV file.
     *
     * This method sets the response content type and header for CSV file download.
     * It retrieves all ToDos, formats the data, and writes it to the response output stream in CSV format.
     * If an IOException occurs during the process, it throws a RuntimeException.
     *
     * @param response The HttpServletResponse to write the CSV data to
     */
    public void exportToCsv(HttpServletResponse response) {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"todos.csv\"");

        List<ToDo> todos = getAllToDos();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("id", "title", "description", "finished", "assignees", "createdDate", "dueDate", "finishedDate", "category"))) {

            for (ToDo todo : todos) {
                csvPrinter.printRecord(
                    todo.getId(),
                    todo.getTitle(),
                    todo.getDescription(),
                    todo.isFinished() ? "TRUE" : "FALSE",
                    String.join("+", todo.getAssigneeList().stream()
                        .map(assignee -> assignee.getPrename() + " " + assignee.getName())
                        .collect(Collectors.toList())),
                    formatDate(todo.getCreatedDate(), formatter),
                    formatDate(todo.getDueDate(), formatter),
                    formatDate(todo.getFinishedDate(), formatter),
                    todo.getCategory()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Error exporting ToDos to CSV", e);
        }
    }

    /**
     * Formats a timestamp into a date string.
     *
     * This method converts a given timestamp (in milliseconds) to a formatted date string
     * using the provided DateTimeFormatter. If the timestamp is null, it returns an empty string.
     *
     * @param timestamp The timestamp in milliseconds to format
     * @param formatter The DateTimeFormatter to use for formatting the date
     * @return The formatted date string, or an empty string if the timestamp is null
     */
    private String formatDate(Long timestamp, DateTimeFormatter formatter) {
        if (timestamp == null) {
            return "";
        }
        return LocalDate.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).format(formatter);
    }
}
