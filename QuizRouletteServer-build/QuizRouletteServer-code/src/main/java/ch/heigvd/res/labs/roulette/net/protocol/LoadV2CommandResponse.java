package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * This class is used to serialize/deserialize the response of the server back to
 * the client.
 *
 * The JSONObjectMapper class will use this class it this purpose.
 *
 * It's used by the version 2 of the protocol.
 * It contain the status of the LOAD action (sucess or failed) and the number of
 * student added.
 *
 * Created by Quentin Zeller & Ali Miladi on 03.04.2017.
 */
public class LoadV2CommandResponse {

    private String status = "";

    private String numberOfNewStudents = "";

    public LoadV2CommandResponse(String status, String numberOfNewStudents){
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
    }

    /* Return the status of the performed command
     */
    public String getStatus(){
        return status;
    }
    /*
     * Return the number of students
     */
    public String getNumberOfStudent(){
        return numberOfNewStudents;
    }
}
