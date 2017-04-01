package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by Blue Spring on 27.03.2017.
 */
public class LoadCommandResponse {

    private int numberOfStudents;
    private String status;

    public LoadCommandResponse(String status, int numberOfStudents) {
        this.status = status;
        this.numberOfStudents = numberOfStudents;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public String getStatus() {
        return status;
    }

}
