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
 * @author Mélanie Huck
 * @author James Nolan
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  // The client's socket
  private Socket clientSocket = null;
  
  // The writer and reader from the socket (protected to be used by subversions)
  protected PrintWriter out;
  protected BufferedReader in;
  
  @Override
  public void connect(String server, int port) throws IOException {
      // If connected, do nothing
      if (isConnected()) {
          return;
      }
      
      // Else, connect to the port
      clientSocket = new Socket(server, port);
      
      // Prepare the writer and reader
      out = new PrintWriter(clientSocket.getOutputStream());
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      
      LOG.log(Level.INFO, "Client connection OK with greetings: " + in.readLine());
  }

  @Override
  public void disconnect() throws IOException {
      // If already disconnected, do nothing
      if (!isConnected()) {
          return;
      }
      
      // Bye
      out.println(RouletteV1Protocol.CMD_BYE);
      out.flush();
      
      // Close the read/writers
      out.close();
      in.close();
      
      // Close the socket
      clientSocket.close();
  }

  @Override
  public boolean isConnected() {
      // The server is connected if the socket is not closed
      return clientSocket != null && !clientSocket.isClosed();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      // If not connected, throw exception
      if (!isConnected()) {
          throw new IOException("Must be connected to load student!");
      }
      
      // Send load command
      out.println(RouletteV1Protocol.CMD_LOAD);
      out.flush();
      
      // Skip server info messages
      in.readLine();
      
      // Write name
      out.println(fullname);
      
      // Send end of data marker
      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();
      
      // Skip server info messages
      in.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      // If not connected, throw exception
      if (!isConnected()) {
          throw new IOException("Must be connected to load students!");
      }
      
      // Send load command
      out.println(RouletteV1Protocol.CMD_LOAD);
      out.flush();
      
      // Skip server info messages
      in.readLine();
      
      // Write all students
      for (Student s : students) {
          out.println(s.getFullname());
          LOG.log(Level.INFO, "SENT student with name " + s.getFullname());
      }
      
      // Send end of data marker
      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();
      
      // Skip server info messages
      in.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      // If not connected, throw exception
      if (!isConnected()) {
          throw new IOException("Must be connected to load students!");
      }
      
      // Send RANDOM command
      out.println(RouletteV1Protocol.CMD_RANDOM);
      out.flush();

      // Read line and parse json
      String response = in.readLine();
      RandomCommandResponse result = JsonObjectMapper.parseJson(response, RandomCommandResponse.class);
      
      // Maybe there was an error...
      if (result.getError() != null) {
          // Maybe there's no student?
          if (result.getError().equals("There is no student, you cannot pick a random one")) {
            throw new EmptyStoreException();
          }
          
          // Maybe the error is something else
          throw new IOException(result.getError());
      }
      return new Student(result.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
      // If not connected, throw exception
      if (!isConnected()) {
          throw new IOException("Must be connected to retrieve number of students!");
      }
      
      
      // Request INFOs from the server
      out.println(RouletteV1Protocol.CMD_INFO);
      out.flush();
      
      // read line and Parse JSON
      String response = in.readLine();
      InfoCommandResponse infos = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);
      
      // Return number of students
      LOG.log(Level.INFO, "Number of students is " + infos.getNumberOfStudents());
      return infos.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    return RouletteV1Protocol.VERSION;
  }
}
