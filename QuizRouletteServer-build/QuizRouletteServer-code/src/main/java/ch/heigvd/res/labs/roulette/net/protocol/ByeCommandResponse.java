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
    private int numberOfCommands;

    /**
     * Constructor.
     *
     * @param status           Status of the command.
     * @param numberOfCommands Number of commands entered by the client.
     */
    public ByeCommandResponse(String status, int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
        this.status = status;
    }

    /**
     * @return The number of commands entered by the client.
     */
    public int getNumberOfCommands() {
        return numberOfCommands;
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
     * @param numberOfCommands Number of commands entered by the client.
     */
    public void setNumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
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
