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
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {
    
  // Server connection's socket
  private Socket socket;
  
  // Writer and reader to exchange message with the sever
  private PrintWriter writer;
  private BufferedReader reader;
    
  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  
  // For Version 2
  protected String readLine() throws IOException 
  {   
    return reader.readLine();
  }
  
  
  @Override
  public void connect(String server, int port) throws IOException 
  {
    socket = new Socket(server, port);
    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
    // Read welcome message
    readLine();
  }

  @Override
  public void disconnect() throws IOException {
    
    writer.println(RouletteV1Protocol.CMD_BYE);
    writer.flush();
    
    writer.close();
    reader.close();
    socket.close();
  }

  @Override
  public boolean isConnected() 
  {
    return socket != null && socket.isConnected() && ! socket.isClosed();
  }

  @Override
  public void loadStudent(String fullname) throws IOException 
  {
    List<Student> list = new LinkedList<Student>();
    list.add(new Student(fullname));
    loadStudents(list);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException 
  {
    
    writer.println(RouletteV1Protocol.CMD_LOAD);
    writer.flush();
    
    // Check if the server is ready.
    if (!readLine().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START)) 
    {
      throw new IOException("Incorrect server response");
    }

    for (Student student : students) 
    {
      writer.println(student.getFullname());
    }
    
    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();
    
    // Read response.
    readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException 
  {
    writer.println(RouletteV1Protocol.CMD_RANDOM);
    writer.flush();

    RandomCommandResponse pickedStudent = JsonObjectMapper.parseJson(readLine(), RandomCommandResponse.class);
    
    // Throw an exception if there is an error (the store is empty).
    if(pickedStudent.getError() != null) 
    {
      throw new EmptyStoreException();
    }
    
    return new Student(pickedStudent.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException 
  {
    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();
    
    return JsonObjectMapper.parseJson(readLine(), InfoCommandResponse.class).getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();
    
    return JsonObjectMapper.parseJson(readLine(), InfoCommandResponse.class).getProtocolVersion();
  }
}
