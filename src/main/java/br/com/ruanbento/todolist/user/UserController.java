package br.com.ruanbento.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController implements UserDAO {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/createUser")
    @Override
    public ResponseEntity cadastrarUsuario(@RequestBody @NotNull UserModel userModel) {
        UserModel foundUser;

        // Busca usuário pelo e-mail (login)
        foundUser = this.userRepository.findByEmail(userModel.getEmail());

        // Verifica se o usuário já está cadastrado
        if(foundUser != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("E-mail já cadastrado!");
        }

        // Verifica se foi informada uma senha para criptografar
        if(userModel.getSenha() != null) {
            var senhaCriptografada = BCrypt.withDefaults().hashToString(12, userModel.getSenha().toCharArray());
            userModel.setSenha(senhaCriptografada);
        }

        // Salva usuário criado
        UserModel createdUser;
        createdUser = this.userRepository.saveAndFlush(userModel);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userModel);

    }
}
