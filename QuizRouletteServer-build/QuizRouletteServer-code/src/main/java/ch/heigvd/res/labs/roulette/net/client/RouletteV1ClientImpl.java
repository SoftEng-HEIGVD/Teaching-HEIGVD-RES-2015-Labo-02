package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private Socket clientSocket ;
  private BufferedReader reader;
  private PrintWriter writer;


  @Override
  public void connect(String server, int port) throws IOException {

    clientSocket = new Socket(server, port) ;

    reader = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()) );
    writer = new PrintWriter( new OutputStreamWriter(clientSocket.getOutputStream()) );
    reader.readLine();
  }

  @Override
  public void disconnect() throws IOException {

    if (isConnected()) {
      writer.println(RouletteV1Protocol.CMD_BYE);
      writer.flush();
    }
  }


  @Override
  public boolean isConnected() {
    return clientSocket != null && clientSocket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {

    writer.println(RouletteV1Protocol.CMD_LOAD);
    writer.flush();
    reader.readLine();

    writer.println(fullname);
    writer.flush();
    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();
    reader.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {

    writer.println(RouletteV1Protocol.CMD_LOAD);
    writer.flush();

    for (Student s : students) {
      writer.println(s.getFullname());
      writer.flush();
    }
    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {

    writer.println(RouletteV1Protocol.CMD_RANDOM);
    writer.flush();
    String answer = reader.readLine();
    RandomCommandResponse parsedAnswer = JsonObjectMapper.parseJson(answer, RandomCommandResponse.class);

    if (parsedAnswer.getError() != null )
      throw new EmptyStoreException();

    return new Student(parsedAnswer.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();
    String answer = reader.readLine();
    InfoCommandResponse parsedAnswer = JsonObjectMapper.parseJson(answer, InfoCommandResponse.class);
    return parsedAnswer.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    return RouletteV1Protocol.VERSION;
  }



}
