package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
  private Socket clientSocket = null;
  protected BufferedReader in = null;
  protected PrintWriter out = null;
  private boolean connected = false;

  @Override
  public void connect(String server, int port) throws IOException {
    
    clientSocket = new Socket(server, port);
    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    out = new PrintWriter(clientSocket.getOutputStream());
    connected = true;
    in.readLine();
  }

  @Override
  public void disconnect() throws IOException {
    out.println("bye");
    out.flush();
    
    in.close();
    out.close();
    clientSocket.close();
    connected = false;
  }

  @Override
  public boolean isConnected() {
    return connected;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    out.println("load");
    out.flush();
    out.println(fullname);
    out.flush();
    out.println("endofdata");
    out.flush();
    
    in.readLine();
    in.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    if(!students.isEmpty()){
        out.println("load"); 
        out.flush();
        for(Student s : students){
            out.println(s.getFullname());
            out.flush();
        }

        out.println("endofdata");
        out.flush();
        in.readLine();
        in.readLine();
    }

  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      String answer;
      RandomCommandResponse rcr;
      
      if(getNumberOfStudents() == 0){
          throw new EmptyStoreException();
      }
      
      out.println("random");
      out.flush();
      
      answer = in.readLine();
     
      rcr = JsonObjectMapper.parseJson(answer, RandomCommandResponse.class);
      return new Student(rcr.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    String answer;
    InfoCommandResponse icr;
    out.println("info");
    out.flush();
    answer = in.readLine();

    icr = JsonObjectMapper.parseJson(answer, InfoCommandResponse.class);
    return icr.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    String answer;
    InfoCommandResponse icr;
    out.println("info");
    out.flush();
    
    answer = in.readLine();

    icr = JsonObjectMapper.parseJson(answer, InfoCommandResponse.class);
    return icr.getProtocolVersion();
  }

}
