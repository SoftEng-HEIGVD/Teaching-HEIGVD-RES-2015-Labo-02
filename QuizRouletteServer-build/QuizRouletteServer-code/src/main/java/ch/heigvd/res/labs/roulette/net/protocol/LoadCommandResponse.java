/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 *
 * @author Marom
 */
@JsonInclude(Include.NON_NULL)
public class LoadCommandResponse {
    private String status;
    private int numberOfStudents;
    
    public void setStatus(String status){
        this.status=status;
    }
    
    public void setNumberOfStudents(int nbStudents){
        this.numberOfStudents=nbStudents;
    }
    public String getStatus(){
        return status;
    }
    public int getNumberOfStudents(){
        return numberOfStudents;
    }
}
