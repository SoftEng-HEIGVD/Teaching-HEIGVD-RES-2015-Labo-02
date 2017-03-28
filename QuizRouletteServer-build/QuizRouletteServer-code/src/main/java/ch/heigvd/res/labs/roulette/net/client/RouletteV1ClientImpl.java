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
 * @author Julien Brêchet
 * @author Adrien Marco
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  // We create a Client Socket and its input and output to communicate with the server
  private Socket clientSocket = null;
  private BufferedReader br = null;
  private PrintWriter pw = null;

  @Override
  public void connect(String server, int port) throws IOException {
    // We etablishes the connection with the server and
    // we create the input and output streams
    clientSocket = new Socket(server, port);
    br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    pw = new PrintWriter(clientSocket.getOutputStream());
    br.readLine(); // don't forget to read the welcome message from the server
  }

  @Override
  public void disconnect() throws IOException {
    // First we need to say "BYE" to close the connection with the server
    pw.println(RouletteV1Protocol.CMD_BYE);
    pw.flush();

    // And then we close the streams and the socket
    br.close();
    pw.close();
    clientSocket.close();
    System.out.println("Disconnected !");
  }

  @Override
  public boolean isConnected() {
    // If the client socket exists, we need to check if it's connected and not closed
    if(clientSocket != null)
      return clientSocket.isConnected() && !clientSocket.isClosed();

    return false;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    // We create a List<Student> storing only one student and we call the loadStudents function
    List<Student> listOf1Student = new LinkedList<>();
    listOf1Student.add(new Student(fullname));
    loadStudents(listOf1Student);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    // First, we say "LOAD" to the server to tell it to start reading
    // client data line by line until it gets a line with the ENDOFDATA string
    pw.println(RouletteV1Protocol.CMD_LOAD);
    pw.flush();

    // If the server don't respond correctly, we have a loading failure
    if(!br.readLine().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START))
      throw new IOException("Loading Failure !");

    for(Student currentStudent : students)
      if(currentStudent.getFullname().length() > 0) // we check that there is really a name (one letter or more)
        pw.println(currentStudent.getFullname()); // and then we transmit it to the server

    // When all students are tranmitted, we tell the server we're finished with ENDOFDATA
    pw.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    pw.flush();

    br.readLine(); // don't forget to read the validation message from the server (DATA LOADED)
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    // We say "RANDOM" to the server to tell it we want a random student
    pw.println(RouletteV1Protocol.CMD_RANDOM);
    pw.flush();

    // Converts the json string into a POJO (plain old Java object)
    RandomCommandResponse response = JsonObjectMapper.parseJson(br.readLine(), RandomCommandResponse.class);
    if(response.getError() == null)
      return new Student(response.getFullname());
    else
      throw new EmptyStoreException();  // exception thrown when an operation is invoked and expects data to be
                                        // available in the data store, but when no data is available
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    // We say "INFO" to the server to say we want it's information message
    pw.println(RouletteV1Protocol.CMD_INFO);
    pw.flush();

    // Converts the json string into a POJO (plain old Java object)
    InfoCommandResponse response = JsonObjectMapper.parseJson(br.readLine(), InfoCommandResponse.class);
    return response.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    // We say "INFO" to the server to say we want it's information message
    pw.println(RouletteV1Protocol.CMD_INFO);
    pw.flush();

    // Converts the json string into a POJO (plain old Java object)
    InfoCommandResponse info = JsonObjectMapper.parseJson(br.readLine(), InfoCommandResponse.class);
    return info.getProtocolVersion();
  }
}