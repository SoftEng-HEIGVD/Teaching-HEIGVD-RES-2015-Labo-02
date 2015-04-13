package ch.heigvd.res.labs.roulette.net.protocol;
/*
 * @author Henrik Akesson & Fabien Salathe
 */
public class ByeCommandResponse {

    private String status;

    private int nbCommands;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfCommands() {
        return nbCommands;
    }

    public void setNumberOfCommands(int nbCommands) {
        this.nbCommands = nbCommands;
    }
}