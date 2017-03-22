package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
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
      socket = new Socket(server, port);
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

      // We get the hello message from the server (we don't use it)
      reader.readLine();
   }

   @Override
   public void disconnect() throws IOException
   {
      writer.println("BYE");
      socket.close();
   }

   @Override
   public boolean isConnected()
   {
      if (socket != null)
         return socket.isBound();
      else
         return false;
   }

   @Override
   public void loadStudent(String fullname) throws IOException
   {
      // Ask the server the current version
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

      reader.readLine();
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException
   {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException
   {
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
      // Ask the server the current version
      writer.println("INFO");
      writer.flush();

      // get the reply and transform it to an "InfoCommandResponse" Object
      InfoCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

      return response.getProtocolVersion();
   }


}
