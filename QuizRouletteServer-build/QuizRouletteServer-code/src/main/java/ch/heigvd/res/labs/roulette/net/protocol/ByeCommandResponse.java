package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * @author silver kameni & nguefack zacharie
 */
public class ByeCommandResponse {
    private String status;
    private int numberOfCommands;
    
    /*
    * definition of constructors 
    */
    public ByeCommandResponse() {}
    public ByeCommandResponse(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }
    /*
    *methods to Set a number of commands
    *and to Set a Status
    */
    public void setnumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }
     public void setStatus(String status) {
        this.status = status;
    }
     /*
     * methods to Get a current status and 
     * number of commands
     */
     public String getStatus() {
        return status;
    }
      public int getnumberOfCommands() {
        return numberOfCommands;
    }

   
    
}
