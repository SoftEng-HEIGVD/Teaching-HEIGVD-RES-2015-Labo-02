package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by Max Caduff on 29.03.17.
 */

public class LoadCommandResponse {

    private String status = "just initialized";
    private int numberOfNewStudents = -1;

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
        status = "success";
    }

    public void setError() {
        status = "error";
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }
}
