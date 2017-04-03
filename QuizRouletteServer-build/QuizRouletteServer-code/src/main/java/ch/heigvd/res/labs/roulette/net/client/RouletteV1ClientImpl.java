package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

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

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  private Socket clientSocket;
  protected BufferedReader reader;
  protected PrintWriter writer;

  private boolean isConnected = false;

  public RouletteV1ClientImpl() {
    clientSocket = new Socket();
  }

  @Override
  public void connect(String server, int port) throws IOException {
    clientSocket = new Socket(server, port);
    isConnected = true;

    reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    writer = new PrintWriter(clientSocket.getOutputStream());

    String line = reader.readLine(); // Read and consume line Hello first
  }

  @Override
  public void disconnect() throws IOException {
    clientSocket.close();
    reader.close();
    writer.close();

    isConnected = false;
  }

  @Override
  public boolean isConnected() {
    return isConnected;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    writer.println(RouletteV1Protocol.CMD_LOAD);
    writer.println(fullname);
    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();
    reader.readLine(); // Consume "Send data" string
    reader.readLine(); // Consume "Data loaded" string
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    writer.println(RouletteV1Protocol.CMD_LOAD);
    for(Student student : students) {
      writer.println(student.getFullname());
    }
    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();
    reader.readLine(); // Consume "Send Data" string
    reader.readLine(); // Consume "Data loaded" string
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    writer.println(RouletteV1Protocol.CMD_RANDOM);
    writer.flush();
    String json = reader.readLine();
    RandomCommandResponse response = RandomCommandResponse.fromJson(json);

    if(response.getError() != null) {
      throw new EmptyStoreException();
    }

    return new Student(RandomCommandResponse.fromJson(json).getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    return getInfos().getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    return getInfos().getProtocolVersion();
  }

  private InfoCommandResponse getInfos() throws IOException {
    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();
    String json = reader.readLine();

    return InfoCommandResponse.fromJson(json);
  }

}
