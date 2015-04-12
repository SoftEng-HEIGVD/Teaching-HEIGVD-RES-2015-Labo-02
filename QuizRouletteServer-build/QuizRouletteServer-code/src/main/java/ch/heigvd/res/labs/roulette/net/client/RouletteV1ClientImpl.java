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
  private BufferedReader in = null;
  private PrintWriter out = null;
  private boolean connected = false;

  @Override
  public void connect(String server, int port) throws IOException {
    clientSocket = new Socket(server, port);
    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    out = new PrintWriter(clientSocket.getOutputStream());
    connected = true;
  }

  @Override
  public void disconnect() throws IOException {
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
    out.println(fullname);
    out.println("endofdata");
    out.flush();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    if(!students.isEmpty()){
        out.println("load"); 
        for(Student s : students){
            out.println(s.getFullname());
        }

        out.println("endofdata");
        out.flush();
    }
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      String answer;
      RandomCommandResponse rcr;
      
      if(getNumberOfStudents() == 0){
          throw new EmptyStoreException();
      }
      
      out.print("random");
      out.flush();
      do{
        answer = in.readLine();
    }while(!answer.contains("{"));
     
      rcr = JsonObjectMapper.parseJson(answer, RandomCommandResponse.class);
      return new Student(rcr.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    String answer;
    InfoCommandResponse icr;
    out.print("info");
    out.flush();
    do{
        answer = in.readLine();
    }while(!answer.contains("{"));

    icr = JsonObjectMapper.parseJson(answer, InfoCommandResponse.class);
    return icr.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    String answer;
    InfoCommandResponse icr;
    out.print("info");
    out.flush();
    
    do{
        answer = in.readLine();
    }while(!answer.contains("{"));
    
    icr = JsonObjectMapper.parseJson(answer, InfoCommandResponse.class);
    return icr.getProtocolVersion();
  }

}
