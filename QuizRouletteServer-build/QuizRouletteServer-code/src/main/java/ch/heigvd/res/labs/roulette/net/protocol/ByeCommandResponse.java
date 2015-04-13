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
public class ByeCommandResponse {
    private String status;
    private int numberOfCommands;
    
    public void setStatus(String status){
        this.status=status;
    }
    public void setNumberOfCommands(int nbCommande){
        this.numberOfCommands=nbCommande;
    }
    
    public String getStatus(){
        return status;
    }
    
    public int getNumberOfCommands(){
        return numberOfCommands;
    }
}
