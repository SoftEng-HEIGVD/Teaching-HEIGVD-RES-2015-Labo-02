package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
    
    private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());
    
    @Override
    public void clearDataStore() throws IOException {
        printWriter.println(RouletteV2Protocol.CMD_CLEAR);
        printWriter.flush();

        LOG.log(Level.INFO, bufferedReader.readLine());
    }

  @Override
  public List<Student> listStudents() throws IOException {
        printWriter.println(RouletteV2Protocol.CMD_LIST);
        printWriter.flush();
        
        String result = bufferedReader.readLine();
        StudentsList answer = JsonObjectMapper.parseJson(result, StudentsList.class);
        
        List<Student> expResult = answer.getStudents();
        
        return expResult;
  }
  
  /**
    * load a list of students to the server
    * @param students list of students
    * @throws IOException
    */
   @Override
   public void loadStudents(List<Student> students) throws IOException {
      printWriter.println(RouletteV2Protocol.CMD_LOAD);
      printWriter.flush();

      LOG.log(Level.INFO, bufferedReader.readLine());

      for (Student student : students) {
         printWriter.println(student.getFullname());
         printWriter.flush();
      }

      printWriter.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      printWriter.flush();
      
      LOG.log(Level.INFO, bufferedReader.readLine());
   }
   
   @Override
   public int getNumberOfStudents() throws IOException {
      return getInfo().getNumberOfStudents();
   }

   /**
    * get the protocol version from the server
    * @return the version of the server
    * @throws IOException 
    */
   @Override
   public String getProtocolVersion() throws IOException {
      return getInfo().getProtocolVersion();
   }

   /**
    * gets the InfoCOmmandResponse by asking infos to the server
    * @return InfoCommandResponse
    * @throws IOException 
    */
   private InfoCommandResponse getInfo() throws IOException {
      printWriter.println(RouletteV2Protocol.CMD_INFO);
      printWriter.flush();
      
      String result = bufferedReader.readLine();
      
      return JsonObjectMapper.parseJson(result, InfoCommandResponse.class);
   }
   
   @Override
   public void disconnect() throws IOException {
      if (isConnected()) {
        printWriter.println(RouletteV2Protocol.CMD_BYE);
        printWriter.flush();

        LOG.log(Level.INFO, bufferedReader.readLine());

        bufferedReader.close();
        printWriter.close();
        serverSocket.close();

        bufferedReader = null;
        printWriter = null;
        serverSocket = null;
      }
   }
}
