package jwilson.restful.restfulapispringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jwilson.restful.restfulapispringboot.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> { 
}
