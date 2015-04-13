package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 * 
 * @author Miguel Santamaria
 * @author Bastien Rouiller
 */
public class ByeCommandResponse {

    private String status = "success";
    private int numberOfCommands = 0;

    public void incrementNbOfCommandsUsed() {
        numberOfCommands++;
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }
}
