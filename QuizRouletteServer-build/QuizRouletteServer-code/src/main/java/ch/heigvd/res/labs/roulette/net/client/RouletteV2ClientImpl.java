package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 * The difference with the protocol v1 is we have a specific message when we load, by and info
 * and we can now clear and list the students
 *
 * @author Olivier Liechti
 * @author Julien Brêchet
 * @author Adrien Marco
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {


  @Override
  public void clearDataStore() throws IOException {
    // We send the CLEAR command to tell the server to clear its data.
    pw.println(RouletteV2Protocol.CMD_CLEAR);
    pw.flush();
    br.readLine(); // don't forget to read the server response
  }

  @Override
  public List<Student> listStudents() throws IOException {
    // We send the LIST command to tell the server to return us a list of all the students.
    pw.println(RouletteV2Protocol.CMD_LIST);
    pw.flush();
    StudentsList students_list = JsonObjectMapper.parseJson(br.readLine(), StudentsList.class);
    return students_list.getStudents();
  }

}