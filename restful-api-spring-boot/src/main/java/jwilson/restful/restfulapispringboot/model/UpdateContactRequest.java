package jwilson.restful.restfulapispringboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateContactRequest {
  
  @JsonIgnore // THIS IS IMPORTANT! TO IGNORE USER TO SEND ID VIA JSON BODY. THIS ID IS FROM QUERYSTRING, NOT JSON PARAMS
  @NotBlank
  private String id;

  @NotBlank
  @Size(max = 100)
  private String firstName;

  @Size(max = 100)
  private String lastName;

  @Size(max = 100)
  @Email
  private String email;

  @Size(max = 100)
  private String phone;

}
