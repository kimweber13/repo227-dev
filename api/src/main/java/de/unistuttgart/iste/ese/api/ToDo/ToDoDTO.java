package de.unistuttgart.iste.ese.api.ToDo;

import jakarta.validation.constraints.*;
import java.util.List;

public class ToDoDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    private String description;

    private boolean finished;

    @NotNull(message = "Due date is required")
    //@FutureOrPresent(message = "Due date must be in the present or future")
    private Long dueDate;

    @NotNull(message = "Assignee list is required")
    private List<Long> assigneeIdList;

    // Constructors
    public ToDoDTO() {}

    public ToDoDTO(String title, String description, boolean finished, Long dueDate, List<Long> assigneeIdList) {
        this.title = title;
        this.description = description;
        this.finished = finished;
        this.dueDate = dueDate;
        this.assigneeIdList = assigneeIdList;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public List<Long> getAssigneeIdList() {
        return assigneeIdList;
    }

    public void setAssigneeIdList(List<Long> assigneeIdList) {
        this.assigneeIdList = assigneeIdList;
    }
}
