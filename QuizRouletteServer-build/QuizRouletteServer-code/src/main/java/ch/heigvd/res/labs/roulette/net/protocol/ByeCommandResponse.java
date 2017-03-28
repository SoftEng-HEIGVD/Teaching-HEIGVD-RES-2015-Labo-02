package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * We will use this class to serialize or deserialize the response of the server
 * to the command BYE
 * @author Julien Brêchet
 * @author Adrien Marco
 */

public class ByeCommandResponse {

    private String status;
    private int numberOfCommands;

    public int getNumberOfCommands(){
        return numberOfCommands;
    }

    public void setNumberOfCommands(int number){
        numberOfCommands = number;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

}