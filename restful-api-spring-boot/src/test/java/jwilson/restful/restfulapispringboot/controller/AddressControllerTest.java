package jwilson.restful.restfulapispringboot.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import jwilson.restful.restfulapispringboot.entity.Contact;
import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.AddressResponse;
import jwilson.restful.restfulapispringboot.model.CreateAddressRequest;
import jwilson.restful.restfulapispringboot.model.WebResponse;
import jwilson.restful.restfulapispringboot.repository.AddressRepository;
import jwilson.restful.restfulapispringboot.repository.ContactRepository;
import jwilson.restful.restfulapispringboot.repository.UserRepository;
import jwilson.restful.restfulapispringboot.security.BCrypt;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    addressRepository.deleteAll();
    contactRepository.deleteAll();
    userRepository.deleteAll();

    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
    user.setName("Test");
    user.setToken("tokentest");
    user.setTokenExpiredAt(System.currentTimeMillis() + 1000000);
    userRepository.save(user);

    Contact contact = new Contact();
    contact.setId("test");
    contact.setUser(user);
    contact.setFirstName("Joe");
    contact.setLastName("Wilson");
    contact.setEmail("joevampire27@gmail.com");
    contact.setPhone("0812842342");
    contactRepository.save(contact);
  }

  @Test
  void createAddressBadRequest() throws Exception {
    CreateAddressRequest request = new CreateAddressRequest();
    request.setCountry("");

    mockMvc.perform(
              post("/api/contacts/test/addresses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isBadRequest()
    ).andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNotNull(response.getErrors());
    });
  }

  @Test
  void createAddressSuccess() throws Exception {
    CreateAddressRequest request = new CreateAddressRequest();
    request.setStreet("Ave Boulevard 7c");
    request.setCity("New Jersey");
    request.setProvince("New Jersey");
    request.setCountry("United States");
    request.setPostalCode("77777");

    mockMvc.perform(
              post("/api/contacts/test/addresses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
        WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>() {
        });
        assertNull(response.getErrors());
        // make sure data response
        assertEquals(request.getStreet(), response.getData().getStreet());
        assertEquals(request.getCity(), response.getData().getCity());
        assertEquals(request.getProvince(), response.getData().getProvince());
        assertEquals(request.getCountry(), response.getData().getCountry());
        assertEquals(request.getPostalCode(), response.getData().getPostalCode());

        assertTrue(addressRepository.existsById(response.getData().getId()));
    });
  }

}
