package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Christopher Meier
 * @author Daniel Palumbo
 */
public class ByeCommandResponse {
    private String status;
    private int nbCommands;

    public ByeCommandResponse(String status, int nbCommands) {
        this.status = status;
        this.nbCommands = nbCommands;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNbCommands() {
        return nbCommands;
    }

    public void setNbCommands(int nbCommands) {
        this.nbCommands = nbCommands;
    }
}
