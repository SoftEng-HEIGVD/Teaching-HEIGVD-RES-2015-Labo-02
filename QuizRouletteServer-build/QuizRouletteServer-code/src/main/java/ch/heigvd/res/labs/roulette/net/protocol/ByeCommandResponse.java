package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Class used to serialize/deserialize the response sent by the server when
 * processing the `BYE` command defined in the V2 protocol.
 *
 * @author Lucas ELISEI (faku99)
 * @author David TRUAN  (Daxidz)
 */
public class ByeCommandResponse {
    // The status of the command.
    private String status;

    // Number of commands entered by the client.
    private int nbCommands;

    /**
     * Constructor.
     *
     * @param nbCommands Number of commands entered by the client.
     * @param status     Status of the command.
     */
    public ByeCommandResponse(String status, int nbCommands) {
        this.nbCommands = nbCommands;
        this.status = status;
    }

    /**
     * @return The number of commands entered by the client.
     */
    public int getNbCommands() {
        return nbCommands;
    }

    /**
     * @return The status of the command.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the number of commands entered by the client.
     *
     * @param nbCommands Number of commands entered by the client.
     */
    public void setNbCommands(int nbCommands) {
        this.nbCommands = nbCommands;
    }

    /**
     * Sets the status of the command.
     *
     * @param status Status of the command.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
