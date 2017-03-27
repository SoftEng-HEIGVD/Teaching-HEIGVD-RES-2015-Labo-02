package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  private Socket socket;
  private BufferedReader responseReader;
  private PrintWriter requestWriter;

  public RouletteV1ClientImpl() {
      socket = new Socket();
  }

  @Override
  public void connect(String server, int port) throws IOException {
      LOG.info("Connecting to server...");

      socket.connect(new InetSocketAddress(server, port));

      try{
          responseReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
          requestWriter  = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")));
      } catch (IOException e){
          e.printStackTrace();
      }

      // Flush initial server message
      responseReader.readLine();

      LOG.info("Connected !");
  }

  @Override
  public void disconnect() throws IOException {
      requestWriter.write("BYE\n");

      responseReader.close();
      requestWriter.close();
      socket.close();

      LOG.info("Disconnected");
  }

  @Override
  public boolean isConnected() {
      return socket != null && socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {

      LOG.info("Loading student...");

      requestWriter.write("LOAD\n");
      requestWriter.flush();

      responseReader.readLine();

      requestWriter.write(fullname+"\n");

      requestWriter.write("ENDOFDATA\n");
      requestWriter.flush();

      responseReader.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {

      LOG.info("Loading students...");
      requestWriter.write("LOAD\n");

      responseReader.readLine();

      for(Student student:students)
          requestWriter.write(student.getFullname()+"\n");

      requestWriter.write("ENDOFDATA\n");
      requestWriter.flush();

      responseReader.readLine();

  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {

      LOG.info("Picking random student...");
      String response;

      requestWriter.write("RANDOM\n");
      requestWriter.flush();

      response = responseReader.readLine();

      RandomCommandResponse parsedResponse = JsonObjectMapper.parseJson(response,RandomCommandResponse.class);



      if(parsedResponse.getFullname()==null)
          throw new EmptyStoreException();

      return new Student(parsedResponse.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {

      String response;

      requestWriter.write("INFO\n");
      requestWriter.flush();

      response = responseReader.readLine();

      InfoCommandResponse parsedResponse = JsonObjectMapper.parseJson(response,InfoCommandResponse.class);
      return parsedResponse.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {

      String response;

      requestWriter.write("INFO\n");
      requestWriter.flush();

      response = responseReader.readLine();

      InfoCommandResponse parsedResponse = JsonObjectMapper.parseJson(response,InfoCommandResponse.class);
      return parsedResponse.getProtocolVersion();
  }

}
