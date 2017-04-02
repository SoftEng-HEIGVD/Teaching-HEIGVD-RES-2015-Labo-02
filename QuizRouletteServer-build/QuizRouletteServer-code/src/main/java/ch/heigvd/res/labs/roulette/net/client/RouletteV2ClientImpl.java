package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author François Quellec,
 *         Pierre-Samuel Rochat
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  @Override
  public void clearDataStore() throws IOException {
    outToServer.writeBytes(RouletteV2Protocol.CMD_CLEAR + '\n');
    outToServer.flush();
    // Reading the clear response
    inFromServer.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException {

    List students = new LinkedList<Student>();

    int size = getNumberOfStudents();

    outToServer.writeBytes(RouletteV2Protocol.CMD_LIST + '\n');
    for(int i = 0; i < size; i++) {
      students.add(new Student(inFromServer.readLine()));
    }

    return students;
  }
}
