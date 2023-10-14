package ao.com.franciscolourenco.todolist.task;

import ao.com.franciscolourenco.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request){


        LocalDateTime currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("A data de início/termino deve ser maior ou igual a data actual");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("A data de início deve ser inferior a data de termino");
        }

        UUID idUser = UUID.fromString(request.getAttribute("idUser").toString());
        taskModel.setIdUser(idUser);
        TaskModel taskCreated = this.taskRepository.save(taskModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskCreated);
    }


    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        UUID idUser = UUID.fromString(request.getAttribute("idUser").toString());
        List<TaskModel> tasks = this.taskRepository.findByIdUser(idUser);
        return tasks;
    }

    @PutMapping("/{id}")
    public TaskModel update(@RequestBody TaskModel taskModel,
                       @PathVariable UUID id,
                       HttpServletRequest request){

        var task = this.taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));


        Utils.copyNonNullProperties(taskModel, task);
        UUID idUser = UUID.fromString(request.getAttribute("idUser").toString());
        task.setIdUser(idUser);

        return this.taskRepository.save(task);

    }

}

