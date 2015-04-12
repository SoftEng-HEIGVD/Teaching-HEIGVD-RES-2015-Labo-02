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
  private BufferedReader buffReader = null;
  // output to server (dont forget to flush before expecting response!)
  protected PrintWriter printWriter = null;
  // welcome message to avoid...
  // these are hard-coded server-side (not in protocol) that's why there are here also.
  private final String welcome = "Hello. Online HELP is available. Will you find it?";
  private final String notUnderstood = "Huh? please use HELP if you don't know what commands are available.";


  /**
   * Customized readLine to read the next "interesting" line:
   * avoid welcome message and "not understood" messages. 
   * @return
   * @throws IOException 
   */
  protected String myReadLine() throws IOException {
      String line;
      do {
          line = buffReader.readLine();
      } while (line.equalsIgnoreCase(welcome) || line.equalsIgnoreCase(notUnderstood));
      return line;
  }
  
  @Override
  public void connect(String server, int port) throws IOException {
      mysocket = new Socket(server, port);
      printWriter = new PrintWriter(new OutputStreamWriter(mysocket.getOutputStream()));
      buffReader = new BufferedReader(new InputStreamReader(mysocket.getInputStream()));
      //br.readLine(); // welcome message!
  }

  @Override
  public void disconnect() throws IOException {
      printWriter.println(RouletteV1Protocol.CMD_BYE);
      printWriter.flush();
      printWriter.close();
      buffReader.close();
      mysocket.close();
  }

  @Override
  public boolean isConnected() {
      if (mysocket == null) {
         return false; 
      } else {
          // return true if has been connected and not closed so far.
          return mysocket.isConnected() && !mysocket.isClosed();
      } // git trick for braces
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      List<Student> list = new LinkedList<Student>();
      list.add(new Student(fullname));
      loadStudents(list);
  }

  /**
   * Customized endLoad to handle V1/V2 dynamically.
   * V1 reads the line and return. 
   * V2 has a status code and number of added students printed in the logs.
   * 
   * // V1: DATA LOADED or V2: {"status":"success","numberOfNewStudents":3}
   * @throws IOException 
   */
  private void endLoad () throws IOException {
      myReadLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      printWriter.println(RouletteV1Protocol.CMD_LOAD);
      printWriter.flush();

      if (!myReadLine().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START)) {
          throw new IOException("server response not correct....");
      } // git trick for braces

      for (Student student : students) {
          printWriter.println(student.getFullname());
      } // git trick for braces

      printWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      printWriter.flush();

      endLoad();
       // V1: DATA LOADED or V2: {"status":"success","numberOfNewStudents":3}
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      printWriter.println(RouletteV1Protocol.CMD_RANDOM);
      printWriter.flush();

      RandomCommandResponse rcr = JsonObjectMapper.parseJson(myReadLine(), RandomCommandResponse.class);
      if(rcr.getError() != null) {
          throw new EmptyStoreException();
      } // git trick for braces
      return new Student(rcr.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
      printWriter.println(RouletteV1Protocol.CMD_INFO);
      printWriter.flush();

      InfoCommandResponse icr = JsonObjectMapper.parseJson(myReadLine(), InfoCommandResponse.class);
      return icr.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
      printWriter.println(RouletteV1Protocol.CMD_INFO);
      printWriter.flush();

      InfoCommandResponse icr = JsonObjectMapper.parseJson(myReadLine(), InfoCommandResponse.class);
      return icr.getProtocolVersion();
  }



}
