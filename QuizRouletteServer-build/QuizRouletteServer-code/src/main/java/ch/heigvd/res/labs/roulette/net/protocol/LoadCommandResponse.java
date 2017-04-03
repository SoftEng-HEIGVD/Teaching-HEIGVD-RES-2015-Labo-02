package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by sydney on 31.03.17.
 *
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Sydney Hauke
 * @author Thuy-My Tran
 */
public class LoadCommandResponse {
    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponse(Status status, int nbNewSt) {
        this.status = status.toString();
        numberOfNewStudents = nbNewSt;
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }
}
