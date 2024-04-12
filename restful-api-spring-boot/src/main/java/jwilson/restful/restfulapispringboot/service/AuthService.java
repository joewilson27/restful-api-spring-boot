package jwilson.restful.restfulapispringboot.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.LoginUserRequest;
import jwilson.restful.restfulapispringboot.model.TokenResponse;
import jwilson.restful.restfulapispringboot.repository.UserRepository;
import jwilson.restful.restfulapispringboot.security.BCrypt;
import jakarta.transaction.Transactional;

@Service
public class AuthService {
  
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ValidationService validationService;

  @Transactional // this annotation is used when you're manipulation database on your method
  public TokenResponse login(LoginUserRequest request) {
    validationService.validate(request);

    User user = userRepository.findById(request.getUsername())
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password is wrong"));

    // check request password matched with user in table
    if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
      // success, then create token
      user.setToken(UUID.randomUUID().toString());
      user.setTokenExpiredAt(next30Days());
      userRepository.save(user);

      return TokenResponse.builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();

    } else {
      // failed
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password is wrong");
    }
  }

  private Long next30Days() {
    return System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 30);
  }

}
