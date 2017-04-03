package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * This class is used to serialize/deserialize the response of the server back to
 * the client.
 *
 * The JSONObjectMapper class will use this class it this purpose.
 *
 * It's used by the version 2 of the protocol.
 * It contain the status of the BYE action (sucess or failed) and the number of
 * command performed
 *
 * Created by Quentin Zeller & Ali Miladi on 03.04.2017.
 */
public class ByeV2CommandResponse {
    private String status = "";

    private String numberOfCommands = "";

    public ByeV2CommandResponse(String status, String numberOfCommands){
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    /* Return the status of the performed command
     */
    public String getStatus(){
        return status;
    }
    /*
     * Return the number of students
     */
    public String getNumberOfCommands(){
        return numberOfCommands;
    }
}
