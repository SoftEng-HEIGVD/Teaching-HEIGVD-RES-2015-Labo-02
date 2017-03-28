package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * We will use this class to serialize or deserialize the response of the server
 * to the command LOAD
 * @author Julien Brêchet
 * @author Adrien Marco
 */

public class LoadCommandResponse {

    private String status;
    private int numberStudents;

    public int getNumberStudents(){
        return numberStudents;
    }

    public void setNumberStudents(int number){
        numberStudents = number;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

}