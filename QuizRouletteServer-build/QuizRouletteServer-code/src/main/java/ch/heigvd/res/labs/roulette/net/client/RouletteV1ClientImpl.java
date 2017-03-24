package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 * @author Matthieu Chatelan
 * @author Lara Chauffoureaux
 */
public class RouletteV1ClientImpl implements IRouletteV1Client
{
   private Socket socket = null;
   private BufferedReader reader;
   private PrintWriter writer;

   private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

   @Override
   public void connect(String server, int port) throws IOException
   {
      LOG.log(Level.FINE, "Client is connecting to the server");

      // Create the soclet
      socket = new Socket(server, port);

      // Create the reader and the writer to read from the socket and to write to it
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

      // We get the hello message from the server (we don't use it)
      reader.readLine();
   }

   @Override
   public void disconnect() throws IOException
   {
      LOG.log(Level.FINE, "Client is requesting a disconnect, disconnecting...");

      // Terminate the connection with the server
      writer.println("BYE");

      // Close the socket on the client side
      socket.close();
   }

   @Override
   public boolean isConnected()
   {
      LOG.log(Level.FINE, "Checking whether the clint is connected to the server or not");

      if (socket != null)
         return socket.isBound();
      else
         return false;
   }

   @Override
   public void loadStudent(String fullname) throws IOException
   {
      LOG.log(Level.FINE, "Client is loading a student into the store");

      // Ask the server to load a student into the store
      writer.println("LOAD");
      writer.flush();

      // Trash the reply from the server (we know how to talk to the server)
      reader.readLine();

      // Load the student given in parameters
      writer.println(fullname);
      writer.flush();

      // Sent the ENDOFDATA
      writer.println("ENDOFDATA");
      writer.flush();

      // Trash the server's reply (useless for us)
      reader.readLine();
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException
   {
      LOG.log(Level.FINE, "Client is loading multiple students into the store");

      // Ask the server to load students into the store
      writer.println("LOAD");
      writer.flush();

      // Trash the reply from the server (we know how to talk to the server)
      reader.readLine();

      // Load students from the List
      for (Student student : students)
         writer.println(student.getFullname());

      // Write them to the server
      writer.flush();

      // Sent the ENDOFDATA
      writer.println("ENDOFDATA");
      writer.flush();

      reader.readLine();
   }

   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException
   {
      LOG.log(Level.FINE, "Picking a random student from the store");

      // Ask the server to provice a random student
      writer.println("RANDOM");
      writer.flush();

      // We collect the reply
      RandomCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);

      // If there is no error, return a new Student with the name received
      if (response.getError().equals(""))
         return new Student(response.getFullname());
      // Otherwise if there is an error, throw an exception
      else
         throw new EmptyStoreException();
   }

   @Override
   public int getNumberOfStudents() throws IOException
   {
      LOG.log(Level.FINE, "Getting the number of students in the store");

      // Ask the server the current version
      writer.println("INFO");
      writer.flush();

      // get the reply and transform it to an "InfoCommandResponse" Object
      InfoCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

      return response.getNumberOfStudents();
   }

   @Override
   public String getProtocolVersion() throws IOException
   {
      LOG.log(Level.FINE, "Getting the protocol version...");

      // Ask the server the current version
      writer.println("INFO");
      writer.flush();

      // get the reply and transform it to an "InfoCommandResponse" Object
      InfoCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

      return response.getProtocolVersion();
   }


}
