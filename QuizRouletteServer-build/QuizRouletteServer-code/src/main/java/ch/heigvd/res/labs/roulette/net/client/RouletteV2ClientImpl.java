package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  @Override
  public void clearDataStore() throws IOException {
    out.println("clear");
    out.flush();
    in.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException {
    String answer;
    
    out.println("list");
    out.flush();
    answer = in.readLine();

    return JsonObjectMapper.parseJson(answer, StudentsList.class).getStudents();
  }
  
}
