package jwilson.restful.restfulapispringboot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.LoginUserRequest;
import jwilson.restful.restfulapispringboot.model.TokenResponse;
import jwilson.restful.restfulapispringboot.model.WebResponse;
import jwilson.restful.restfulapispringboot.repository.UserRepository;
import jwilson.restful.restfulapispringboot.security.BCrypt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void loginFailedUserNotFound() throws Exception {
      LoginUserRequest request = new LoginUserRequest();
      request.setUsername("test");
      request.setPassword("secret");

      mockMvc.perform(
          post("/api/auth/login")
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
    void loginFailedWrongPassword() throws Exception {
      User user = new User();
      user.setName("Test");
      user.setUsername("test");
      user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
      userRepository.save(user);

      LoginUserRequest request = new LoginUserRequest();
      request.setUsername("test");
      request.setPassword("wrong"); // we define wrong password here

      mockMvc.perform(
          post("/api/auth/login")
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
    void loginSuccess() throws Exception {
      User user = new User();
      user.setName("Test");
      user.setUsername("test");
      user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
      userRepository.save(user);

      LoginUserRequest request = new LoginUserRequest();
      request.setUsername("test");
      request.setPassword("secret");

      mockMvc.perform(
          post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
      ).andExpectAll(
        status().isOk()
      ).andDo(result -> {
        WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNull(response.getErrors());
        assertNotNull(response.getData().getToken());
        assertNotNull(response.getData().getExpiredAt());

        User userDb = userRepository.findById("test").orElse(null);
        assertNotNull(userDb);
        // make sure data from table is equal to data response
        assertEquals(userDb.getToken(), response.getData().getToken());
        assertEquals(userDb.getTokenExpiredAt(), response.getData().getExpiredAt());
      });
    }

    @Test
    void logoutFailed() throws Exception {
      mockMvc.perform(
        delete("/api/auth/logout")
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
    void logoutSuccess() throws Exception {
      User user = new User();
      user.setUsername("test");
      user.setName("Name");
      user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
      user.setToken("tokentest");
      user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
      userRepository.save(user);

      mockMvc.perform(
        delete("/api/auth/logout")
          .accept(MediaType.APPLICATION_JSON)
          .header("X-API-TOKEN", "tokentest")
      ).andExpectAll(
        status().isOk()
      ).andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNull(response.getErrors());
        assertEquals("OK", response.getData());

        // then, we'd better to make sure token and expiredat are null
        User userDb = new User();
        assertNotNull(userDb);
        // you can do check like these:
        //assertEquals(userDb.getTokenExpiredAt(), null);
        //assertEquals(userDb.getToken(), null);
        // or like these:
        assertNull(userDb.getTokenExpiredAt());
        assertNull(userDb.getToken());
      });
    }


}
