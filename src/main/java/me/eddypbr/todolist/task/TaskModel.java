package me.eddypbr.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;

  private String description;

  @Column(length = 50)
  private String title;

  private LocalDateTime startAt;

  private LocalDateTime endAt;

  private TaskPriorityEnum priority;

  public void setPriority(TaskPriorityEnum priority) throws Exception {
    if (priority == null) {
      throw new Exception("Invalid priority value");
    }
    
    this.priority = priority;
  }

  private UUID idUser;

  @CreationTimestamp
  private LocalDateTime CreatedAt;

  public void setTitle(String title) throws Exception {
    if (title.length() > 50) {
      throw new Exception("Title must be have less then 50 characters");
    }

    this.title = title;
  }

}
