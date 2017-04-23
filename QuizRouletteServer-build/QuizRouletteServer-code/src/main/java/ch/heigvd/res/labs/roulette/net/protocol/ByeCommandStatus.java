package ch.heigvd.res.labs.roulette.net.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * after processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class. 
 * 
 * There is one subtelty: depending on the outcome, the payload will EITHER 
 * contain an "error" attribute, OR a "fullname" attribute. The @JsonInclude 
 * annotation is used to handle this issue.
 * 
 * Handle the  V2: {"status":"success","numberOfCommands":12}
 * 
 * @author Valentin Minder
 */
@JsonInclude(Include.NON_NULL)
public class ByeCommandStatus {

  private String status;
  
  private int numberOfCommands;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getNumberOfCommands() {
    return numberOfCommands;
  }

  public void setNumberOfCommands(int numberOfCommands) {
    this.numberOfCommands = numberOfCommands;
  }

}
