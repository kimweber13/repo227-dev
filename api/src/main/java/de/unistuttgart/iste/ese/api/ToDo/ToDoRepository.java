package de.unistuttgart.iste.ese.api.ToDo;

import de.unistuttgart.iste.ese.api.Assignee.Assignee;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

/**
 * Repository interface for ToDo entity.
 * This interface provides CRUD operations for the ToDo entity using Spring Data JPA.
 */
public interface ToDoRepository extends CrudRepository<ToDo, Long> {
    /**
     * Retrieves all ToDo entities.
     *
     * @return a list of all ToDo entities
     */
    List<ToDo> findAll();

    /**
     * Finds all ToDo entities associated with a specific Assignee ID.
     *
     * @param assigneeId the ID of the Assignee
     * @return a list of ToDo entities associated with the given Assignee ID
     */
    List<ToDo> findByAssigneeListId(Long assigneeId);

    /**
     * Finds all ToDo entities that contain a specific Assignee in their assignee list.
     *
     * @param assignee the Assignee to search for
     * @return a list of ToDo entities that contain the given Assignee
     */
    List<ToDo> findAllByAssigneeListContaining(Assignee assignee);
}
