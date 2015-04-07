package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification.
 * The JsonObjectMapper utility class can use this class.
 *
 * There is one subtelty: depending on the outcome, the payload will EITHER
 * contain an "error" attribute, OR a "fullname" attribute. The @JsonInclude
 * annotation is used to handle this issue.
 *
 * @author Olivier Liechti
 */

public class ByeCommandResponse {

   private String status;

   private int numberCommands;

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public int getNumberOfCommands() {
      return numberCommands;
   }

   public void setNumberOfCommands(int numberCommands) {
      this.numberCommands = numberCommands;
   }
}
