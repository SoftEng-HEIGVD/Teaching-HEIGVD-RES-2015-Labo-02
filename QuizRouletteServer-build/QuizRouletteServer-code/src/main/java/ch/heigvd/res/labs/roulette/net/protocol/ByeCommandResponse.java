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
public class ByeCommandResponse {
    private String status;
    private int nbCommande;
    
    public void setStatus(String status){
        this.status=status;
    }
    public void setNbCommande(int nbCommande){
        this.nbCommande=nbCommande;
    }
    
    public String getStatus(){
        return status;
    }
    
    public int getNbCommande(){
        return nbCommande;
    }
}
