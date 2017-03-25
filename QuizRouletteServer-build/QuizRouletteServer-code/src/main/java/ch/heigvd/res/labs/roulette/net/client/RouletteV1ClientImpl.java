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
import java.util.LinkedList;
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
   private Socket serverSocket;

   BufferedReader bufferedReader;
   PrintWriter printWriter;

   /**
    * Connects to a server with the server and the port specified
    *
    * @param server we want to connect to
    * @param port we want to use
    * @throws IOException
    */
   @Override
   public void connect(String server, int port) throws IOException {
      if (!isConnected()) {
         serverSocket = new Socket(server, port);

         bufferedReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), "UTF-8"));
         printWriter = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream(), "UTF-8"));

         LOG.log(Level.INFO, bufferedReader.readLine());

      }
   }

   /**
    * Disconnects from the server if connected
    *
    * @throws IOException
    */
   @Override
   public void disconnect() throws IOException {
      if (isConnected()) {
         printWriter.println(RouletteV1Protocol.CMD_BYE);
         printWriter.flush();

         bufferedReader.close();
         printWriter.close();
         serverSocket.close();

         bufferedReader = null;
         printWriter = null;
         serverSocket = null;
      }
   }

   /**
    * checks if the server is initialised and connected
    *
    * @return
    */
   @Override
   public boolean isConnected() {
      return serverSocket != null && serverSocket.isConnected();
   }

   /**
    * send a student to the server
    *
    * @param fullname name of the student
    * @throws IOException
    */
   @Override
   public void loadStudent(String fullname) throws IOException {
      List<Student> students = new LinkedList<>();
      students.add(new Student(fullname));
      loadStudents(students);
   }

   /**
    * load a list of students to the server
    * @param students list of students
    * @throws IOException
    */
   @Override
   public void loadStudents(List<Student> students) throws IOException {
      printWriter.println(RouletteV1Protocol.CMD_LOAD);
      printWriter.flush();

      LOG.log(Level.INFO, bufferedReader.readLine());

      for (Student student : students) {
         printWriter.println(student.getFullname());
         printWriter.flush();
      }

      printWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      printWriter.flush();

      LOG.log(Level.INFO, bufferedReader.readLine());
   }

   /**
    * asks the server for a random student
    * @return a random student
    * @throws EmptyStoreException
    * @throws IOException 
    */
   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {

      printWriter.println(RouletteV1Protocol.CMD_RANDOM);
      printWriter.flush();

      String result = bufferedReader.readLine();
      RandomCommandResponse answer = JsonObjectMapper.parseJson(result, RandomCommandResponse.class);

      if (!answer.getError().equals("")) {
         throw new EmptyStoreException();
      }

      return Student.fromJson(result);
   }

   /**
    * get the number of students currently in the server
    * @return the number of students
    * @throws IOException 
    */
   @Override
   public int getNumberOfStudents() throws IOException {
      return getInfo().getNumberOfStudents();
   }

   /**
    * get the protocol version from the server
    * @return the version of the server
    * @throws IOException 
    */
   @Override
   public String getProtocolVersion() throws IOException {
      return getInfo().getProtocolVersion();
   }

   /**
    * gets the InfoCOmmandResponse by asking infos to the server
    * @return InfoCommandResponse
    * @throws IOException 
    */
   private InfoCommandResponse getInfo() throws IOException {
      printWriter.println(RouletteV1Protocol.CMD_INFO);
      printWriter.flush();
      String result = bufferedReader.readLine();
      return JsonObjectMapper.parseJson(result,
              InfoCommandResponse.class);
   }

}
