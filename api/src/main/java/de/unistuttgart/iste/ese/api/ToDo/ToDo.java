package de.unistuttgart.iste.ese.api.ToDo;

import de.unistuttgart.iste.ese.api.Assignee.Assignee;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a ToDo item in the system.
 * A ToDo can be assigned to multiple Assignees.
 */
@Entity
@Table(name = "todos")
public class ToDo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String title;

    private String description;

    private boolean finished;

    @ManyToMany
    private List<Assignee> assigneeList = new ArrayList<>();

    private Long createdDate;

    private Long dueDate;

    private Long finishedDate;

    private String category;

    // Constructors
    /**
     * Default constructor for JPA.
     */
    public ToDo() {}

    /**
     * Creates a new ToDo with the given details.
     *
     * @param title       The title of the ToDo
     * @param description The description of the ToDo
     * @param finished    Whether the ToDo is finished
     * @param dueDate     The due date of the ToDo
     * @param assigneeList The list of Assignees for this ToDo
     */
    public ToDo(String title, String description, boolean finished, Long dueDate, List<Assignee> assigneeList) {
        this.title = title;
        this.description = description;
        this.finished = finished;
        this.dueDate = dueDate;
        this.assigneeList = assigneeList;
        this.createdDate = System.currentTimeMillis();
    }

    /**
     * Lifecycle method to set createdDate and finishedDate before persisting or updating.
     */
    @PrePersist
    @PreUpdate
    private void prePersistAndUpdate() {
        if (this.createdDate == null) {
            this.createdDate = System.currentTimeMillis();
        }
        if (this.finished && this.finishedDate == null) {
            this.finishedDate = System.currentTimeMillis();
        }
    }

    // Getters and Setters
    /**
     * Gets the ID of the ToDo.
     *
     * @return The ToDo's ID
     */
    public Long getId() { return id; }
    /**
     * Sets the ID of the ToDo.
     *
     * @param id The ID to set
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Gets the title of the ToDo.
     *
     * @return The ToDo's title
     */
    public String getTitle() { return title; }
    /**
     * Sets the title of the ToDo.
     *
     * @param title The title to set
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Gets the description of the ToDo.
     *
     * @return The ToDo's description
     */
    public String getDescription() { return description; }
    /**
     * Sets the description of the ToDo.
     *
     * @param description The description to set
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Checks if the ToDo is finished.
     *
     * @return true if the ToDo is finished, false otherwise
     */
    public boolean isFinished() { return finished; }
    /**
     * Sets the finished status of the ToDo.
     *
     * @param finished The finished status to set
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
        if (finished && this.finishedDate == null) {
            this.finishedDate = System.currentTimeMillis();
        }
    }

    /**
     * Gets the list of Assignees for this ToDo.
     *
     * @return The list of Assignees
     */
    public List<Assignee> getAssigneeList() { return assigneeList; }
    /**
     * Sets the list of Assignees for this ToDo.
     *
     * @param assigneeList The list of Assignees to set
     */
    public void setAssigneeList(List<Assignee> assigneeList) { this.assigneeList = assigneeList; }

    /**
     * Gets the created date of the ToDo.
     *
     * @return The ToDo's created date
     */
    public Long getCreatedDate() { return createdDate; }
    /**
     * Sets the created date of the ToDo.
     *
     * @param createdDate The created date to set
     */
    public void setCreatedDate(Long createdDate) { this.createdDate = createdDate; }

    /**
     * Gets the due date of the ToDo.
     *
     * @return The ToDo's due date
     */
    public Long getDueDate() { return dueDate; }
    /**
     * Sets the due date of the ToDo.
     *
     * @param dueDate The due date to set
     */
    public void setDueDate(Long dueDate) { this.dueDate = dueDate; }

    /**
     * Gets the finished date of the ToDo.
     *
     * @return The ToDo's finished date
     */
    public Long getFinishedDate() { return finishedDate; }
    /**
     * Sets the finished date of the ToDo.
     *
     * @param finishedDate The finished date to set
     */
    public void setFinishedDate(Long finishedDate) { if (this.finished) {
        this.finishedDate = finishedDate;
    } }


    /**
     * Gets the category of the ToDo.
     *
     * @return The category of the ToDo
     */
    public String getCategory() { return category; }
    /**
     * Sets the category of the ToDo.
     *
     * @param category The category to set
     */
    public void setCategory(String category) { this.category = category; }
}
