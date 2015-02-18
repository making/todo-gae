package todo.domain.repository.todo;

import todo.domain.model.Todo;

import java.util.Collection;

public interface TodoRepository {
    Todo findOne(String todoId);

    Collection<Todo> findAll();

    void save(Todo todo);

    void delete(Todo todo);

    long countByFinished(boolean finished);
}