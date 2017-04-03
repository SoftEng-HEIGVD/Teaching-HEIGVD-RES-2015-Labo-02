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
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 * @author Valentin Finini
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private Socket connection;

  private BufferedReader serverResponseReader;
  private PrintWriter clientServerRequester;

  @Override
  public void connect(String server, int port) throws IOException {
    connection = new Socket(server, port);

    serverResponseReader = new BufferedReader(
      new InputStreamReader(connection.getInputStream())
    );

    clientServerRequester = new PrintWriter(
      new OutputStreamWriter(connection.getOutputStream())
    );

    //Flush server Hello
    serverResponseReader.readLine();
  }

  @Override
  public void disconnect() throws IOException {
    serverResponseReader.close();
    clientServerRequester.close();
    connection.close();
  }

  @Override
  public boolean isConnected() {
    if(connection != null)
      return !connection.isClosed();
    else
      return false;
  }

  @Override
  public int loadStudent(String fullname) throws IOException {
    return loadStudents(Collections.singletonList(new Student(fullname)));
  }

  @Override
  public int loadStudents(List<Student> students) throws IOException {
    send(RouletteV1Protocol.CMD_LOAD);

    //Flush server response
    serverResponseReader.readLine();

    for(Student student : students) {
      send(student.getFullname());
    }

    send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    serverResponseReader.readLine();

    return 0;
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    send(RouletteV1Protocol.CMD_RANDOM);

    RandomCommandResponse response = JsonObjectMapper.parseJson(serverResponseReader.readLine(), RandomCommandResponse.class);

    if(response.getError() != null)
      throw new EmptyStoreException();

    return new Student(response.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    send(RouletteV1Protocol.CMD_INFO);

    InfoCommandResponse response = JsonObjectMapper.parseJson(serverResponseReader.readLine(), InfoCommandResponse.class);
    return response.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    send(RouletteV1Protocol.CMD_INFO);

    InfoCommandResponse response = JsonObjectMapper.parseJson(serverResponseReader.readLine(), InfoCommandResponse.class);
    return response.getProtocolVersion();
  }

  public void send(String command) throws IOException {
    clientServerRequester.println(command);

    //Flush the stream so we're sending it
    clientServerRequester.flush();
  }

  public PrintWriter getClientServerRequester() {
    return clientServerRequester;
  }

  public BufferedReader getServerResponseReader() {
    return serverResponseReader;
  }
}
