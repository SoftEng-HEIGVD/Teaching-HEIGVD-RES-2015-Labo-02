package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 * @author Luca Sivillica
 * @author Dany Tchente
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  private static String CHARSET = "UTF-8";

  private Socket clientSocket;
  private BufferedReader reader;
  private PrintWriter writer;

  @Override
  public void connect(String server, int port) throws IOException {
    clientSocket = new Socket(server, port);

    reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), CHARSET));
    writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), CHARSET));

    reader.readLine(); // Read the welcome message of the server
  }

  @Override
  public void disconnect() throws IOException {
    writer.println(RouletteV1Protocol.CMD_BYE);
    writer.flush();

    reader.close();
    writer.close();

    clientSocket.close();
  }

  @Override
  public boolean isConnected() {
    if (clientSocket != null) {
      return clientSocket.isConnected();
    }

    return false;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    writer.println(RouletteV1Protocol.CMD_LOAD);
    writer.flush();

    reader.readLine(); // Read the message : "Send your data [end with ENDOFDATA]" of the server

    writer.println(fullname);
    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();

    reader.readLine(); // Read the message "DATALOADED" of the server
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    writer.println(RouletteV1Protocol.CMD_LOAD);
    writer.flush();

    reader.readLine(); // Read the message : "Send your data [end with ENDOFDATA]" of the server

    for (Student student : students) {
      writer.println(student.getFullname());
    }

    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();

    reader.readLine(); // Read the message "DATALOADED" of the server
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    String serverResponseJson;
    RandomCommandResponse serverResponse;

    writer.println(RouletteV1Protocol.CMD_RANDOM);
    writer.flush();

    serverResponseJson = reader.readLine();

    serverResponse = JsonObjectMapper.parseJson(serverResponseJson, RandomCommandResponse.class);

    /* if there is an error then we throw an exception */
    if (!serverResponse.getError().isEmpty()) {
      throw new EmptyStoreException();
    }

    return new Student(serverResponse.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    String serverResponseJson;
    InfoCommandResponse serverResponse;

    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();

    serverResponseJson = reader.readLine();

    serverResponse = JsonObjectMapper.parseJson(serverResponseJson, InfoCommandResponse.class);

    return serverResponse.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    String serverResponseJson;
    InfoCommandResponse serverResponse;

    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();

    serverResponseJson = reader.readLine();

    serverResponse = JsonObjectMapper.parseJson(serverResponseJson, InfoCommandResponse.class);

    return serverResponse.getProtocolVersion();
  }

}
