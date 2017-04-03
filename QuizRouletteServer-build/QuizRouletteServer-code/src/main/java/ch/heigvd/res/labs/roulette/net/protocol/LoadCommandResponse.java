package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * @author silver kameni & nguefack zacharie
 */

public class LoadCommandResponse {
    private String status;
    private int numberOfNewStudents;
    
     
    /*
    * definition of constructors 
    */
     public LoadCommandResponse() {
    }
    public LoadCommandResponse(String status, int numberOfNewStudents) {
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
    }

     /*
     * methods to Get a current status and 
     * number Of New Students
     */
    public String getStatus() {
        return status;
    }
    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    /*
    *methods to Set a number Of New Students
    *and to Set a Status
    */
    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }
     public void setStatus(String status) {
        this.status = status;
    }
  
}
