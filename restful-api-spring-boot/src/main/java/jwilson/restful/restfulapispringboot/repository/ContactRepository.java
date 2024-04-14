package jwilson.restful.restfulapispringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jwilson.restful.restfulapispringboot.entity.Contact;
import jwilson.restful.restfulapispringboot.entity.User;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> { 
  
  Optional<Contact> findFirstByUserAndId(User user, String id);

}
