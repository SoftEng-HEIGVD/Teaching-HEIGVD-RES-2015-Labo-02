package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @authors Ludovic Richard, Luana Martelli
 */
public class LoadCommandResponse {

    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponse(String status, int numberOfStudents) {
        this.status = status;
        this.numberOfNewStudents = numberOfStudents;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public String getStatus() {
        return status;
    }

}
