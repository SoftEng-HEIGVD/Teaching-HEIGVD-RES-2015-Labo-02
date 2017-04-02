package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by sydney on 31.03.17.
 */
public class ByeCommandResponse {
    private Status status;
    private int numberOfCommands;

    public ByeCommandResponse(Status status, int nbOfCommands) {
        this.status = status;
        this.numberOfCommands = nbOfCommands;
    }
}
