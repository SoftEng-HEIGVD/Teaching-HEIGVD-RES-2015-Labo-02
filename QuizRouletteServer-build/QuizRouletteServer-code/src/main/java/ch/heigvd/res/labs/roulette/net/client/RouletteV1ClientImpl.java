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
 * @author Xavier Vaz Afonso, Gaëtan Othenin-Girard
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  //Declaration of variables
  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private Socket clientSocket = null;
  private PrintWriter pWriter = null;
  private BufferedReader bReader = null;

  @Override
  public void connect(String server, int port) throws IOException {

    //Create the client socket
    clientSocket = new Socket(server, port);

    //To read and write with the server
    pWriter = new PrintWriter(clientSocket.getOutputStream());
    bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  }

  @Override
  public void disconnect() throws IOException {

    //Send 'bye' to close the connection
    pWriter.write(RouletteV1Protocol.CMD_BYE + "\n");

    //close the socket
    clientSocket.close();

    clientSocket=null;
    pWriter = null;
    bReader = null;
  }

  @Override
  public boolean isConnected() {
    return clientSocket != null && clientSocket.isConnected() ;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {

    //Load a student
    pWriter.write(RouletteV1Protocol.CMD_LOAD + "\n");
    pWriter.write(fullname + "\n");
    pWriter.write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
    pWriter.flush();

    //Read the server response
    bReader.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {

    //load all the students
    for(Student student : students) {
      loadStudent(student.getFullname());
    }

    //Read the server response
    bReader.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {

    //Read the server response
    bReader.readLine();

    //Pick a random student
    pWriter.write(RouletteV1Protocol.CMD_RANDOM + "\n");
    pWriter.flush();

    String info = bReader.readLine();
    RandomCommandResponse response = JsonObjectMapper.parseJson(info, RandomCommandResponse.class);

    //if the error message is not empty there is no student
    if(!(response.getError().isEmpty()))
    {
      throw new  EmptyStoreException();
    }

    Student student = new Student(response.getFullname());
    return student;
  }

  @Override
  public int getNumberOfStudents() throws IOException {

    return getCommandResponse().getNumberOfStudents();

  }

  @Override
  public String getProtocolVersion() throws IOException {

    return getCommandResponse().getProtocolVersion();
  }

  public InfoCommandResponse getCommandResponse () throws IOException {

    //Send the command info
    bReader.readLine();
    pWriter.write(RouletteV1Protocol.CMD_INFO + "\n");
    pWriter.flush();

    String info = bReader.readLine();
    InfoCommandResponse response = JsonObjectMapper.parseJson(info, InfoCommandResponse.class);

    return response;
  }
}
