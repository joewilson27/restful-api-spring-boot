package jwilson.restful.restfulapispringboot.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import jwilson.restful.restfulapispringboot.entity.Contact;
import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.ContactResponse;
import jwilson.restful.restfulapispringboot.model.CreateContactRequest;
import jwilson.restful.restfulapispringboot.model.UpdateContactRequest;
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

  @Test
  void getContactNotFound() throws Exception {
    mockMvc.perform(
      get("/api/contacts/123") // 123 is random id we put to get an error not found contact
       .accept(MediaType.APPLICATION_JSON)
       .contentType(MediaType.APPLICATION_JSON)
       .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getContactSuccess() throws Exception {
    // call current user
    User user = userRepository.findById("test").orElseThrow();

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("Joe");
    contact.setLastName("Wilson");
    contact.setEmail("joevampire27@gmail.com");
    contact.setPhone("081284081237");
    contactRepository.save(contact);

    mockMvc.perform(
      get("/api/contacts/" + contact.getId())
       .accept(MediaType.APPLICATION_JSON)
       .contentType(MediaType.APPLICATION_JSON)
       .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {});

      assertNull(response.getErrors());

      // make sure data contacts are equal with data response
      assertEquals(contact.getId(), response.getData().getId());
      assertEquals(contact.getFirstName(), response.getData().getFirstName());
      assertEquals(contact.getLastName(), response.getData().getLastName());
      assertEquals(contact.getEmail(), response.getData().getEmail());
      assertEquals(contact.getPhone(), response.getData().getPhone());
    });
  }

  @Test
  void updateContactBadRequest() throws Exception {
    UpdateContactRequest request = new UpdateContactRequest();
    request.setFirstName(""); // fill blank for testing
    request.setEmail("wrong"); // fill invalid email address

    mockMvc.perform(
      put("/api/contacts/1234")
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
  void updateContactSuccess() throws Exception {
    // call current user
    User user = userRepository.findById("test").orElseThrow();

    // make sure we have created contact before updating
    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("Joe");
    contact.setLastName("Wilson");
    contact.setEmail("joevampire27@gmail.com");
    contact.setPhone("081284081237");
    contactRepository.save(contact);

    UpdateContactRequest request = new UpdateContactRequest();
    request.setFirstName("Nikola");
    request.setLastName("Tesla");
    request.setEmail("tesla@gmail.com");
    request.setPhone("08123123324");

    mockMvc.perform(
      put("/api/contacts/" + contact.getId())
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
      assertEquals(request.getFirstName(), response.getData().getFirstName());
      assertEquals(request.getLastName(), response.getData().getLastName());
      assertEquals(request.getEmail(), response.getData().getEmail());
      assertEquals(request.getPhone(), response.getData().getPhone());

      // make sure data has been inserted in table
      assertTrue(contactRepository.existsById(response.getData().getId()));
    });
  }

  @Test
  void deleteContactNotFound() throws Exception {
    mockMvc.perform(
      delete("/api/contacts/123") // 123 is random id we put to get an error not found contact
       .accept(MediaType.APPLICATION_JSON)
       .contentType(MediaType.APPLICATION_JSON)
       .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void deleteContactSuccess() throws Exception {
    // call current user
    User user = userRepository.findById("test").orElseThrow();

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("Joe");
    contact.setLastName("Wilson");
    contact.setEmail("joevampire27@gmail.com");
    contact.setPhone("081284081237");
    contactRepository.save(contact);

    mockMvc.perform(
      delete("/api/contacts/" + contact.getId())
       .accept(MediaType.APPLICATION_JSON)
       .contentType(MediaType.APPLICATION_JSON)
       .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});

      assertNull(response.getErrors());
      assertEquals("OK", response.getData());
    });
  }

  @Test
  void searchNotFound() throws Exception {
    mockMvc.perform(
      get("/api/contacts")
       .accept(MediaType.APPLICATION_JSON)
       .contentType(MediaType.APPLICATION_JSON)
       .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {});

      assertNull(response.getErrors());
      assertEquals(0, response.getData().size());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(0, response.getPaging().getTotalPage());
      assertEquals(10, response.getPaging().getSize());
    });
  }

  @Test
  void searchSuccess() throws Exception {
    User user = userRepository.findById("test").orElseThrow();

    // create dummy contact to search for
    for (int i = 0; i < 100; i++) {
      Contact contact = new Contact();
      contact.setId(UUID.randomUUID().toString());
      contact.setUser(user);
      contact.setFirstName("Joe " + i);
      contact.setLastName("Wilson");
      contact.setEmail("joevampire27"+i+"@gmail.com");
      contact.setPhone("081284081237");
      contactRepository.save(contact);
    }

    // search by firstName
    mockMvc.perform(
      get("/api/contacts")
        .queryParam("name", "Joe") // searching using like
       .accept(MediaType.APPLICATION_JSON)
       .contentType(MediaType.APPLICATION_JSON)
       .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {});

      assertNull(response.getErrors());
      assertEquals(10, response.getData().size()); // we have 100 data with name joe, but page size is 10
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(10, response.getPaging().getSize());
    });

    // search by lastName
    mockMvc.perform(
      get("/api/contacts")
        .queryParam("name", "Wilson") // searching using like
       .accept(MediaType.APPLICATION_JSON)
       .contentType(MediaType.APPLICATION_JSON)
       .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {});

      assertNull(response.getErrors());
      assertEquals(10, response.getData().size()); // we have 100 data with name joe, but page size is 10
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(10, response.getPaging().getSize());
    });

    // search by email
    mockMvc.perform(
      get("/api/contacts")
        .queryParam("email", "gmail.com") // searching using like
       .accept(MediaType.APPLICATION_JSON)
       .contentType(MediaType.APPLICATION_JSON)
       .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {});

      assertNull(response.getErrors());
      assertEquals(10, response.getData().size()); // we have 100 data with name joe, but page size is 10
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(10, response.getPaging().getSize());
    });

    // search by phone
    mockMvc.perform(
      get("/api/contacts")
        .queryParam("phone", "08128408") // searching using like
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {});

      assertNull(response.getErrors());
      assertEquals(10, response.getData().size()); 
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(10, response.getPaging().getSize());
    });

    // search by page
    mockMvc.perform(
      get("/api/contacts")
        .queryParam("phone", "08128408") // searching using like
        .queryParam("page", "1000")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {});

      assertNull(response.getErrors());
      assertEquals(0, response.getData().size()); 
      assertEquals(1000, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(10, response.getPaging().getSize());
    });

  }

}
