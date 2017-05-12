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
 * @author Loan Lassalle
 * @author Tano Iannetta
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  /**
   * @brief clearDataStore removes all students
   * @throws IOException with BufferedReader and PrintWriter
   */
  @Override
  public void clearDataStore() throws IOException {
    send(RouletteV2Protocol.CMD_CLEAR);
    receive();
  }

  /**
   * @brief listStudents gets back list of students
   * @return list of students
   * @throws IOException
   */
  @Override
  public List<Student> listStudents() throws IOException {
    send(RouletteV2Protocol.CMD_LIST);

    StudentsList responseList = JsonObjectMapper.parseJson(receive(), StudentsList.class);

    return responseList.getStudents();
  }
}