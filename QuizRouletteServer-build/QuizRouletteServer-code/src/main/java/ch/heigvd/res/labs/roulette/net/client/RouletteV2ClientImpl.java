package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

  @Override
  public void loadStudent(String fullname) throws IOException {
    super.loadStudent(fullname);

    //Read the server response
    String info = bReader.readLine();
  }

  @Override
  public void clearDataStore() throws IOException {
    pWriter.write(RouletteV2Protocol.CMD_CLEAR + "\n");
    pWriter.flush();

    String info = bReader.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException {
    pWriter.write(RouletteV2Protocol.CMD_LIST + "\n");
    pWriter.flush();

    String list = bReader.readLine();
    return JsonObjectMapper.parseJson(list, StudentsList.class).getStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    return RouletteV2Protocol.VERSION;
  }
}
