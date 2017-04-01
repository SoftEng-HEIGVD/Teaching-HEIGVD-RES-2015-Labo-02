package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Class used to serialize/deserialize the response sent by the server when
 * processing the `LOAD` command defined in the V2 protocol.
 *
 * @author Lucas ELISEI (faku99)
 * @author David TRUAN  (Daxidz)
 */
public class LoadCommandResponse {

    // The status of the command.
    private String status;

    // The number of new students.
    private int numberOfNewStudents;

    /**
     * Constructor.
     *
     * @param status              Status of the command.
     * @param numberOfNewStudents Number of new students.
     */
    public LoadCommandResponse(String status, int numberOfNewStudents) {
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
    }

    /**
     * @return The number of new students.
     */
    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    /**
     * @return The status of the command.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the number of new students.
     *
     * @param numberOfNewStudents Number of new students.
     */
    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }

    /**
     * Sets the status of the command.
     *
     * @param status Status of the command.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
