package todo.app.todo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import todo.domain.model.Todo;
import todo.domain.service.todo.TodoService;

import javax.validation.groups.Default;
import java.util.Collection;

@Controller
@RequestMapping("todo")
public class TodoController {
    @Autowired
    TodoService todoService;

    @ModelAttribute
    public TodoForm setUpForm() {
        TodoForm form = new TodoForm();
        return form;
    }

    @RequestMapping(value = "list")
    public String list(Model model) {
        Collection<Todo> todos = todoService.findAll();
        model.addAttribute("todos", todos);
        return "todo/list";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(@Validated({Default.class, TodoForm.TodoCreate.class}) TodoForm todoForm,
                         BindingResult bindingResult, Model model, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            return list(model);
        }

        Todo todo = new Todo();
        BeanUtils.copyProperties(todoForm, todo);

        try {
            todoService.create(todo);
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return list(model);
        }

        attributes.addFlashAttribute("resultMessage", "Created successfully!");
        return "redirect:/todo/list";
    }

    @RequestMapping(value = "finish", method = RequestMethod.POST)
    public String finish(@Validated({Default.class, TodoForm.TodoFinish.class}) TodoForm form,
                         BindingResult bindingResult, Model model, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            return list(model);
        }

        try {
            todoService.finish(form.getTodoId());
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return list(model);
        }

        attributes.addFlashAttribute("resultMessage", "Finished successfully!");
        return "redirect:/todo/list";
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public String delete(
            @Validated({Default.class, TodoForm.TodoDelete.class}) TodoForm form,
            BindingResult bindingResult, Model model,
            RedirectAttributes attributes) {

        if (bindingResult.hasErrors()) {
            return list(model);
        }

        try {
            todoService.delete(form.getTodoId());
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return list(model);
        }

        attributes.addFlashAttribute("resultMessage", "Deleted successfully!");
        return "redirect:/todo/list";
    }
}
