package ch.heigvd.res.labs.roulette.net.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
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
public class ByeCommandResponse {
    
  private String status;
  private int numberOfCommands;

  public ByeCommandResponse(String status, int numberOfCommands) {
    this.status = status;
    this.numberOfCommands = numberOfCommands;
  }

  public String getStatus() {
    return status;
  }

  public int getnumberOfCommands() {
    return numberOfCommands;
  }

}
