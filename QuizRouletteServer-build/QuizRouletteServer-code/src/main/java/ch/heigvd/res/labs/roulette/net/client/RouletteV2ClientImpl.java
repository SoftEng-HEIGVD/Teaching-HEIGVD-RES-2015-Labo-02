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
 * @author Kevin Moreira
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client
{

  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());
  @Override
  public void clearDataStore() throws IOException {
    LOG.log(Level.FINE, "clear the data store");
    writer.println("CLEAR");
    writer.flush();
    reader.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException
  {
    LOG.log(Level.FINE, "Client is asking the list of students");
    writer.println("LIST");
    writer.flush();
    StudentsList listStu = JsonObjectMapper.parseJson(reader.readLine(), StudentsList.class);

    // Return the extracted list from the "StudentList" object
    return listStu.getStudents();
  }
  
}
