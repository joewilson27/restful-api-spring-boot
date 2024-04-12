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
import jwilson.restful.restfulapispringboot.model.UpdateUserRequest;
import jwilson.restful.restfulapispringboot.model.UserResponse;
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

  @Test
  void getUserUnauthorized() throws Exception {
    mockMvc.perform(
      get("/api/users/current")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "notfound") // for error purpose. then added false token
    ).andExpectAll(
      status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getUserUnauthorizedTokenNotSend() throws Exception {
    // here, we test request get users without token
    mockMvc.perform(
      get("/api/users/current")
        .accept(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertNotNull(response.getErrors());
    });
  }

  @Test
  void getUserSuccess() throws Exception {
    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
    user.setName("Test");
    user.setToken("tokentest"); // example token we created earlier
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
    userRepository.save(user);

    mockMvc.perform(
      get("/api/users/current")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "tokentest")
        
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
        WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        // make sure no error
        assertNull(response.getErrors());
        // make sure response and example data are equals 
        assertEquals("test", response.getData().getUsername());
        assertEquals("Test", response.getData().getName());
    });
  }

  @Test
  void getUserTokenExpired() throws Exception {
    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
    user.setName("Test");
    user.setToken("tokentest"); // example token we created earlier
    user.setTokenExpiredAt(System.currentTimeMillis() - 10000000); // and fill expired date for this user
    userRepository.save(user);

    mockMvc.perform(
      get("/api/users/current")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "tokentest")
        
    ).andExpectAll(
      status().isUnauthorized()
    ).andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertNotNull(response.getErrors());
    });
  }

  @Test
  void updateUserUnauthorized() throws Exception {
    UpdateUserRequest request = new UpdateUserRequest();

    mockMvc.perform(
      patch("/api/users/current")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
      status().isUnauthorized()
    ).andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void updateUserSuccess() throws Exception {
    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
    user.setName("Test");
    user.setToken("tokentest"); // example token we created earlier
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
    userRepository.save(user);
    
    UpdateUserRequest request = new UpdateUserRequest();
    request.setName("TestUpdate");
    request.setPassword("TestUpdate123");

    mockMvc.perform(
      patch("/api/users/current")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
          .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
        WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

      assertNull(response.getErrors());
      // check response, make sure equals to data update
      assertEquals("TestUpdate", response.getData().getName());
      assertEquals("test", response.getData().getUsername());

      // make sure password is equal
      User userDb = userRepository.findById("test").orElse(null);
      assertNotNull(userDb);
      assertTrue(BCrypt.checkpw("TestUpdate123", userDb.getPassword())); // you know "TestUpdate123" is password that we had set above
    });
  }
}
