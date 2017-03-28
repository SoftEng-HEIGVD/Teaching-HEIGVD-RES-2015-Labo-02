package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by Michael on 28.03.2017.
 */
public class LoadCommandResponse {

    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponse(String status, int numberOfNewStudents){
        this.status=status;
        this.numberOfNewStudents = numberOfNewStudents;
    }



    public String getStatus(){
        return status;
    }

    public int getNumberOfNewStudents(){
        return numberOfNewStudents;
    }

}
