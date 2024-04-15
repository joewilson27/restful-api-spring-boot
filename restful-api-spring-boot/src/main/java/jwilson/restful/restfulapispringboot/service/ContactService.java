package jwilson.restful.restfulapispringboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.transaction.annotation.Transactional;
import jwilson.restful.restfulapispringboot.entity.Contact;
import jwilson.restful.restfulapispringboot.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jwilson.restful.restfulapispringboot.model.ContactResponse;
import jwilson.restful.restfulapispringboot.model.CreateContactRequest;
import jwilson.restful.restfulapispringboot.model.SearchContactRequest;
import jwilson.restful.restfulapispringboot.model.UpdateContactRequest;
import jwilson.restful.restfulapispringboot.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
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
    
    return toContactResponse(contact);
  }

  private ContactResponse toContactResponse(Contact contact) {
    return ContactResponse.builder()
                        .id(contact.getId())
                        .firstName(contact.getFirstName())
                        .lastName(contact.getLastName())
                        .email(contact.getEmail())
                        .phone(contact.getPhone())
                        .build();
  }

  @Transactional(readOnly = true) // because we only read data here
  public ContactResponse get(User user, String id) {
    Contact contact = contactRepository.findFirstByUserAndId(user, id)
                                       .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

    return toContactResponse(contact);
  }

  @Transactional
  public ContactResponse update(User user, UpdateContactRequest request) {
    validationService.validate(request);

    Contact contact = contactRepository.findFirstByUserAndId(user, request.getId())
                                       .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
                 
    contact.setFirstName(request.getFirstName());
    contact.setLastName(request.getLastName());
    contact.setEmail(request.getEmail());
    contact.setPhone(request.getPhone());
    contactRepository.save(contact);

    return toContactResponse(contact);
  }

  @Transactional
  public void delete(User user, String contactId) {
    Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                                       .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
         
    contactRepository.delete(contact);                                      
  }

  @Transactional(readOnly = true) // because we only read data here
  public Page<ContactResponse> search(User user, SearchContactRequest request) {
    log.info("Search Request : {}", request);
    Specification<Contact> specification = (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(builder.equal(root.get("user"), user)); // make sure user
      // start the query searching
      // name
      if (Objects.nonNull(request.getName())) {
        // because, name is consist of firstName and lastName, so we are using this approach
        predicates.add(builder.or(
          builder.like(root.get("firstName"), "%"+request.getName()+"%"),
          builder.like(root.get("lastName"), "%"+request.getName()+"%")
        ));
      }
      // email
      if (Objects.nonNull(request.getEmail())) {
        predicates.add(builder.like(root.get("email"), "%"+request.getEmail()+"%"));
      }
      // phone
      if (Objects.nonNull(request.getPhone())) {
        predicates.add(builder.like(root.get("phone"), "%"+request.getPhone()+"%"));
      }

      return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
    };

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
    Page<Contact> contacts = contactRepository.findAll(specification, pageable);

    List<ContactResponse> contactResponses = contacts.getContent().stream()
                                                     .map(this::toContactResponse) // short than .map(contact -> toContactResponse(contact))
                                                     .toList(); // short than .collect(Collectors.toList());
  
    return new PageImpl<>(contactResponses, pageable, contacts.getTotalElements());
  }



}
