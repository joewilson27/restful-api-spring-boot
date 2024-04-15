package jwilson.restful.restfulapispringboot.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.type.TypeReference;

import jwilson.restful.restfulapispringboot.entity.Address;
import jwilson.restful.restfulapispringboot.entity.Contact;
import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.AddressResponse;
import jwilson.restful.restfulapispringboot.model.CreateAddressRequest;
import jwilson.restful.restfulapispringboot.model.UpdateAddressRequest;
import jwilson.restful.restfulapispringboot.model.WebResponse;
import jwilson.restful.restfulapispringboot.repository.AddressRepository;
import jwilson.restful.restfulapispringboot.repository.ContactRepository;
import jwilson.restful.restfulapispringboot.repository.UserRepository;
import jwilson.restful.restfulapispringboot.security.BCrypt;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

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
    request.setCity("Jersey City");
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

  @Test
  void getAddressNotFound() throws Exception {
    mockMvc.perform(
            get("/api/contacts/test/addresses/test")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
            status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    Address address = new Address();
    address.setId("test");
    address.setContact(contact);
    address.setStreet("Ave Boulevard 7c");
    address.setCity("Jersey City");
    address.setProvince("New Jersey");
    address.setCountry("United States");
    address.setPostalCode("77777");
    addressRepository.save(address);

    mockMvc.perform(
            get("/api/contacts/test/addresses/test") // kita cari id test (query param paling terakhir)
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>() {
      });
      assertNull(response.getErrors());
      assertEquals(address.getId(), response.getData().getId());
      assertEquals(address.getStreet(), response.getData().getStreet());
      assertEquals(address.getCity(), response.getData().getCity());
      assertEquals(address.getProvince(), response.getData().getProvince());
      assertEquals(address.getCountry(), response.getData().getCountry());
      assertEquals(address.getPostalCode(), response.getData().getPostalCode());
    });
  }

  @Test
  void updateAddressBadRequest() throws Exception {
    UpdateAddressRequest request = new UpdateAddressRequest();
    request.setCountry(""); // fill blank to test error

    mockMvc.perform(
            put("/api/contacts/test/addresses/test")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
            status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void updateAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    // create new address
    Address address = new Address();
    address.setId("test");
    address.setContact(contact);
    address.setStreet("Ave Boulevard 7c");
    address.setCity("Jersey City");
    address.setProvince("New Jersey");
    address.setCountry("United States");
    address.setPostalCode("77777");
    addressRepository.save(address);

    // then prepared for update data to existing addresses
    UpdateAddressRequest request = new UpdateAddressRequest();
    request.setStreet("Joshua Street");
    request.setCity("Belleville");
    request.setProvince("New Jersey");
    request.setCountry("United States");
    request.setPostalCode("55555");

    mockMvc.perform(
            put("/api/contacts/test/addresses/test")
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
      assertEquals(request.getStreet(), response.getData().getStreet());
      assertEquals(request.getCity(), response.getData().getCity());
      assertEquals(request.getProvince(), response.getData().getProvince());
      assertEquals(request.getCountry(), response.getData().getCountry());
      assertEquals(request.getPostalCode(), response.getData().getPostalCode());

      assertTrue(addressRepository.existsById(response.getData().getId()));
    });
  }

  @Test
  void deleteAddressNotFound() throws Exception {
    // try to remove address based on id contact test and id address test
    mockMvc.perform(
            delete("/api/contacts/test/addresses/test")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void deleteAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    Address address = new Address();
    address.setId("test");
    address.setContact(contact);
    address.setStreet("Ave Boulevard 7c");
    address.setCity("Jersey City");
    address.setProvince("New Jersey");
    address.setCountry("United States");
    address.setPostalCode("77777");
    addressRepository.save(address);

    mockMvc.perform(
            delete("/api/contacts/test/addresses/test")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });
      assertNull(response.getErrors());
      assertEquals("OK", response.getData());

      assertFalse(addressRepository.existsById("test")); // make sure address by id test is no longer there
    });
  }

  @Test
  void listAddressNotFound() throws Exception {
    mockMvc.perform(
            get("/api/contacts/salah/addresses")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
      status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void listAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    // create an example of address for contact test
    for (int i = 0; i < 5; i++) {
      Address address = new Address();
      address.setId("test-" + i);
      address.setContact(contact);
      address.setStreet("Ave Boulevard 7c");
      address.setCity("Jersey City");
      address.setProvince("New Jersey");
      address.setCountry("United States");
      address.setPostalCode("77777");
      addressRepository.save(address);
    }

    mockMvc.perform(
            get("/api/contacts/test/addresses")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "tokentest")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<AddressResponse>>>() {
      });
      assertNull(response.getErrors());
      assertEquals(5, response.getData().size()); // must be 5, as data looping above
    });
  }

}
