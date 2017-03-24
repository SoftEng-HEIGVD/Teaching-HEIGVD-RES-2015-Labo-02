package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;


/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {
  private Socket clientS;
  private PrintWriter outToServer;
  private BufferedReader inFromServer;

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  @Override
  public void connect(String server, int port) throws IOException {
    clientS = new Socket(server, port);
    outToServer = new PrintWriter(clientS.getOutputStream());
    inFromServer = new BufferedReader(new InputStreamReader(clientS.getInputStream()));
    inFromServer.readLine(); // To pass the fisrt line that says "Hello..."
  }

  @Override
  public void disconnect() throws IOException {
    outToServer.println(RouletteV1Protocol.CMD_BYE);
    outToServer.close();
    inFromServer.close();
    clientS.close();
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
    outToServer.println(RouletteV1Protocol.CMD_LOAD);
    outToServer.println(fullname);
    outToServer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    outToServer.flush();
    inFromServer.readLine(); //Flushing the SEND and ENDOFDATA responses from server
    inFromServer.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    outToServer.println(RouletteV1Protocol.CMD_LOAD);
    for (Student s : students) {
      outToServer.println(s.getFullname());
    }
    outToServer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    outToServer.flush();
    inFromServer.readLine(); //Flushing the SEND and ENDOFDATA responses from server
    inFromServer.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    if (getNumberOfStudents() == 0) {
      throw new EmptyStoreException();
    } else {
      outToServer.println(RouletteV1Protocol.CMD_RANDOM);
      outToServer.flush();
      return JsonObjectMapper.parseJson(inFromServer.readLine(), Student.class);
    }
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    outToServer.println(RouletteV1Protocol.CMD_INFO);
    outToServer.flush();
    InfoCommandResponse info = JsonObjectMapper.parseJson(inFromServer.readLine(), InfoCommandResponse.class);
    return info.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    outToServer.println(RouletteV1Protocol.CMD_INFO);
    outToServer.flush();
    InfoCommandResponse info = JsonObjectMapper.parseJson(inFromServer.readLine(), InfoCommandResponse.class);
    return info.getProtocolVersion();
  }
}
