package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

   private Socket socket;
   private BufferedReader in;
   private PrintWriter out;

   private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

   private String readServerResponse() throws IOException {
      return in.readLine();
   }

   private void sendDataToServer(String data) {
      // write data (or command) to the server
      out.println(data);
      // Flush the writer to ensure it is immediatly sent
      out.flush();
   }

   @Override
   public void connect(String server, int port) throws IOException {

      // Create the socket and open the Writer and Reader
      socket = new Socket(server, port);
      out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
      in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

      // Read server welcome message
      readServerResponse();
   }

   @Override
   public void disconnect() throws IOException {
      sendDataToServer(RouletteV1Protocol.CMD_BYE);
      socket.close();
   }

   @Override
   public boolean isConnected() {
      return socket != null && !socket.isClosed() && socket.isConnected();
   }

   @Override
   public void loadStudent(String fullname) throws IOException {
      // Notify the server we are going to send him some data
      sendDataToServer(RouletteV1Protocol.CMD_LOAD);
      readServerResponse();
      // Send Student fullname
      sendDataToServer(fullname);
      // Notify the server we ended transmitting data
      sendDataToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      readServerResponse();
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException {
      // Notify the server we are going to send him some data
      sendDataToServer(RouletteV1Protocol.CMD_LOAD);
      readServerResponse();
      
      // Send all student information to the server
      for (Student s : students) {
         sendDataToServer(s.getFullname());
      }
      
      // Notify the server we ended transmitting data
      sendDataToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      readServerResponse();

   }

   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {
      // Ask the server for a random entry
      sendDataToServer(RouletteV1Protocol.CMD_RANDOM);
      // Read the server's answer
      String result = readServerResponse();
      // Process the response
      RandomCommandResponse response = JsonObjectMapper.parseJson(result, RandomCommandResponse.class);
      // If the server notified an error, throw the corresponding exception
      if (response.getError() != null) {
         throw new EmptyStoreException();
      }
      return new Student(response.getFullname());
   }
   
   private InfoCommandResponse getServerInformation() throws IOException {
      // Aske the server for information
      sendDataToServer(RouletteV1Protocol.CMD_INFO);
      // Read the answer
      String result = readServerResponse();
      // Process the result
      return JsonObjectMapper.parseJson(result, InfoCommandResponse.class);
      
   }

   @Override
   public int getNumberOfStudents() throws IOException {
      // Get server informations
      InfoCommandResponse response = getServerInformation();
      // Return the number of student
      return response.getNumberOfStudents();

   }

   @Override
   public String getProtocolVersion() throws IOException {
      // Get server informations
      InfoCommandResponse response = getServerInformation();
      // Return the number of student
      return response.getProtocolVersion();
   }

}
