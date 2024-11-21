package de.unistuttgart.iste.ese.api.ToDo;

import de.unistuttgart.iste.ese.api.Assignee.Assignee;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ToDoRepository extends CrudRepository<ToDo, Long> {
    List<ToDo> findAll();
    List<ToDo> findByAssigneeListId(Long assigneeId);

    List<ToDo> findAllByAssigneeListContaining(Assignee assignee);
}
