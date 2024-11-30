package de.unistuttgart.iste.ese.api.Assignee;

import de.unistuttgart.iste.ese.api.ToDo.ToDo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an assignee in the ToDo management system.
 * An assignee can be assigned to multiple ToDos.
 */
@Entity
@Table(name = "assignees")
public class Assignee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 40)
    private String prename;

    @NotNull
    @Size(min = 1, max = 40)
    private String name;

    @Email(message = "Email should be valid")
    @Pattern(regexp = ".+uni-stuttgart.de$", message = "Email must end with uni-stuttgart.de")
    private String email;

    @ManyToMany(mappedBy = "assigneeList")
    private List<ToDo> todos = new ArrayList<>();

    // Constructors
    /**
     * Default constructor for JPA.
     */
    public Assignee() {}

    /**
     * Creates a new Assignee with the given details.
     *
     * @param name    The last name of the assignee
     * @param prename The first name of the assignee
     * @param email   The email address of the assignee (must end with uni-stuttgart.de)
     */
    public Assignee(String name, String prename, String email) {
        this.name = name;
        this.prename = prename;
        this.email = email;
    }

    // Getters and Setters
    /**
     * Gets the ID of the assignee.
     *
     * @return The assignee's ID
     */
    public Long getId() {
        return id;
    }
    /**
     * Sets the ID of the assignee.
     *
     * @param id The ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the last name of the assignee.
     *
     * @return The assignee's last name
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the last name of the assignee.
     *
     * @param name The last name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the first name of the assignee.
     *
     * @return The assignee's first name
     */
    public String getPrename() {
        return prename;
    }
    /**
     * Sets the first name of the assignee.
     *
     * @param prename The first name to set
     */
    public void setPrename(String prename) {
        this.prename = prename;
    }

    /**
     * Gets the email address of the assignee.
     *
     * @return The assignee's email address
     */
    public String getEmail() {
        return email;
    }
    /**
     * Sets the email address of the assignee.
     *
     * @param email The email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
