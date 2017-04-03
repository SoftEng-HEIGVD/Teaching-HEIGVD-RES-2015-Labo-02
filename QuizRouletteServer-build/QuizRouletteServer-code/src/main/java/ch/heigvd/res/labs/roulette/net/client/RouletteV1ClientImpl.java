package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.*;
import ch.heigvd.res.labs.roulette.data.Student;
<<<<<<< HEAD
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
=======

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
>>>>>>> fb-lab02-v2-contrib
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author François Quellec
 *         Pierre-Samuel Rochat
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

<<<<<<< HEAD
  private Socket clientSocket = null;
  private DataOutputStream outToServer = null;
  private BufferedReader inFromServer = null;
=======
  protected Socket clientSocket = null;
  protected PrintWriter outToServer = null;
  protected BufferedReader inFromServer = null;
>>>>>>> fb-lab02-v2-contrib

  @Override
  public void connect(String server, int port) throws IOException {
    clientSocket = new Socket(server, port);
    outToServer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), Charset.forName("UTF-8"))));
    inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    // Reading the hello response
    inFromServer.readLine();
  }

  @Override
  public void disconnect() throws IOException {
    if(clientSocket != null && clientSocket.isConnected()){
      outToServer.print(RouletteV1Protocol.CMD_BYE + '\n');
      outToServer.flush();
      clientSocket.close();
      outToServer.close();
      inFromServer.close();

      clientSocket = null;
      outToServer = null;
      inFromServer = null;
    }
    else throw new SocketException("Server already disconnected");
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
    outToServer.print(RouletteV1Protocol.CMD_LOAD+ '\n');
    outToServer.flush();
    // Reading the Load response
    inFromServer.readLine();

    for(Student s : students){
      outToServer.print(s.getFullname() + "\n");
    }

    outToServer.print(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER+ '\n');
    outToServer.flush();
    // Reading the Data Loaded response
    inFromServer.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    if(getNumberOfStudents() == 0) throw new EmptyStoreException();
    outToServer.print(RouletteV1Protocol.CMD_RANDOM + '\n');
    outToServer.flush();
    String randomStudent = inFromServer.readLine();
    return new Student(randomStudent);
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    outToServer.print(RouletteV1Protocol.CMD_INFO+ '\n');
    outToServer.flush();
    InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(inFromServer.readLine(), InfoCommandResponse.class);
    return infoResponse.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    outToServer.print(RouletteV1Protocol.CMD_INFO+ '\n');
    outToServer.flush();
    InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(inFromServer.readLine(), InfoCommandResponse.class);
    return infoResponse.getProtocolVersion();
  }
}