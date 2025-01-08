package de.unistuttgart.iste.ese.api.ToDo;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Data Transfer Object for ToDo entities.
 * Used for creating and updating ToDos.
 */
public class ToDoDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    private String description;

    private boolean finished;

    private Long dueDate;

    @NotNull(message = "Assignee list is required")
    private List<Long> assigneeIdList;

    private String category;

    // Constructors
    /**
     * Default constructor.
     */
    public ToDoDTO() {}

    /**
     * Creates a new ToDoDTO with the given details.
     *
     * @param title          The title of the ToDo
     * @param description    The description of the ToDo
     * @param finished       Whether the ToDo is finished
     * @param dueDate        The due date of the ToDo
     * @param assigneeIdList The list of Assignee IDs for this ToDo
     */
    public ToDoDTO(String title, String description, boolean finished, Long dueDate, List<Long> assigneeIdList) {
        this.title = title;
        this.description = description;
        this.finished = finished;
        this.dueDate = dueDate;
        this.assigneeIdList = assigneeIdList;
    }

    // Getters and Setters
    /**
     * Gets the title of the ToDo.
     *
     * @return The ToDo's title
     */
    public String getTitle() {
        return title;
    }
    /**
     * Sets the title of the ToDo.
     *
     * @param title The title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of the ToDo.
     *
     * @return The ToDo's description
     */
    public String getDescription() {
        return description;
    }
    /**
     * Sets the description of the ToDo.
     *
     * @param description The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Checks if the ToDo is finished.
     *
     * @return true if the ToDo is finished, false otherwise
     */
    public boolean isFinished() {
        return finished;
    }
    /**
     * Sets the finished status of the ToDo.
     *
     * @param finished The finished status to set
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * Gets the due date of the ToDo.
     *
     * @return The ToDo's due date
     */
    public Long getDueDate() {
        return dueDate;
    }
    /**
     * Sets the due date of the ToDo.
     *
     * @param dueDate The due date to set
     */
    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the list of Assignee IDs for this ToDo.
     *
     * @return The list of Assignee IDs
     */
    public List<Long> getAssigneeIdList() {
        return assigneeIdList;
    }
    /**
     * Sets the list of Assignee IDs for this ToDo.
     *
     * @param assigneeIdList The list of Assignee IDs to set
     */
    public void setAssigneeIdList(List<Long> assigneeIdList) {
        this.assigneeIdList = assigneeIdList;
    }

    /**
     * Gets the category of the ToDo.
     *
     * @return The ToDo's category
     */
    public String getCategory() {
        return category;
    }
    /**
     * Sets the category of the ToDo.
     *
     * @param category The category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }
}
