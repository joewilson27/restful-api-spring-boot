package jwilson.restful.restfulapispringboot.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.AddressResponse;
import jwilson.restful.restfulapispringboot.model.CreateAddressRequest;
import jwilson.restful.restfulapispringboot.model.UpdateAddressRequest;
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

  @GetMapping(
    path = "/api/contacts/{contactId}/addresses/{addressId}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<AddressResponse> get(User user,
                                          @PathVariable("contactId") String contactId,
                                          @PathVariable("addressId") String addressId) {
    AddressResponse addressResponse = addressService.get(user, contactId, addressId);
    return WebResponse.<AddressResponse>builder().data(addressResponse).build();
  }

  @PutMapping(
    path = "/api/contacts/{contactId}/addresses/{addressId}",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<AddressResponse> update(User user, 
                                            @RequestBody UpdateAddressRequest request, 
                                            @PathVariable("contactId") String contactId,
                                            @PathVariable("addressId") String addressId) {
     request.setContactId(contactId); // set contact id from queryString / pathVariable
     request.setAddressId(addressId);
     AddressResponse response = addressService.update(user, request);
     return WebResponse.<AddressResponse>builder().data(response).build();
  }

  @DeleteMapping(
    path = "/api/contacts/{contactId}/addresses/{addressId}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
    public WebResponse<String> remove(User user,
                                  @PathVariable("contactId") String contactId,
                                  @PathVariable("addressId") String addressId) {
    addressService.remove(user, contactId, addressId);
    return WebResponse.<String>builder().data("OK").build();
  }

  @GetMapping(
    path = "/api/contacts/{contactId}/addresses",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<List<AddressResponse>> list(User user,
                                            @PathVariable("contactId") String contactId) {
  List<AddressResponse> addressResponses = addressService.list(user, contactId);
  return WebResponse.<List<AddressResponse>>builder().data(addressResponses).build();
  }

}
