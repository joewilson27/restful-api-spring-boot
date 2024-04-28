package jwilson.restful.restfulapispringboot.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {
  
  @Autowired
  private Validator validator;

  public void validate(Object request) {
    Set<ConstraintViolation<Object>> constraintViolation = validator.validate(request);
    if (constraintViolation.size() != 0) {
      // error
      throw new ConstraintViolationException(constraintViolation);
    }
  }

}
