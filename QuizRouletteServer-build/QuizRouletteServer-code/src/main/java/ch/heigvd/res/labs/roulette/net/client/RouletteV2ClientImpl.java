package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liecht, modified by Mathieu Monteverde
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
   
   private ByeCommandResponse byeResponse = null;
   private LoadCommandResponse loadResponse = null;

  @Override
  public void clearDataStore() throws IOException {
     // Send clear command
     sendDataToServer(RouletteV2Protocol.CMD_CLEAR);
     readServerResponse();
  }
  
  @Override
  public void disconnect() throws IOException {
     sendDataToServer(RouletteV2Protocol.CMD_BYE);
     
     String result = readServerResponse();
     byeResponse = JsonObjectMapper.parseJson(result, ByeCommandResponse.class);
     
     /*
     TODO: Read the server response
     
     */
     
     socket().close();
  }
  
  @Override
   public void loadStudent(String fullname) throws IOException {
      // Notify the server we are going to send him some data
      sendDataToServer(RouletteV1Protocol.CMD_LOAD);
      readServerResponse();
      // Send Student fullname
      sendDataToServer(fullname);
      // Notify the server we ended transmitting data
      sendDataToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      
      String result = readServerResponse();
      loadResponse = JsonObjectMapper.parseJson(result, LoadCommandResponse.class);
      
      // Do something with the response later ? 
      // Nothing was implemented, but it would have been maybe by throwing an exception
      // if not a success or something
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException {
      // Notify the server we are going to send him some data
      sendDataToServer(RouletteV1Protocol.CMD_LOAD);
      readServerResponse();
      
      // Send all student information to the server
      for (Student s : students) {
         sendDataToServer(s.getFullname());
      }
      
      // Notify the server we ended transmitting data
      sendDataToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      
      String result = readServerResponse();
      loadResponse = JsonObjectMapper.parseJson(result, LoadCommandResponse.class);
      
      // Same issue than with the single loadStudent method
   }

  @Override
  public List<Student> listStudents() throws IOException {
     // Send command
     sendDataToServer(RouletteV2Protocol.CMD_LIST);
     // Read response from server
     String result = readServerResponse();
     // Convert into an object
     ListCommandResponse response = JsonObjectMapper.parseJson(result, ListCommandResponse.class);
     // Return list of students
     return new ArrayList<Student>(Arrays.asList(response.getStudents()));
     
  }
  
}
