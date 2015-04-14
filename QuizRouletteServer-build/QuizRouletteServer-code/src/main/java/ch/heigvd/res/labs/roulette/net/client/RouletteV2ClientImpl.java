package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Mélanie Huck
 * @author James Nolan
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
    
  // Logger...
  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

  @Override
  public void clearDataStore() throws IOException {
      if (!isConnected()) {
          throw new IOException("Must be connected to clear store!");
      }
      // send CLEAR command
      out.println(RouletteV2Protocol.CMD_CLEAR);
      out.flush();
      
      // Skip the server's answer
      in.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException {
      // Only if connected
      if (!isConnected()) {
          throw new IOException("Must be connected to retrieve student list!");
      }
      
      // Send LIST command
      out.println(RouletteV2Protocol.CMD_LIST);
      out.flush();
      
      // Get result and parse JSON
      String result = in.readLine();
      
      StudentsList list = JsonObjectMapper.parseJson(result, StudentsList.class);
      
      return list.getStudents();
  }
  
  
  @Override
  public String getProtocolVersion() throws IOException {
    return RouletteV2Protocol.VERSION;
  }
  
}
