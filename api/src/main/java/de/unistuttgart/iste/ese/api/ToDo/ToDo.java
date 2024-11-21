package de.unistuttgart.iste.ese.api.ToDo;

import de.unistuttgart.iste.ese.api.Assignee.Assignee;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

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

    @NotNull
    private Long createdDate;

    @NotNull
    private Long dueDate;

    private Long finishedDate;

    public ToDo() {}

    public ToDo(String title, String description, boolean finished, Long dueDate, List<Assignee> assigneeList) {
        this.title = title;
        this.description = description;
        this.finished = finished;
        this.dueDate = dueDate;
        this.assigneeList = assigneeList;
        this.createdDate = System.currentTimeMillis();
    }

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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) {
        this.finished = finished;
        if (finished && this.finishedDate == null) {
            this.finishedDate = System.currentTimeMillis();
        }
    }

    public List<Assignee> getAssigneeList() { return assigneeList; }
    public void setAssigneeList(List<Assignee> assigneeList) { this.assigneeList = assigneeList; }

    public Long getCreatedDate() { return createdDate; }
    public void setCreatedDate(Long createdDate) { this.createdDate = createdDate; }

    public Long getDueDate() { return dueDate; }
    public void setDueDate(Long dueDate) { this.dueDate = dueDate; }

    public Long getFinishedDate() { return finishedDate; }
    public void setFinishedDate(Long finishedDate) { this.finishedDate = finishedDate; }
}
