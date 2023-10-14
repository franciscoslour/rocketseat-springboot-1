package ao.com.franciscolourenco.todolist.filter;

import ao.com.franciscolourenco.todolist.user.IUserRepository;
import ao.com.franciscolourenco.todolist.user.UserModel;
import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String rota = request.getServletPath();
        if(rota.startsWith("/tasks/")){
            String authorization = request.getHeader("Authorization");
            String authEncoded = authorization.substring("Basic".length()).trim();
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
            String authString = new String(authDecoded);
            String[] user = authString.split(":");
            String username = user[0];
            String password = user[1];

            Optional<UserModel> resultado = this.userRepository.findByUsername(username);
            if(resultado.isPresent()){
                UserModel usuario = resultado.get();
                BCrypt.Result verifyer = BCrypt.verifyer().verify(password.toCharArray(), usuario.getPassword());
                if(verifyer.verified) {
                    request.setAttribute("idUser", usuario.getId());
                    filterChain.doFilter(request,response);
                }else{
                    response.sendError(401);
                }

            }else{
                response.sendError(401);
            }
        }else{
            filterChain.doFilter(request,response);
        }
    }

}
