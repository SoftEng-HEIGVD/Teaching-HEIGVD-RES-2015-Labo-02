package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol V2 specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * Example of response in the V2 protocol : {"status":"success","numberOfCommands":12},
 * where 12 is the number of commands sent by the client during the session.,
 *
 * @author Camilo Pineda Serna
 */
public class ByeCommandV2Response {
    private String status;
    private int numberOfCommands;

    public ByeCommandV2Response() {
    }

    public ByeCommandV2Response(String status, int numberOfCommands) {
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