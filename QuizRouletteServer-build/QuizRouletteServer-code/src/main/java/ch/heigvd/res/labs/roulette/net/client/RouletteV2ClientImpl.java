package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.IIOException;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  @Override
  public void clearDataStore() throws IOException {
      writer.println(RouletteV2Protocol.CMD_CLEAR);
      writer.flush();
      String line;
      line=lineReader();
      if(!line.equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE)){
          throw new IOException("server response not correct....");
      }
      
  }

  @Override
  public List<Student> listStudents() throws IOException {
      writer.println(RouletteV2Protocol.CMD_LIST);
      writer.flush();
      StudentsList s = JsonObjectMapper.parseJson(lineReader(), StudentsList.class);
      return s.getStudents();
  }
  
}
