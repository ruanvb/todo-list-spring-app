package br.com.ruanbento.todolist.task;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

public interface TaskDAO {
    ResponseEntity createTask(TaskModel task, HttpServletRequest httpServletRequest);
    List<TaskModel> listTasks(TaskModel task, HttpServletRequest httpServletRequest);
    ResponseEntity udpateTask(TaskModel task, HttpServletRequest httpServletRequest, @PathVariable UUID id);
}
