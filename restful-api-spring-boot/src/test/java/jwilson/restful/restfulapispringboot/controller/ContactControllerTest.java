package jwilson.restful.restfulapispringboot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.ContactResponse;
import jwilson.restful.restfulapispringboot.model.CreateContactRequest;
import jwilson.restful.restfulapispringboot.model.WebResponse;
import jwilson.restful.restfulapispringboot.repository.ContactRepository;
import jwilson.restful.restfulapispringboot.repository.UserRepository;
import jwilson.restful.restfulapispringboot.security.BCrypt;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    contactRepository.deleteAll();
    userRepository.deleteAll();

    // because we need user login, then we create user in this setup
    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
    user.setName("Test");
    user.setToken("tokentest");
    user.setTokenExpiredAt(System.currentTimeMillis() + 1000000);
    userRepository.save(user);
  }

  @Test
  void createContactBadRequest() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName(""); // fill blank for testing
    request.setEmail("wrong"); // fill invalid email address

    mockMvc.perform(
      post("/api/contacts")
       .accept(MediaType.APPLICATION_JSON)
       .contentType(MediaType.APPLICATION_JSON)
       .content(objectMapper.writeValueAsString(request))
       .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void createContactSuccess() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("Joe");
    request.setLastName("Wilson");
    request.setEmail("joevampire27@gmail.com");
    request.setPhone("08128484847");

    mockMvc.perform(
      post("/api/contacts")
       .accept(MediaType.APPLICATION_JSON)
       .contentType(MediaType.APPLICATION_JSON)
       .content(objectMapper.writeValueAsString(request))
       .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {}); // still work with this generic WebResponse
      // WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

      assertNull(response.getErrors());
      // check response, must be equal as data we are created
      assertEquals("Joe", response.getData().getFirstName());
      assertEquals("Wilson", response.getData().getLastName());
      assertEquals("joevampire27@gmail.com", response.getData().getEmail());
      assertEquals("08128484847", response.getData().getPhone());

      // make sure data has been inserted in table
      assertTrue(contactRepository.existsById(response.getData().getId()));
    });
  }

}
