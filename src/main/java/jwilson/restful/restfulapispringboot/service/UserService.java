package jwilson.restful.restfulapispringboot.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.RegisterUserRequest;
import jwilson.restful.restfulapispringboot.model.UpdateUserRequest;
import jwilson.restful.restfulapispringboot.model.UserResponse;
import jwilson.restful.restfulapispringboot.repository.UserRepository;
import jwilson.restful.restfulapispringboot.security.BCrypt;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

  public UserResponse get(User user) {
    return UserResponse.builder()
            .username(user.getUsername())
            .name(user.getName())
            .build();
  } 

  @Transactional
  public UserResponse update(User user, UpdateUserRequest request) {
    validationService.validate(request);

    log.info("REQUEST : {}", request);

    // check whether that requests are not null
    if (Objects.nonNull(request.getName())) {
      user.setName(request.getName());
    }

    if (Objects.nonNull(request.getPassword())) {
      user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
    }

    userRepository.save(user);

    log.info("USER getName() : {}", user.getName());

    return UserResponse.builder()
                      .name(user.getName())
                      .username(user.getUsername())
                      .build();
  }

}
