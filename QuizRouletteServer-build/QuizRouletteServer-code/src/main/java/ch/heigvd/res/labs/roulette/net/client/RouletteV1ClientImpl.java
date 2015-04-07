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
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  // The client's socket
  private Socket clientSocket = null;
  
  // The writer and reader from the socket
  private OutputStreamWriter out;
  private InputStreamReader in;
  
  @Override
  public void connect(String server, int port) throws IOException {
      // Connect to the port
      clientSocket = new Socket(server, port);
      
      // Prepare the writer and reader
      out = new OutputStreamWriter(clientSocket.getOutputStream());
      in = new InputStreamReader(clientSocket.getInputStream());
  }

  @Override
  public void disconnect() throws IOException {
      // Close the socket
      clientSocket.close();
  }

  @Override
  public boolean isConnected() {
      // The server is connected if the socket is not closed
      return !clientSocket.isClosed();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      // Send load command
      out.write("LOAD");
      
      // Write name
      out.write(fullname);
      
      // Send end of data marker
      out.write("ENDOFDATA");
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      // Send load command
      out.write("LOAD");
      
      // Write all students
      for (Student s : students) {
          out.write(s.getFullname());
      }
      
      // Send end of data marker
      out.write("ENDOFDATA");
      
      // Flush stream
      out.flush();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      // Send RANDOM comamand
      out.write("RANDOM");
      
      // Wait for response
      while(!in.ready());
      
      // Read
      char[] buffer = new char[100];
      in.read(buffer, 0, 100);
      
      return new Student(new String(buffer));
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getProtocolVersion() throws IOException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
