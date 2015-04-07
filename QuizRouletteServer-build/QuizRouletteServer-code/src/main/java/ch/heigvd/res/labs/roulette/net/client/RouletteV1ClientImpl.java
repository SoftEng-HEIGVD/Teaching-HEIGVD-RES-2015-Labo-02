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
 * @author Olivier Liechti, Mathias Dolt
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

   private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

   private Socket serverSocket;
   private PrintWriter writer;
   private BufferedReader reader;

   @Override
   public void connect(String server, int port) throws IOException {
      if (isConnected()) {
         throw new IOException("Already connected");
      }

      serverSocket = new Socket(server, port);
      writer = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
      reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
      
      reader.readLine();
   }

   @Override
   public void disconnect() throws IOException {
      if (isConnected()) {
         writer.println(RouletteV1Protocol.CMD_BYE);
         writer.flush();
         writer.close();
         reader.close();
         serverSocket.close();
         writer = null;
         reader = null;
         serverSocket = null;
      } else {
         throw new IOException("Not connected");
      }
   }

   @Override
   public boolean isConnected() {
      return serverSocket != null && serverSocket.isConnected();
   }

   @Override
   public void loadStudent(String fullname) throws IOException {
      if (isConnected()) {
         List<Student> students = new ArrayList<>();
         students.add(new Student(fullname));
         loadStudents(students);
      } else {
         throw new IOException("Not connected");
      }
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException {
      if (isConnected()) {
         writer.println(RouletteV1Protocol.CMD_LOAD);
         writer.flush();

         String response = reader.readLine();
         if (!response.equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
            LOG.log(Level.INFO, "[DEBUG] " + response);
            throw new IOException("Response from server isn't correct");
         }

         for (Student s : students) {
            writer.println(s.getFullname());
         }

         writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
         writer.flush();
         reader.readLine();
      } else {
         throw new IOException("Not connected");
      }
   }

   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {
      if (isConnected()) {
         writer.println(RouletteV1Protocol.CMD_RANDOM);
         writer.flush();

         RandomCommandResponse rcr = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);

         if (rcr.getError() != null) {
            throw new EmptyStoreException();
         }

         return new Student(rcr.getFullname());
      } else {
         throw new IOException("Not connected");
      }
   }

   @Override
   public int getNumberOfStudents() throws IOException {
      if (isConnected()) {
         writer.println(RouletteV1Protocol.CMD_INFO);
         writer.flush();
         
         InfoCommandResponse icr = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

         return icr.getNumberOfStudents();
      } else {
         throw new IOException("Not connected");
      }
   }

   @Override
   public String getProtocolVersion() throws IOException {
      if (isConnected()) {
         writer.println(RouletteV1Protocol.CMD_INFO);
         writer.flush();

         InfoCommandResponse icr = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

         return icr.getProtocolVersion();
      } else {
         throw new IOException("Not connected");
      }
   }

}
