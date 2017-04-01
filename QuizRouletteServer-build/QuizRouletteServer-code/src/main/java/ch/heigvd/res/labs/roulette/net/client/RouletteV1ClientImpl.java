package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import javax.sound.sampled.Line;
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
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  private Socket clientSocket;

  protected BufferedReader reader;
  protected PrintWriter writer;

  @Override
  public void connect(String server, int port) throws IOException {
    clientSocket = new Socket(server, port);
    reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    reader.readLine();
  }

  @Override
  public void disconnect() throws IOException {
    writer.flush();
    clientSocket.close();
  }

  @Override
  public boolean isConnected() {
    return clientSocket != null && !clientSocket.isClosed();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    writer.println(RouletteV1Protocol.CMD_LOAD);
    writer.println(fullname);
    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();
    reader.readLine();
    reader.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    writer.println(RouletteV1Protocol.CMD_LOAD);
    for(Student student : students)
      writer.println(student.getFullname());
    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();
    reader.readLine();
    reader.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    writer.println(RouletteV1Protocol.CMD_RANDOM);
    writer.flush();
    String line = reader.readLine();
    RandomCommandResponse response = JsonObjectMapper.parseJson(line, RandomCommandResponse.class);
    String error = response.getError();
    if(error != null)
      throw new EmptyStoreException();
    String fullname = response.getFullname();
    return new Student(fullname);
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();
    String line = reader.readLine();
    InfoCommandResponse response = JsonObjectMapper.parseJson(line, InfoCommandResponse.class);
    return response.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();
    String line = reader.readLine();
    InfoCommandResponse response = JsonObjectMapper.parseJson(line, InfoCommandResponse.class);
    return response.getProtocolVersion();
  }



}
