package ao.com.franciscolourenco.todolist.user;


import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {


    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity<?> creat(@RequestBody UserModel userModel){
        Optional<UserModel> userResult = this.userRepository.findByUsername(userModel.getUsername());
        if(userResult.isPresent()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");

        var passwordHashed  = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHashed);

        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

}

