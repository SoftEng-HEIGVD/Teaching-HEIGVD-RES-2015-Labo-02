package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Thibaud Duchoud & Mario Ferreira
 */
public class LoadCommandResponse {
    private String status = "";
    private int numberOfNewStudents = 0;

    public LoadCommandResponse(String status, int numberOfNewStudents) {
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
    }
    
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the numberOfNewStudents
     */
    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    /**
     * @param numberOfNewStudents the numberOfNewStudents to set
     */
    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }
}
