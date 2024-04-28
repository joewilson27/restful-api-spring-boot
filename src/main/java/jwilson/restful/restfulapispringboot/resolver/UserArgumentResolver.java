package jwilson.restful.restfulapispringboot.resolver;

// this class is for handling get User API 

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;
import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

  @Autowired
  private UserRepository userRepository;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return User.class.equals(parameter.getParameterType());
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    
      HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
      String token = servletRequest.getHeader("X-API-TOKEN");
      log.info("TOKEN {}", token);
      if (token == null) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
      }

      User user = userRepository.findFirstByToken(token)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
      log.info("USER {}", user);
      log.info("USER name {}", user.getName());
      log.info("USER username {}", user.getUsername());
                
      // throw error if token is expired
      log.info("getTokenExpiredAt {}", user.getTokenExpiredAt());
      log.info("currentTimeMillis {}", System.currentTimeMillis());
      if (user.getTokenExpiredAt() < System.currentTimeMillis()) {
        log.info("catch err expired");
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
      }

      log.info("send this username {}", user.getUsername());
      return user;
  }

  /**
   * From here, we then add this class to configuration webmvc. we put them in our custom webconfiguration class
  */
}
