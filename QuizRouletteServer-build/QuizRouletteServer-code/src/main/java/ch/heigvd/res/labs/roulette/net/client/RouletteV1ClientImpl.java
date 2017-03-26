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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {
  private Socket socket = null;
  private PrintWriter w = null;
  private BufferedReader r = null;

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  @Override
  public void connect(String server, int port) throws IOException {
    //bind socket to port
    socket = new Socket(server,port);
    //Store the Stream object
    w = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),StandardCharsets.UTF_8));
    r = new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));

    r.readLine();//read the Hello
  }

  @Override
  public void disconnect() throws IOException {
    //terminate connection
    w.println(RouletteV1Protocol.CMD_BYE);
    w.flush();
    //close pipe & socket
    w.close();
    r.close();
    socket.close();
  }

  @Override
  public boolean isConnected() {
    //before using the object check if exist
    return socket!=null && socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {

    w.println(RouletteV1Protocol.CMD_LOAD);w.flush();//Say we want to load student
    r.readLine();//response

    w.println(fullname);//send student name
    w.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);//say we have finished
    w.flush();

    r.readLine();//DATA LOADED response
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    w.println(RouletteV1Protocol.CMD_LOAD);w.flush();//Say we want to load student
    r.readLine();//response on server
    //send student one by one
    for (Student student: students
         ) {
      w.println(student.getFullname());//print all students
    }

    //Say we have finished
    w.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    w.flush();

    r.readLine();//data loaded response
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {

    w.println(RouletteV1Protocol.CMD_RANDOM);w.flush();//ask for RANDOM
    //get the reponse and parse it as JSON
    RandomCommandResponse randomResponse = JsonObjectMapper.parseJson(r.readLine(),RandomCommandResponse.class);

    if(!randomResponse.getError().isEmpty()) throw new EmptyStoreException();//be sur there is no error

    else
      return new Student(randomResponse.getFullname());//return a student object with his name
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    w.println(RouletteV1Protocol.CMD_INFO);w.flush();

    //use the InfoCommandResponse class to parse the number of Students. Because could vary in future version
    return JsonObjectMapper.parseJson(r.readLine(),InfoCommandResponse.class).getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    w.println(RouletteV1Protocol.CMD_INFO);w.flush();
    //use of InfoCommandResponse class to parse the response and not be subject to future implementations
    return JsonObjectMapper.parseJson(r.readLine(),InfoCommandResponse.class).getProtocolVersion();
  }



}
