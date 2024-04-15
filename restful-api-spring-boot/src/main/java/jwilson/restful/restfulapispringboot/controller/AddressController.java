package jwilson.restful.restfulapispringboot.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.AddressResponse;
import jwilson.restful.restfulapispringboot.model.CreateAddressRequest;
import jwilson.restful.restfulapispringboot.model.WebResponse;
import jwilson.restful.restfulapispringboot.service.AddressService;

@RestController
public class AddressController {
  
  @Autowired
  private AddressService addressService;

  @PostMapping(
    path = "/api/contacts/{contactId}/addresses",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE

  )
  public WebResponse<AddressResponse> create(User user, 
                                            @RequestBody CreateAddressRequest request, 
                                            @PathVariable("contactId") String contactId) {
     request.setContactId(contactId); // set contact id from queryString / pathVariable
     AddressResponse response = addressService.create(user, request);
     return WebResponse.<AddressResponse>builder().data(response).build();
  }

}
