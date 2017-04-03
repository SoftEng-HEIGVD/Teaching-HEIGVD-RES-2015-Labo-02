package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Christopher Meier
 * @author Daniel Palumbo
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
  private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(RouletteV2ClientImpl.class.getName());

  @Override
  public void clearDataStore() throws IOException {
    os.println(RouletteV2Protocol.CMD_CLEAR);
    os.flush();
    // Test to check if server follow the convention ?
    if(!is.readLine().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
      throw new IOException("The server doesn't follow the conventions...");
    }
  }

  @Override
  public List<Student> listStudents() throws IOException {
    os.println(RouletteV2Protocol.CMD_LIST);
    os.flush();

    StudentsList sl = JsonObjectMapper.parseJson(is.readLine(), StudentsList.class);

    return sl.getStudents();
  }

  protected void endLoad() throws IOException {
    LoadCommandResponse lcr = JsonObjectMapper.parseJson(is.readLine(), LoadCommandResponse.class);
    if(lcr.getStatus().equalsIgnoreCase("success")) {
      LOG.log(Level.INFO, "Added successfully 0 students");
    }
    else {
      LOG.severe("Error, student not added !");
    }
  }
  
}
