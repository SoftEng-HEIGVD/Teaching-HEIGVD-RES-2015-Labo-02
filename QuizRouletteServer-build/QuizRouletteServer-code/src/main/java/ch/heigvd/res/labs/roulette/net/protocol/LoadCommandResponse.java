/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Gabriel Luthier
 * @author Maxime Guillod
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoadCommandResponse {
    
    private String status;
    private int numberOfNewStudents;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }
    
    
    
    public LoadCommandResponse() {
    }
    
    public LoadCommandResponse(String status, int numberOfNewStudents) {
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
    }
}
