package jwilson.restful.restfulapispringboot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.RegisterUserRequest;
import jwilson.restful.restfulapispringboot.model.WebResponse;
import jwilson.restful.restfulapispringboot.repository.UserRepository;
import jwilson.restful.restfulapispringboot.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach // delete data before run for testing
  void setup() {
    userRepository.deleteAll();
  }

  @Test
  void testRegisterSuccess() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("test");
    request.setPassword("secret");
    request.setName("Test");

    mockMvc.perform(
            post("/api/users")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
      ).andExpectAll(
        status().isOk()
      ).andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
          });

          assertEquals("OK", response.getData());
    });
  }

  @Test
  void testRegisterBadRequest() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest();
    // empty the column for testing
    request.setUsername("");
    request.setPassword("");
    request.setName("");

    mockMvc.perform(
            post("/api/users")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
      ).andExpectAll(
        status().isBadRequest()
      ).andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
          });

          assertNotNull(response.getErrors());
    });
  }

  @Test
  void testRegisterDuplicate() throws Exception {
    // create User first, then after this, we're goin to duplicate this data
    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
    user.setName("Test");
    userRepository.save(user);

    RegisterUserRequest request = new RegisterUserRequest();
    // empty the column for testing
    request.setUsername("test");
    request.setPassword("secret");
    request.setName("Test");

    mockMvc.perform(
            post("/api/users")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
      ).andExpectAll(
        status().isBadRequest()
      ).andDo(result -> {
          WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
          });

          assertNotNull(response.getErrors());
    });
  }
}
