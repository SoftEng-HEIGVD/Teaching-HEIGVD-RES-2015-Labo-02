package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 * 
 * @author Miguel Pombo Dias
 */
public class ByeCommandResponse {

   private String status;
   private int numberOfCommands;

   public ByeCommandResponse(int numberOfCommands) {
      this.numberOfCommands = numberOfCommands;
      status = "success";
   }

   public int getNumberOfCommands() {
      return numberOfCommands;
   }

   public String getStatus() {
      return status;
   }

   public void setNumberOfCommands(int numberOfCommands) {
      this.numberOfCommands = numberOfCommands;
   }

   public void setStatus(String status) {
      this.status = status;
   }
}
