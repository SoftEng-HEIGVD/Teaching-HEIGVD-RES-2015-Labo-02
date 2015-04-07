package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification.
 * The JsonObjectMapper utility class can use this class.

 * @author Olivier Liechti, Sébastien Henneberger
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
