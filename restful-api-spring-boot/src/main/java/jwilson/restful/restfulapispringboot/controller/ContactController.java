package jwilson.restful.restfulapispringboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.ContactResponse;
import jwilson.restful.restfulapispringboot.model.CreateContactRequest;
import jwilson.restful.restfulapispringboot.model.UpdateContactRequest;
import jwilson.restful.restfulapispringboot.model.WebResponse;
import jwilson.restful.restfulapispringboot.service.ContactService;

@RestController
public class ContactController {
  
  @Autowired
  private ContactService contactService;

  @PostMapping(
    path = "/api/contacts",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request) {
    ContactResponse contactResponse = contactService.create(user, request);
    return WebResponse.<ContactResponse>builder().data(contactResponse).build();
  }

  @GetMapping(
    path = "/api/contacts/{contactId}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<ContactResponse> get(User user, @PathVariable("contactId") String contactId) {
    ContactResponse contactResponse = contactService.get(user, contactId);
    return WebResponse.<ContactResponse>builder().data(contactResponse).build();
  }

  @PutMapping(
    path = "/api/contacts/{contactId}",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<ContactResponse> update(User user, 
                                            @RequestBody UpdateContactRequest request,
                                            @PathVariable String contactId) {
    // make sure id we set from query string
    request.setId(contactId);

    ContactResponse contactResponse = contactService.update(user, request);
    return WebResponse.<ContactResponse>builder().data(contactResponse).build();
  }

  @DeleteMapping(
    path = "/api/contacts/{contactId}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<String> delete(User user, @PathVariable("contactId") String contactId) {
    contactService.delete(user, contactId);
    return WebResponse.<String>builder().data("OK").build();
  }

}
