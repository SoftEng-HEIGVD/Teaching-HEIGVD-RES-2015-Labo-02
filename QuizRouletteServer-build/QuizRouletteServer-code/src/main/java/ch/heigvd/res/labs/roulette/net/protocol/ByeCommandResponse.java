package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by sydney on 31.03.17.
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 */
public class ByeCommandResponse {
    private String status;
    private int numberOfCommands;

    public ByeCommandResponse(Status status, int nbOfCommands) {
        this.status = status.toString();
        this.numberOfCommands = nbOfCommands;
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }
}
