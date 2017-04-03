package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Valentin Finini
 * @author Mika Pagani
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  @Override
  public void clearDataStore() throws IOException {
    send(RouletteV2Protocol.CMD_CLEAR);
    getServerResponseReader().readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException {
    send(RouletteV2Protocol.CMD_LIST);

    StudentsList sl = JsonObjectMapper.parseJson(getServerResponseReader().readLine(), StudentsList.class);
    return sl.getStudents();
  }

  public int loadStudents(List<Student> students) throws IOException {
    int numberOfNewStudents = 0;

    send(RouletteV1Protocol.CMD_LOAD);

    //Flush server response
    getServerResponseReader().readLine();

    for(Student student : students) {
      send(student.getFullname());
    }

    send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    LoadCommandResponse lcr = JsonObjectMapper.parseJson(getServerResponseReader().readLine(), LoadCommandResponse.class);

    if(lcr.getStatus().equals("success"))
      numberOfNewStudents = lcr.getNumberOfNewStudents();

    return numberOfNewStudents;
  }
}
