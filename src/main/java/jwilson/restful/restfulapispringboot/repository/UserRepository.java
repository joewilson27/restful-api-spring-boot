package jwilson.restful.restfulapispringboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jwilson.restful.restfulapispringboot.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findFirstByToken(String token);

}
