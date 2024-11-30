package de.unistuttgart.iste.ese.api.Assignee;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for Assignee entity.
 * This interface provides CRUD operations for the Assignee entity using Spring Data JPA.
 */
public interface AssigneeRepository extends CrudRepository<Assignee, Long> {
    /**
     * Finds an Assignee by their name.
     *
     * @param name the name of the Assignee to find
     * @return the Assignee with the given name, or null if not found
     */
    Assignee findByName(String name);

    /**
     * Finds an Assignee by their ID.
     *
     * @param id the ID of the Assignee to find
     * @return the Assignee with the given ID, or null if not found
     */
    Assignee findById(long id);
}
