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
public class LoadCommandResponse {
    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponse(String status, int numberOfNewStudents) {
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
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
    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    /**
     * @param numberOfNewStudents the numberOfNewStudents to set
     */
    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }
}
