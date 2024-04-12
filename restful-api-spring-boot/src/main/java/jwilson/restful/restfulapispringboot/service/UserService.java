package jwilson.restful.restfulapispringboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.RegisterUserRequest;
import jwilson.restful.restfulapispringboot.repository.UserRepository;
import jwilson.restful.restfulapispringboot.security.BCrypt;

@Service
public class UserService {
  
  // inject
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ValidationService validationService;

  @Transactional  // this annotation is used when you're manipulation database on your method
  public void register(RegisterUserRequest request) {
    validationService.validate(request);

    if (userRepository.existsById(request.getUsername())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
    user.setName(request.getName());

    userRepository.save(user);
  }

}
