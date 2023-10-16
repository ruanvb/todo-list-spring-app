package br.com.ruanbento.todolist.user;

import org.springframework.http.ResponseEntity;

public interface UserDAO {
    ResponseEntity cadastrarUsuario(UserModel user);
}
