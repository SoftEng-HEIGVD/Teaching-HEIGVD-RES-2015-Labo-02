package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * @author Henrik Akesson & Fabien Salathe
 */
public class LoadCommandResponse {

    private String status;

    private int numberNewStudents;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberNewStudents() {
        return numberNewStudents;
    }

    public void setNumberNewStudents(int numberNewStudents) {
        this.numberNewStudents = numberNewStudents;
    }
}