package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol V2 specification. The
 * JsonObjectMapper utility class can use this class.
 * <p>
 * Example of response in the V2 protocol : {"status":"success","numberOfNewStudents":3},
 * where 3 is the number of student lines sent by the client.
 *
 * @author Camilo Pineda Serna
 */
public class LoadCommandV2Response {
    private String status;
    private int numberOfNewStudents;

    public LoadCommandV2Response() {
    }

    public LoadCommandV2Response(String status, int numberOfNewStudents) {
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
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