package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * Modified by: guiguismall, yoaaaarp
 * 
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  @Override
  public void disconnect() throws IOException {
      out.write("BYE\n");
      out.flush();
      
      //read the answer
      in.readLine();
      clientSocket.close();
  }
  
  @Override
  public void loadStudent(String fullname) throws IOException {
      // handles empty name
      if(fullname.isEmpty())
          return;
      
      // writes commands
      out.write("LOAD\n");
      out.write(fullname + "\n");
      out.write("ENDOFDATA\n");
      out.flush();
      
      // reads server response
      in.readLine();
  }
  
  @Override
  public void loadStudents(List<Student> students) throws IOException {
      // writes commands
      out.write("LOAD\n");
      for(Student s : students) {
          out.write((s.getFullname() + "\n"));
      }
      out.write("ENDOFDATA\n");
      out.flush();
      
      // reads server response
      in.readLine();
  }
    
  @Override
  public void clearDataStore() throws IOException {
      // send clear command
      out.write("CLEAR\n");
      out.flush();
      
      // read server reply
      in.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException {
      // send list command
      out.write("LIST\n");
      out.flush();
      
      // get result
      String result = in.readLine();
      
      // parse result
      List<Student> students = JsonObjectMapper.parseJson(result, ListCommandResponse.class).getStudents();
        
      return students;
  }
  
  @Override
  public String getProtocolVersion() throws IOException {
    return RouletteV2Protocol.VERSION;
  }
  
}
