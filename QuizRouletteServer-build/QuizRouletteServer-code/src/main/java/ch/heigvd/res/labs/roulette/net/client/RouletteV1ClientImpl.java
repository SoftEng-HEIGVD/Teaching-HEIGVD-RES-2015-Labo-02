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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 * @author Tony Clavien
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private Socket socket = null;
  private BufferedReader reader = null;
  private PrintWriter writer = null;

  @Override
  public void connect(String server, int port) throws IOException {
	  // connect and initiate reader and writer
    socket = new Socket(server, port);
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
    
    // Flush server
    reader.readLine();
  }

  @Override
  public void disconnect() throws IOException {
	  // Send the message to the server
	  writer.println(RouletteV1Protocol.CMD_BYE);
	  // flush the buffer
	  writer.flush();
	  
	  // Close the local stream
	  reader.close();
	  writer.close();
	  socket.close();
  }

  @Override
  public boolean isConnected() {
	  
	  if(socket == null)
	  {
		  return false;
	  }
	  // need socket to be initialized
    return socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
	  List<Student> ls = new ArrayList<>();
	  ls.add(new Student(fullname));
	  loadStudents(ls);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
	  // Send the message to the server
	  writer.println(RouletteV1Protocol.CMD_LOAD);
	  // flush the buffers
	  writer.flush();
	  
	  // Read the message Send your data ...
	  reader.readLine();
	  
	  // send data
	  for(Student s : students)
	  {
		  writer.println(s.getFullname());
		  writer.flush();
	  }
	  
	  // close the load
	  writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
	  // flush the streams
	  writer.flush();
	  
	  // read the end load message
	  reader.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
	// Send the message to the server
		  writer.println(RouletteV1Protocol.CMD_RANDOM);
		  // flush the buffers
		  writer.flush();
		 
		 RandomCommandResponse answer = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);
		 
		 if(answer.getFullname() == null || answer.getFullname().isEmpty())
		 {
			 throw new EmptyStoreException();
		 }
		 return new Student(answer.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
	// Send the message to the server
	  System.out.println("Send info message");
	  writer.println(RouletteV1Protocol.CMD_INFO);
	  // flush the buffers
	  System.out.println("writer flushed");
	  writer.flush();
	 
	 InfoCommandResponse nbr = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
	 System.out.println("Received : " + nbr.toString());
	 return nbr.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
		// Send the message to the server
	  writer.println(RouletteV1Protocol.CMD_INFO);
	  // flush the buffers
	  writer.flush();
	 
	 InfoCommandResponse nbr = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
	 return nbr.getProtocolVersion();
  }
  


}
