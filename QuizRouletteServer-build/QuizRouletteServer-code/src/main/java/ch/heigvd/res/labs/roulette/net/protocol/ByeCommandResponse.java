package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by quentingigon on 03.04.17.
 */
public class ByeCommandResponse {

    private String status;
    private int nbCommands;

    public ByeCommandResponse(String status, int commands) {
        this.status = status;
        this.nbCommands = commands;
    }

    public ByeCommandResponse() {
    }

    public String getStatus() {
        return status;
    }

    public int getNbCommands() {
        return nbCommands;
    }
}
