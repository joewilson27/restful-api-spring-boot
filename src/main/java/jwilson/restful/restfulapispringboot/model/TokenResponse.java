package jwilson.restful.restfulapispringboot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {
  
  // no need validation in this model, because this is response, not request model

  private String token;

  private Long expiredAt;

}
