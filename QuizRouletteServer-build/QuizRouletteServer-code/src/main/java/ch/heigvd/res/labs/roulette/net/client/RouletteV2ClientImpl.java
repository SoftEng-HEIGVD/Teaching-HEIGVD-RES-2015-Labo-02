package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liecht, modified by Mathieu Monteverde
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  @Override
  public void clearDataStore() throws IOException {
     // Send clear command
     sendDataToServer(RouletteV2Protocol.CMD_CLEAR);
     readServerResponse();
  }
  
  @Override
  public void disconnect() throws IOException {
     sendDataToServer(RouletteV2Protocol.CMD_BYE);
     
     String response = readServerResponse();
     
     /*
     TODO: Read the server response
     
     */
     
     socket().close();
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
     return Arrays.asList(response.getStudents());
     
  }
  
}
