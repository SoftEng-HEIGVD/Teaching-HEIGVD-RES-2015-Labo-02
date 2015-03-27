/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * @author Zoruk
 */
public class ByeCommandResponse {
    private String status;
    private int numberOfCommands;

    public ByeCommandResponse(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the numberOfNewStudents
     */
    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    /**
     * @param numberOfCommands the numberOfCommands to set
     */
    public void setNumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }
}
