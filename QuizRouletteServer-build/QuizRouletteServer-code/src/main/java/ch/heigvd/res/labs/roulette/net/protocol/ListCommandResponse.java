package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

import java.util.List;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LIST" command defined in the protocol V2 specification. The
 * JsonObjectMapper utility class can use this class.
 * <p>
 * Example of response in the V2 protocol : {"students":[{"fullname":"john doe"},{"fullname":"bill smith"}]},
 * where the value of students is an array containing all students in the store.
 *
 * @author Camilo Pineda Serna
 */
public class ListCommandResponse {
    private List<Student> studentsList;

    public ListCommandResponse() {
    }

    public ListCommandResponse(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    public void setNumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }
}