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

import java.util.LinkedList;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti, Valentin Minder
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  // socket for server connection
  private Socket mysocket = null;
  // input from server
  private BufferedReader br = null;
  // output to server (dont forget to flush before expecting response!)
  protected PrintWriter pw = null;
  // welcome message to avoid...
  private final String welcome = "Hello. Online HELP is available. Will you find it?";
  
  
  protected String myReadLine() throws IOException {
      String line;
      do {
          line = br.readLine();
      } while (line.equalsIgnoreCase(welcome));
      return line;
  }
  
  @Override
  public void connect(String server, int port) throws IOException {
      mysocket = new Socket(server, port);
      pw = new PrintWriter(new OutputStreamWriter(mysocket.getOutputStream()));
      br = new BufferedReader(new InputStreamReader(mysocket.getInputStream()));
      //br.readLine(); // welcome message!
  }

  @Override
  public void disconnect() throws IOException {
      pw.write("BYE\n");
      pw.flush();
      pw.close();
      br.close();
      mysocket.close();
  }

  @Override
  public boolean isConnected() {
      if (mysocket == null) {
         return false; 
      } else {
          return mysocket.isConnected() && !mysocket.isClosed();
      } // git trick for braces
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      List<Student> list = new LinkedList<Student>();
      list.add(new Student(fullname));
      loadStudents(list);
  }

  private void endLoad () throws IOException {
      myReadLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      pw.write("LOAD\n");
      pw.flush();

      String waitStart = "Send your data [end with ENDOFDATA]";
      if (!myReadLine().equalsIgnoreCase(waitStart)) {
          throw new IOException("server response not correct....");
      } // git trick for braces

      for (Student student : students) {
          pw.write(student.getFullname()+"\n");
      } // git trick for braces

      pw.write("ENDOFDATA\n");
      pw.flush();

      endLoad();
       // V1: DATA LOADED or V2: {"status":"success","numberOfNewStudents":3}
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      pw.write("RANDOM\n");
      pw.flush();

      RandomCommandResponse rcr = JsonObjectMapper.parseJson(myReadLine(), RandomCommandResponse.class);
      if(rcr.getError() != null) {
          throw new EmptyStoreException();
      } // git trick for braces
      return new Student(rcr.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
      pw.write("INFO\n");
      pw.flush();

      InfoCommandResponse icr = JsonObjectMapper.parseJson(myReadLine(), InfoCommandResponse.class);
      return icr.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
      pw.write("INFO\n");
      pw.flush();

      InfoCommandResponse icr = JsonObjectMapper.parseJson(myReadLine(), InfoCommandResponse.class);
      return icr.getProtocolVersion();
  }



}
