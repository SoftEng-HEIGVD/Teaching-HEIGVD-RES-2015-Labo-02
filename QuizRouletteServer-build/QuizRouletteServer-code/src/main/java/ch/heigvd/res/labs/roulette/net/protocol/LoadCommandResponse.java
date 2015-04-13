/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;


/**
 *
 * @author Marom
 */
public class LoadCommandResponse {
    private String status;
    private int nbStudents;
    
    public void setStatus(String status){
        this.status=status;
    }
    
    public void setNbStudents(int nbStudents){
        this.nbStudents=nbStudents;
    }
    public String getStatus(){
        return status;
    }
    public int getNbStudents(){
        return nbStudents;
    }
}
