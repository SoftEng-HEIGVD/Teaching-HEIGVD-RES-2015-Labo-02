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
 */
public class RouletteV1ClientImpl implements IRouletteV1Client
{

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  // Socket for connection on the server
  private Socket communicationSocket = null;
  // Input from server
  private BufferedReader buffReader = null;
  private PrintWriter printWriter = null;



  @Override
  public void connect(String server, int port) throws IOException {
      // Openning a connection with the server
      communicationSocket = new Socket(server, port);
      // Openning an input and an ouput stream
      printWriter = new PrintWriter(new OutputStreamWriter(communicationSocket.getOutputStream()));
      buffReader = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));

      // Reading the welcomme message on the serveur
	  buffReader.readLine();
  }

  @Override
  public void disconnect() throws IOException {
    // Sending the message BYE to signify end of connection
    printWriter.println(RouletteV1Protocol.CMD_BYE);
    printWriter.flush();

    // Closing the input and output stream
	printWriter.close();
    buffReader.close();
    // Closing the connection with server
    communicationSocket.close();
  }

  @Override
  public boolean isConnected()
  {
      if (communicationSocket != null)
      {
        return communicationSocket.isConnected() && !communicationSocket.isClosed();
      }
      else
      {
        return false;
      }
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    // Constructing a list of only one student
    ArrayList<Student> studentsList = new ArrayList<Student>();
    studentsList.add(new Student(fullname));
    loadStudents(studentsList);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException
  {
	  
          // Sends the signal for the beginning of the loading process
	  printWriter.println(RouletteV1Protocol.CMD_LOAD);
	  printWriter.flush();

	  // Tests if the server is ready and waiting for the data
	  if(!buffReader.readLine().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START))
		  throw new IOException("Error during the loading !");

	  // Sends the students name by name
	  for(Student currentStudent : students)
	  {
		  // Do not sends empty strings
		  if (currentStudent.getFullname().length() >= 1)
		  {
			  // Sends full name
			  printWriter.println(currentStudent.getFullname());
		  }
	  }

	  // Signifies the end of the transfert with ENDOFDATA
	  printWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
	  printWriter.flush();
          
	  // Waits for commfirmation of the server
          // TODO : think of what to do if server do not send DATA LOADED...
	  buffReader.readLine();
          
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException
  {
  	  // Asking for random student
	  printWriter.println(RouletteV1Protocol.CMD_RANDOM);
	  printWriter.flush();

	  // Reading the answer given by he server
	  RandomCommandResponse response = JsonObjectMapper.parseJson(buffReader.readLine(), RandomCommandResponse.class);

	  // Testing the validity of the answer
	   if(response.getError() != null)
	  {
		  throw new EmptyStoreException();
	  }
	  else
	  {
		  return new Student(response.getFullname());
	  }

  }

  @Override
  public int getNumberOfStudents() throws IOException
  {
	  // Asking for the info stored on the server
	  printWriter.println(RouletteV1Protocol.CMD_INFO);
	  printWriter.flush();
          
          // Parsing the answer for the number of students
	  InfoCommandResponse response = JsonObjectMapper.parseJson(buffReader.readLine(),
                                                                    InfoCommandResponse.class);
	  return response.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException
  {
	  // Asking for the info stored on the server
	  printWriter.println(RouletteV1Protocol.CMD_INFO);
	  printWriter.flush();
          
          // Parsing the answer for the version number of the protocol
	  InfoCommandResponse info = JsonObjectMapper.parseJson(buffReader.readLine(),
                                                                InfoCommandResponse.class);
	  return info.getProtocolVersion();
  }
}
