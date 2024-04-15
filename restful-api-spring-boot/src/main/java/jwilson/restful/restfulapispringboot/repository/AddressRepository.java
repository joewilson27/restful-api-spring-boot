package jwilson.restful.restfulapispringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jwilson.restful.restfulapispringboot.entity.Address;

public interface AddressRepository extends JpaRepository<Address, String> {
  
}
