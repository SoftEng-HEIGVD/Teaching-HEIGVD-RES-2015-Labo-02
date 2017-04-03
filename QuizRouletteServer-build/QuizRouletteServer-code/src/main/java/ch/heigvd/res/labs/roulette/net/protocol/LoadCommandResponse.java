package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Christopher Meier
 * @author Daniel Palumbo
 */
public class LoadCommandResponse {

  private String status;
  private int numberOfNewStudents;

  public LoadCommandResponse() {
    this.numberOfNewStudents = 0;
  }

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
