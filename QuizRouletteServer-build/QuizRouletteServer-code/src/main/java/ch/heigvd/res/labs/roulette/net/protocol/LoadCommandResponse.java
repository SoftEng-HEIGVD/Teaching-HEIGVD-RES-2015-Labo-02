package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by gaetan on 01.04.17.
 */
public class LoadCommandResponse {
    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponse(String status, int numberOfStudents) {
        this.status = status;
        this.numberOfNewStudents = numberOfStudents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public void setNumberOfNewStudents(int numberOfStudents) {
        this.numberOfNewStudents = numberOfStudents;
    }
}
