package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by matthieu on 3/25/17.
 */
public class LoadCommandResponse
{
   private String status;
   private int numberOfStudents;

   public LoadCommandResponse(String status, int numberOfStudents)
   {
      this.status = status;
      this.numberOfStudents = numberOfStudents;
   }

   public String getStatus()
   {
      return status;
   }

   public void setStatus(String status)
   {
      this.status = status;
   }

   public int getNumberOfStudents()
   {
      return numberOfStudents;
   }

   public void setNumberOfStudents(int numberOfStudents)
   {
      this.numberOfStudents = numberOfStudents;
   }
}
