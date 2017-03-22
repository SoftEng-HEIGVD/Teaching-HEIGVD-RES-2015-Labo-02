/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * @author dons
 */
public class ByeCommandResponse {
    private String status;
    private int numberOfCommands = 0;
    
    public int getnumberOfCommands(){
        return numberOfCommands;
    }
    
    public void increasenumberOfCommands(){
        ++numberOfCommands;
    }
    
    public String getStatus(){
        return status;
    }
    
    public void setStatus(String newStatus){
        status = newStatus;
    }
}
