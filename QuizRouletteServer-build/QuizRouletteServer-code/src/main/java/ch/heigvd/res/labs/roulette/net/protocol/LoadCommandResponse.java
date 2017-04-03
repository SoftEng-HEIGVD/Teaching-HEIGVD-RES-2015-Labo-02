package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Dbnsky & Iamfonky
 */
public class LoadCommandResponse
{
    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponse(){
    }

    public LoadCommandResponse(String status, int nbNewStudents){
        this.status = status;
        this.numberOfNewStudents = nbNewStudents;
    }

    public String getStatus(){
        return status;
    }

    public int getNumberOfNewStudents(){
        return numberOfNewStudents;
    }
}
