package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "INFO" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 * 
 * @author Olivier Liechti
 */
public class InfoCommandResponse {

  private String protocolVersion;
  private int numberOfStudents;

  public InfoCommandResponse() {
      /* TODO
      numberOfStudents n'est pas forcement définit.
      Et du coup, quand on passe dans ce constructeur et qu'on essaye getNumberOfStudents
      et ben ca merde et ca fait un nullPointerException. 
      */
  }

  public InfoCommandResponse(String protocolVersion, int numberOfStudents) {
    this.protocolVersion = protocolVersion;
    this.numberOfStudents = numberOfStudents;
  }

  public String getProtocolVersion() {
    return protocolVersion;
  }

  public void setProtocolVersion(String protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  public int getNumberOfStudents() {
    return numberOfStudents;
  }

  public void setNumberOfStudents(int numberOfStudents) {
    this.numberOfStudents = numberOfStudents;
  }

}
