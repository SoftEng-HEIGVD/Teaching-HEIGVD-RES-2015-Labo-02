/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * @author guiguismall
 */
public class LoadCommandResponse {
    private String status;
    private int numberOfStudents;
    
    public LoadCommandResponse() {
        
    }
    
    public LoadCommandResponse(String status, int numberOfStudents) {
        this.status = status;
        this.numberOfStudents = numberOfStudents;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }
    
    public int getNumberOfStudents() {
        return numberOfStudents;
    }
    
    
}
