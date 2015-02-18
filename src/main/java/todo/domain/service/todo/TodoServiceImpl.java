package todo.domain.service.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import todo.domain.model.Todo;
import todo.domain.repository.todo.TodoRepository;

import java.util.Collection;
import java.util.Date;

@Service
public class TodoServiceImpl implements TodoService {

    private static final long MAX_UNFINISHED_COUNT = 5;

    @Autowired
    TodoRepository todoRepository;

    public Todo findOne(String todoId) {
        Todo todo = todoRepository.findOne(todoId);
        if (todo == null) {
            throw new IllegalArgumentException("[E404] The requested Todo is not found. (id="
                    + todoId + ")");
        }
        return todo;
    }

    @Override
    public Collection<Todo> findAll() {
        return todoRepository.findAll();
    }

    @Override
    public Todo create(Todo todo) {
        long unfinishedCount = todoRepository.countByFinished(false);
        if (unfinishedCount >= MAX_UNFINISHED_COUNT) {
            throw new IllegalStateException("[E001] The count of un-finished Todo must not be over "
                    + MAX_UNFINISHED_COUNT + ".");
        }

        Date createdAt = new Date();
        todo.setCreatedAt(createdAt);
        todo.setFinished(false);

        todoRepository.save(todo);
        return todo;
    }

    @Override
    public Todo finish(String todoId) {
        Todo todo = findOne(todoId);
        if (todo.isFinished()) {
            throw new IllegalStateException("[E002] The requested Todo is already finished. (id="
                    + todoId + ")");
        }
        todo.setFinished(true);
        todoRepository.save(todo);
        return todo;
    }

    @Override
    public void delete(String todoId) {
        Todo todo = findOne(todoId);
        todoRepository.delete(todo);
    }
}