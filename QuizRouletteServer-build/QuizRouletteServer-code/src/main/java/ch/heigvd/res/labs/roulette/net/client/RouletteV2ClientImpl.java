package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  @Override
  public void clearDataStore() throws IOException
  {
    writeLine(RouletteV2Protocol.CMD_CLEAR);
    
    // Check if the server is ready.
    if (!readLine().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) 
    {
      throw new IOException("Incorrect server response");
    }
  }

  @Override
  public List<Student> listStudents() throws IOException
  {
      writeLine(RouletteV2Protocol.CMD_LIST);
      
      StudentsList students = JsonObjectMapper.parseJson(readLine(), StudentsList.class);
      
      return students.getStudents();
  }
  
}
