package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 * @author Zanone Jérémie & Wojciech Myszkorowski
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

   private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

   protected Socket clientSocket;
   protected BufferedReader in;
   protected PrintWriter out;

   @Override
   public void connect(String server, int port) throws IOException {
      // config the socket's I/O
      clientSocket = new Socket(server, port);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      out = new PrintWriter(clientSocket.getOutputStream());
      // read the message from the server
      in.readLine();

   }

   @Override
   public void disconnect() throws IOException {
      if (!clientSocket.isConnected()) {
         throw new IOException("Client already disconnected");
      }
      out.println(RouletteV1Protocol.CMD_BYE);
      out.flush();

      // closes all connections
      out.close();
      in.close();
      clientSocket.close();
   }

   @Override
   public boolean isConnected() {
      return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
   }

   @Override
   public void loadStudent(String fullname) throws IOException {
      if (!clientSocket.isConnected()) {
         throw new IOException("Client not connected");
      }

      out.println(RouletteV1Protocol.CMD_LOAD);
      out.flush();

      //check the correct response
      if (!in.readLine().equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
         throw new IOException("Bad server response");
      }

      // sent the name to the server
      out.println(fullname);

      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();

      if (!in.readLine().equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)) {
         throw new IOException("Bad server response");
      }
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException {
      if (!clientSocket.isConnected()) {
         throw new IOException("Client not connected");
      }

      // informs the server that we want to load some students
      out.println(RouletteV1Protocol.CMD_LOAD);
      out.flush();

      if (!in.readLine().equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
         throw new IOException("Bad server response");
      }

      // send all students of the list
      for (Student s : students) {
         out.println(s.getFullname());
      }

      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();

      if (!in.readLine().equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)) {
         throw new IOException("Bad server response");
      }
   }

   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {
      if (!clientSocket.isConnected()) {
         throw new IOException("Client not connected");
      }

      out.println(RouletteV1Protocol.CMD_RANDOM);
      out.flush();

      // parse the response from the server
      RandomCommandResponse info = JsonObjectMapper.parseJson(in.readLine(), RandomCommandResponse.class);

      if (info.getError() != null) {
         throw new EmptyStoreException();
      }

      return new Student(info.toString());
   }

   @Override
   public int getNumberOfStudents() throws IOException {
      if (!clientSocket.isConnected()) {
         throw new IOException("Client not connected");
      }

      out.println(RouletteV1Protocol.CMD_INFO);
      out.flush();

      // parse the response from the server
      InfoCommandResponse info = JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class);

      return info.getNumberOfStudents();
   }

   @Override
   public String getProtocolVersion() throws IOException {
      if (!clientSocket.isConnected()) {
         throw new IOException("Client not connected");
      }
      out.println(RouletteV1Protocol.CMD_INFO);
      out.flush();

      // parse the response from the server
      InfoCommandResponse info = JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class);

      return info.getProtocolVersion();
   }

}
