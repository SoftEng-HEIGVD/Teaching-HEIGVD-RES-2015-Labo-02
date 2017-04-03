package ch.heigvd.res.labs.roulette.net.client;


import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

import java.io.*;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @authors Ludovic Richard, Luana Martelli
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  @Override
  public void clearDataStore() throws IOException {
    writer.println(RouletteV2Protocol.CMD_CLEAR);
    writer.flush();
    String answer = reader.readLine();
    if (!answer.equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
      System.out.println("Error while clearing data");
    }
  }

  @Override
  public List<Student> listStudents() throws IOException {
    writer.println(RouletteV2Protocol.CMD_LIST);
    writer.flush();
    String answer = reader.readLine();
    ListCommandResponse students = JsonObjectMapper.parseJson(answer, ListCommandResponse.class);
    return students.getStudents();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    /* Sends command */
    writer.println(RouletteV2Protocol.CMD_LOAD);
    writer.flush();

    /* Reads the answer */
    reader.readLine();

    /* Add students */
    for (Student s : students) {
      writer.println(s.getFullname());
      writer.flush();
    }

    /* End of command */
    writer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();

    reader.readLine();

  }

  @Override
  public void loadStudent(String fullname) throws IOException {

    /* Sends the command */
    writer.println(RouletteV2Protocol.CMD_LOAD);
    writer.flush();
    /* Reads the answer */
    String response = reader.readLine();

    if (!response.equals(RouletteV2Protocol.RESPONSE_LOAD_START)) {
      System.out.println("Error while using" + RouletteV2Protocol.CMD_LOAD + "  command");
      return;
    }

    /* Writes the name of the student */
    writer.println(fullname);
    writer.flush();

    /* End of data */
    writer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();

    /* Reads server's message */
    response = reader.readLine();

    if (!response.equals(RouletteV2Protocol.RESPONSE_LOAD_DONE)) {
      System.out.println("Error at the end of process ");
    }

  }

  @Override
  public void disconnect() throws IOException {
    if (socket == null || !isConnected()) {
      return;
    }

    writer.println(RouletteV1Protocol.CMD_BYE);
    writer.flush();

    reader.readLine();

    /* Close connexion */
    reader.close();
    writer.close();
    socket.close();

    socket = null;
  }

}
