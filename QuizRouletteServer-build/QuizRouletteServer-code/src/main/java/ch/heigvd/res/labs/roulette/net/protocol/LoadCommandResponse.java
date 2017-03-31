package ch.heigvd.res.labs.roulette.net.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class. 
 * 
 * There is one subtelty: depending on the outcome, the payload will EITHER 
 * contain an "error" attribute, OR a "fullname" attribute. The @JsonInclude 
 * annotation is used to handle this issue.
 * 
 * @author abass mahdavi 
 * almost a copy past of RandomCommandResponse.java proposed by Pr Liechti
 */


@JsonInclude(Include.NON_NULL)
public class LoadCommandResponse {
    
  private String status;
  private int numberOfStudents;

  public LoadCommandResponse(String status, int numberOfStudents) {
    this.status = status;
    this.numberOfStudents = numberOfStudents;
  }

  public String getStatus() {
    return status;
  }

  public int getNumberOfStudents() {
    return numberOfStudents;
  }

}

