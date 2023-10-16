package br.com.ruanbento.todolist.task;

import br.com.ruanbento.todolist.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController implements TaskDAO {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    @PostMapping("/")
    public ResponseEntity createTask(@RequestBody @NotNull TaskModel taskModel, HttpServletRequest httpServletRequest) {

        // Verifica quantidade máxima de caracteres do título
        if(taskModel.getTitulo().length() > 50){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O título não poderá ultrapassar 50 caracteres.");
        }

        // Verifica campo obrigatório - Título
        if(taskModel.getTitulo().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O título é obrigatório.");
        }

        // Valida data de início > data término
        if(taskModel.getDataInicio().isAfter(taskModel.getDataTermino())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início precisa anteceder a data de término.");
        }

        // Valida data início & data término < data atual
        if(taskModel.getDataInicio().isBefore(LocalDateTime.now()) || taskModel.getDataTermino().isBefore(LocalDateTime.now())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início e data de término precisam ser maior que a data atual.");
        }

        // Obtém ID do usuário vinculado à tarefa
        taskModel.setIdUser((UUID) httpServletRequest.getAttribute("idUser"));

        // Salva a tarefa no banco de dados
        TaskModel task;
        task = this.taskRepository.saveAndFlush(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);

    }

    @Override
    @GetMapping("/")
    public List<TaskModel> listTasks(@RequestBody TaskModel taskModel, @NotNull HttpServletRequest httpServletRequest) {
        // Lista todas as tarefas do usuário
        return this.taskRepository.findByIdUser((UUID) httpServletRequest.getAttribute("idUser"));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity udpateTask(@RequestBody TaskModel taskModel, @NotNull HttpServletRequest httpServletRequest, @PathVariable UUID id) {

        //Obtém id do usuário dono da tarefa
        var idUser = httpServletRequest.getAttribute("idUser");

        // Buca tarefa pelo id informado na url do request
        TaskModel task = this.taskRepository.findById(id).orElse(null);

        // Valida se a tarefa existe
        if (task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada.");
        }

        // Valida se o usuário logado tem permissão para alterar a tarefa
        if(!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Você não possui permissão para alterar essa tarefa.");
        }

        // Verifica campos informados no payload da requisição e altera somente os campos informados
        Utils.copyNonNullProperties(taskModel, task);
        this.taskRepository.saveAndFlush(task);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }
}
