/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * @author mathieu
 */
public class LoadCommandResponse {
   private final String status;
   private final int numberOfNewStudents;
   
   public LoadCommandResponse(String status, int numberOfNewStudents) {
      this.status = status;
      this.numberOfNewStudents = numberOfNewStudents;
   }
   
   public String getStatus() {
      return status;
   }
   
   public int getNumberOfNewStudents() {
      return numberOfNewStudents;
   }
}
