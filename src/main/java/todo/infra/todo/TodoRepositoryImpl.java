package todo.infra.todo;

import com.google.appengine.api.datastore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import todo.domain.model.Todo;
import todo.domain.repository.todo.TodoRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public class TodoRepositoryImpl implements TodoRepository {
    @Autowired
    DatastoreService datastoreService;

    @Override
    public Todo findOne(String todoId) {
        Key key = KeyFactory.stringToKey(todoId);
        try {
            Entity entity = datastoreService.get(key);
            return fromEntity(entity);
        } catch (EntityNotFoundException ignored) {
            return null;
        }
    }


    @Override
    public Collection<Todo> findAll() {
        Query query = new Query("Todo").addSort("createdAt", Query.SortDirection.DESCENDING);
        List<Todo> todos = new ArrayList<>();
        for (Entity entity : datastoreService.prepare(query).asIterable()) {
            todos.add(fromEntity(entity));
        }
        return todos;
    }

    @Override
    public void save(Todo todo) {
        Entity entity = toEntity(todo);
        datastoreService.put(entity);
        todo.setTodoId(KeyFactory.keyToString(entity.getKey()));
    }

    @Override
    public void delete(Todo todo) {
        Key key = KeyFactory.stringToKey(todo.getTodoId());
        datastoreService.delete(key);
    }

    @Override
    public long countByFinished(boolean finished) {
        Query query = new Query("Todo")
                .setFilter(new Query.FilterPredicate("isFinished", Query.FilterOperator.EQUAL, finished));
        return datastoreService.prepare(query).countEntities(FetchOptions.Builder.withDefaults());
    }

    Todo fromEntity(Entity entity) {
        return new Todo(KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty("todoTitle"),
                (Boolean) entity.getProperty("isFinished"),
                (Date) entity.getProperty("createdAt"));
    }

    Entity toEntity(Todo todo) {
        Entity entity;
        if (todo.getTodoId() == null) {
            entity = new Entity("Todo");
        } else {
            Key key = KeyFactory.stringToKey(todo.getTodoId());
            entity = new Entity("Todo", key.getId());
        }
        entity.setProperty("todoTitle", todo.getTodoTitle());
        entity.setProperty("isFinished", todo.isFinished());
        entity.setProperty("createdAt", todo.getCreatedAt());
        return entity;
    }
}
