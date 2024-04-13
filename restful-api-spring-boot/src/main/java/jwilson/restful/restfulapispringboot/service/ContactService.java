package jwilson.restful.restfulapispringboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jwilson.restful.restfulapispringboot.entity.Contact;
import jwilson.restful.restfulapispringboot.entity.User;
import jwilson.restful.restfulapispringboot.model.ContactResponse;
import jwilson.restful.restfulapispringboot.model.CreateContactRequest;
import jwilson.restful.restfulapispringboot.repository.ContactRepository;
import java.util.UUID;

@Service
public class ContactService {
  
  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private ValidationService validationService;

  @Transactional
  public ContactResponse create(User user, CreateContactRequest request) {
    validationService.validate(request);

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setFirstName(request.getFirstName());
    contact.setLastName(request.getLastName());
    contact.setEmail(request.getEmail());
    contact.setPhone(request.getPhone());
    contact.setUser(user); // will fill with user who's logged in
    contactRepository.save(contact);
    
    return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
  }

}
