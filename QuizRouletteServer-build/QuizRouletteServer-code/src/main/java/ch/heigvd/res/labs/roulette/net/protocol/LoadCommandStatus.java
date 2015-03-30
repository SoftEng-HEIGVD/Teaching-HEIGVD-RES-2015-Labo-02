package ch.heigvd.res.labs.roulette.net.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * after processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class. 
 * 
 * There is one subtelty: depending on the outcome, the payload will EITHER 
 * contain an "error" attribute, OR a "fullname" attribute. The @JsonInclude 
 * annotation is used to handle this issue.
 * 
 * Handle the  V2: {"status":"success","numberOfNewStudents":3}
 * 
 * @author Valentin Minder
 */
@JsonInclude(Include.NON_NULL)
public class LoadCommandStatus {

  private String status;
  
  private int numberOfNewStudents;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getNumberOfNewStudents() {
    return numberOfNewStudents;
  }

  public void setNumberOfNewStudents(int numberOfNewStudents) {
    this.numberOfNewStudents = numberOfNewStudents;
  }

}
