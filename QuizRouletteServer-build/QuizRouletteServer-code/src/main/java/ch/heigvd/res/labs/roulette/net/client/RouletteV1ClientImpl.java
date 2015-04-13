package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Bastien Rouiller / beedle-
 * @author Miguel Santamaria / edri
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

   protected Socket clientSocket;
   protected BufferedReader reader;
   protected PrintWriter writer;
   
  @Override
  public void connect(String server, int port) throws IOException { 
     // The client must not be already connected.
     if (!isConnected())
     {
         // Make a connection request on server.
         clientSocket = new Socket(server, port);

         // Init input-output stream objects.
         reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         writer = new PrintWriter(clientSocket.getOutputStream());

         // Read the welcome message for avoiding some futur error.
         reader.readLine();

         // Check if the client is correctly connected.
         if (!clientSocket.isConnected())
            throw new IOException("Unable to connect");
     }
  }

  @Override
  public void disconnect() throws IOException {
     // The client must be connected.
     if (isConnected())
     {
        // Send "BYE" command to server.
         writer.println(RouletteV1Protocol.CMD_BYE);
         writer.flush();

         // Close objects.
         clientSocket.close();
         clientSocket = null;
         writer.close();
         reader.close();
     }
  }

  @Override
  public boolean isConnected() {
     // The socket must not be null and must be connected.
     return clientSocket != null && clientSocket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
     // Send "LOAD" request to server.
     writer.println(RouletteV1Protocol.CMD_LOAD);
     writer.flush();
     
     // Read "Send your data [end with ENDOFDATA]" line.
     reader.readLine();
     
     writer.println(fullname);
     
     writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);     
     writer.flush();
     
     // Read "DATA LOADED" line.
     reader.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
     // Send "LOAD" request to server.
     writer.println(RouletteV1Protocol.CMD_LOAD);
     writer.flush();
     
     // Read "Send your data [end with ENDOFDATA]" line.
     reader.readLine();
     
     // Write each student's name.
     for (Student s : students)
        writer.println(s.getFullname());
     
     writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
     writer.flush();
     
     // Read "DATA LOADED" line.
     reader.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
     // Send "RANDOM" request, and get back the server's answer.
     writer.println(RouletteV1Protocol.CMD_RANDOM);
     writer.flush();
     
     String response = reader.readLine();
     
     if (response.contains("error"))
        throw new EmptyStoreException();
     else     
        return JsonObjectMapper.parseJson(response, Student.class);
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    // Send "INFO" request, and get back the server's answer.
     writer.println(RouletteV1Protocol.CMD_INFO);
     writer.flush();
    
     return JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class).getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
     // Send "INFO" request, and get back the server's answer.
     writer.println(RouletteV1Protocol.CMD_INFO);
     writer.flush();
    
     return JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class).getProtocolVersion();
  }

}
