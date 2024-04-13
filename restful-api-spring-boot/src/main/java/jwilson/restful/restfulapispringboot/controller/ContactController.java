package jwilson.restful.restfulapispringboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.ContactResponse;
import jwilson.restful.restfulapispringboot.model.CreateContactRequest;
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

}
