package ch.heigvd.res.labs.roulette.net.protocol;


import java.util.List;

/**
 * Created by Michael on 28.03.2017.
 */
public class ByeCommandResponse {
    private String status;
    private int numberOfCommands;

    public ByeCommandResponse(String status, int numberOfCommands){
        this.status=status;
        this.numberOfCommands = numberOfCommands;
    }



    public String getStatus(){
        return status;
    }

    public int getNumberOfCommands(){
        return numberOfCommands;
    }

}
