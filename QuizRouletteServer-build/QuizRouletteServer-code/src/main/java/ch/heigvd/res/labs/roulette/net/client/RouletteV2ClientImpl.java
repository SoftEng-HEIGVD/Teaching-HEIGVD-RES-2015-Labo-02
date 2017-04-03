package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
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
    outToServer.writeBytes(RouletteV2Protocol.CMD_LIST + "\n");
    outToServer.flush();

    ListCommandResponse responseList = JsonObjectMapper.parseJson(inFromServer.readLine(), ListCommandResponse.class);
    return responseList.getStudents();
  }
}
