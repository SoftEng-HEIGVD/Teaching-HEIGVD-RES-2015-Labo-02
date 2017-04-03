package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * this class provide a object to map in JSON for the LOAD command.
 * @author Gallouche & dipietroa
 */
public class LoadCommandResponse {
    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponse(String status, int numberOfNewStudents){
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
    }

    public String getStatus(){return status;}

    public int getNumberOfNewStudents(){return numberOfNewStudents;}

    public void setStatus(String status){this.status = status;}

    public void setNumberOfNewStudents(int numberOfNewStudents){this.numberOfNewStudents = numberOfNewStudents;}
}
