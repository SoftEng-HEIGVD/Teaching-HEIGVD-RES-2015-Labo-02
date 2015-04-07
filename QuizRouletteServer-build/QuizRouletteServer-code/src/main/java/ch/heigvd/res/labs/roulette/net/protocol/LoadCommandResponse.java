package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * There is one subtelty: depending on the outcome, the payload will EITHER
 * contain an "error" attribute, OR a "fullname" attribute. The @JsonInclude
 * annotation is used to handle this issue.
 *
 * @author Olivier Liechti
 */
public class LoadCommandResponse {

   private String status;

   private int numberNewStudents;

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public int getNumberNewStudents() {
      return numberNewStudents;
   }

   public void setNumberNewStudents(int numberNewStudents) {
      this.numberNewStudents = numberNewStudents;
   }
}
