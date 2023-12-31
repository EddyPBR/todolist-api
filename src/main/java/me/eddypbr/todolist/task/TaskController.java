package me.eddypbr.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import me.eddypbr.todolist.user.UserModel;
import me.eddypbr.todolist.utils.Utils;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @GetMapping
  public List<TaskModel> list(HttpServletRequest request) {
    UserModel user = (UserModel) request.getAttribute("user");

    var tasks = this.taskRepository.findByIdUser(user.getId());

    return tasks;
  }

  @PostMapping
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    UserModel user = (UserModel) request.getAttribute("user");

    taskModel.setIdUser((UUID) user.getId());

    var currentDate = LocalDateTime.now();

    if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("The start/end date must be greater than the current date");
    }

    if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The start date must be greater than end date");
    }

    var task = this.taskRepository.save(taskModel);

    return ResponseEntity.status(HttpStatus.OK).body(task);
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
    var task = this.taskRepository.findById(id).orElse(null);

    if (task == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
    }

    UserModel user = (UserModel) request.getAttribute("user");

    if (!task.getIdUser().equals(user.getId())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not authorized to update this task");
    }

    Utils.copyNonNullProperties(taskModel, task);

    var updatedTask = this.taskRepository.save(task);

    return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity delete(HttpServletRequest request, @PathVariable UUID id) {
    var task = this.taskRepository.findById(id).orElse(null);

    if (task == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
    }

    UserModel user = (UserModel) request.getAttribute("user");

    if (!task.getIdUser().equals(user.getId())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not authorized to update this task");
    }

    this.taskRepository.deleteById(task.getId());

    return ResponseEntity.status(HttpStatus.OK).body(task);
  }
}
