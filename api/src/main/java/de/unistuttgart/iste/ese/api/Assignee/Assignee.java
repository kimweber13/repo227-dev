package de.unistuttgart.iste.ese.api.Assignee;

import de.unistuttgart.iste.ese.api.ToDo.ToDo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

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
    public Assignee() {}

    public Assignee(String name, String prename, String email) {
        this.name = name;
        this.prename = prename;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
