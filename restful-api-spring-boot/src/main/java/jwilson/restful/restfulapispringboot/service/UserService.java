package jwilson.restful.restfulapispringboot.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
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
  private Validator validator;

  @Transactional
  public void register(RegisterUserRequest request) {
    Set<ConstraintViolation<RegisterUserRequest>> constraintViolation = validator.validate(request);
    if (constraintViolation.size() != 0) {
      // error
      throw new ConstraintViolationException(constraintViolation);
    }

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
