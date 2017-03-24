package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {
  private Socket clientS;
  private DataOutputStream outToServer;
  private BufferedReader inFromServer;
  private JsonObjectMapper jsm;


  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  @Override
  public void connect(String server, int port) throws IOException {
    clientS = new Socket(server, port);
    outToServer = new DataOutputStream(clientS.getOutputStream());
    inFromServer = new BufferedReader(new InputStreamReader(clientS.getInputStream()));
    jsm = new JsonObjectMapper();
  }

  @Override
  public void disconnect() throws IOException {
    outToServer.writeChars(RouletteV1Protocol.CMD_BYE);
    clientS.close();
    outToServer.close();
    inFromServer.close();
  }

  @Override
  public boolean isConnected() {
    if (clientS != null) {
      return clientS.isConnected();
    }
      return false;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    outToServer.writeChars(fullname);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    outToServer.writeChars(RouletteV1Protocol.CMD_LOAD);
    for (Student s : students) {
      loadStudent(s.getFullname());
    }
    outToServer.writeChars(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    if (getNumberOfStudents() == 0) {
      throw new EmptyStoreException();
    }
    outToServer.writeChars(RouletteV1Protocol.CMD_RANDOM);
    return jsm.parseJson(inFromServer.readLine(), Student.class);
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    outToServer.writeChars(RouletteV1Protocol.CMD_INFO);
    return jsm.parseJson(inFromServer.readLine(), InfoCommandResponse.class).getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    outToServer.writeChars(RouletteV1Protocol.CMD_INFO);
    return jsm.parseJson(inFromServer.readLine(), InfoCommandResponse.class).getProtocolVersion();
  }
}
