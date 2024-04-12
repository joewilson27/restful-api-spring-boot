package jwilson.restful.restfulapispringboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

import jwilson.restful.restfulapispringboot.model.LoginUserRequest;
import jwilson.restful.restfulapispringboot.model.TokenResponse;
import jwilson.restful.restfulapispringboot.model.WebResponse;
import jwilson.restful.restfulapispringboot.service.AuthService;

@RestController
public class AuthController {
  
  @Autowired
  private AuthService authService;

  @PostMapping(
    path = "/api/auth/login",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
    TokenResponse tokenResponse = authService.login(request);
    return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
  }

}
