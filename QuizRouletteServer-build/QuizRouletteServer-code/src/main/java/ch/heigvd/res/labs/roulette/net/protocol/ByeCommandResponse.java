package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by Blue Spring on 27.03.2017.
 */
public class ByeCommandResponse {

    private int numberOfCommands;
    private String status;

    public ByeCommandResponse(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    public String getStatus() {
        return status;
    }

}
