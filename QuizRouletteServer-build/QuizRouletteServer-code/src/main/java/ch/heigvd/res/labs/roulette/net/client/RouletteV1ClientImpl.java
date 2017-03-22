package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    // BRUGE GUILLAUME (git: guiguismall)
    // VILLA DAVID     (git: yoaaaarp)
  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  // socket on which the client writes and reads
  protected Socket clientSocket = null;
  protected BufferedReader in;
  protected PrintWriter out;
  protected boolean isConnected = false;
  protected String defaultServer = "localhost";


  
  @Override
  public void connect(String server, int port) throws IOException {
      // in this case, we only want to connect to localhost
      if(server != defaultServer)
          throw new IOException();
      // connects to the socket
      clientSocket = new Socket(server, port);
      // opens and wraps iostreams
      out = new PrintWriter(clientSocket.getOutputStream(),true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      // flushes the line sent by the server
      in.readLine();
      isConnected = true;
  }

  @Override
  public void disconnect() throws IOException {
      out.write("BYE\n");
      out.flush();
      clientSocket.close();
      isConnected = false;
  }

  @Override
  public boolean isConnected() {
      return isConnected;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      // handles empty name
      if(fullname.isEmpty())
          return;
      
      // writes commands
      out.write("LOAD\n");
      out.write(fullname + "\n");
      out.write("ENDOFDATA\n");
      out.flush();
      
      // flushes the 2 lines sent by the server
      in.readLine();
      in.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      // writes commands
      out.write("LOAD\n");
      for(Student s : students) {
          out.write((s.getFullname() + "\n"));
      }
      out.write("ENDOFDATA\n");
      out.flush();
      
      // flushes the 2 lines sent by the server
      in.readLine();
      in.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      // handles no student in database
      if(getNumberOfStudents() == 0)
          throw new EmptyStoreException();
      
      // writes commands
      out.write("RANDOM\n");
      out.flush();
      
      // reads and extracts student name
      String name = in.readLine();
      return new Student(JsonObjectMapper.parseJson(name, RandomCommandResponse.class).getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException { 
      // writes commands
      out.write("INFO\n");
      out.flush();
      
      // reads and extracts number of students 
      String info = in.readLine();
      return JsonObjectMapper.parseJson(info, InfoCommandResponse.class).getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    return RouletteV1Protocol.VERSION;
  }
}
