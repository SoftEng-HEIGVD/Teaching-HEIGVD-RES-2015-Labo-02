package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Thibaud Duchoud & Mario Ferreira
 */
public class ByeCommandResponse {
    
    private String status = "";
    private int numberOfCommands = 0;

    public ByeCommandResponse(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
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
     * @return the numberOfCommands
     */
    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    /**
     * @param numberOfCommands the numberOfCommands to set
     */
    public void setNumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }
    
    
}
