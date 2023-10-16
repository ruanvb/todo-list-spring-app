package br.com.ruanbento.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.ruanbento.todolist.task.TaskRepository;
import br.com.ruanbento.todolist.user.UserModel;
import br.com.ruanbento.todolist.user.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, @NotNull FilterChain filterChain) throws ServletException, IOException {

        if(!httpServletRequest.getServletPath().equals("/users/createUser")) {

            // Obtém a autenticação (email e senha)
            var authorization = httpServletRequest.getHeader("Authorization");
            var userPass = authorization.substring("Basic".length()).trim();
            byte[] bytePass = Base64.getDecoder().decode(userPass);
            var passDecoded = new String(bytePass);

            String[] credentials = passDecoded.split(":");

            String email = credentials[0];
            String pass = credentials[1];

            // Validar usuário
            UserModel foundUser;
            foundUser = this.userRepository.findByEmail(email);

            if (foundUser == null) {
                httpServletResponse.sendError(401);
            } else {
                // Validar a senha
                var passwordVerify = BCrypt.verifyer().verify(pass.toCharArray(), foundUser.getSenha().toCharArray());
                if (passwordVerify.verified) {
                    httpServletRequest.setAttribute("idUser", foundUser.getId());
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                } else {
                    httpServletResponse.sendError(401);
                }
            }

        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
}
