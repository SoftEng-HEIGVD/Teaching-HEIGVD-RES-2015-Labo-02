package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * @author dons
 */
public class LoadCommandResponse {
    private String status;
    private int numberOfNewStudents = 0;
    
    public int getNumberOfNewStudents(){
        return numberOfNewStudents;
    }
    
    public void setnumberOfNewStudents(int student){
        numberOfNewStudents = student;
    }
        
    public String getStatus(){
        return status;
    }
    
    public void setStatus(String newStatus){
        status = newStatus;
    }
}
