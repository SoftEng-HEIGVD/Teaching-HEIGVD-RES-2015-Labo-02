package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
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

  private Socket clientSocket = null;
  private DataOutputStream outToServer = null;
  private BufferedReader inFromServer = null;

  @Override
  public void connect(String server, int port) throws IOException {
    clientSocket = new Socket(server, port);
    outToServer = new DataOutputStream(clientSocket.getOutputStream());
    inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    // Reading the hello response
    inFromServer.readLine();
  }

  @Override
  public void disconnect() throws IOException {
    outToServer.close();
    inFromServer.close();
    clientSocket.close();
  }

  @Override
  public boolean isConnected() {
    if(clientSocket == null)
      return false;
    else return clientSocket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    LinkedList e = new LinkedList<Student>();
    e.add(new Student(fullname));
    loadStudents(e);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    outToServer.writeBytes(RouletteV1Protocol.CMD_LOAD+ '\n');
    // Reading the Load response
    inFromServer.readLine();

    for(Student s : students){
      outToServer.writeBytes(s.getFullname() + "\n");
    }

    outToServer.writeBytes(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER+ '\n');
    outToServer.flush();
    // Reading the Data Loaded response
    inFromServer.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    if(getNumberOfStudents() == 0) throw new EmptyStoreException();
    outToServer.writeBytes(RouletteV1Protocol.CMD_RANDOM + '\n');
    outToServer.flush();
    String randomStudent = inFromServer.readLine();
    return new Student(randomStudent);
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    outToServer.writeBytes(RouletteV1Protocol.CMD_INFO+ '\n');
    outToServer.flush();
    InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(inFromServer.readLine(), InfoCommandResponse.class);
    return infoResponse.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    outToServer.writeBytes(RouletteV1Protocol.CMD_INFO+ '\n');
    outToServer.flush();
    InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(inFromServer.readLine(), InfoCommandResponse.class);
    return infoResponse.getProtocolVersion();
  }



}
