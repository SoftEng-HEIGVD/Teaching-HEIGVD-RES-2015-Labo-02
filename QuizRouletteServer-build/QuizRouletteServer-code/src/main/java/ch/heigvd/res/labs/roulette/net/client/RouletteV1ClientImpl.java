package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
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

  private Socket socket;
  private BufferedReader bis;
  private BufferedWriter bos;

  public RouletteV1ClientImpl(){
      socket = new Socket();
  }

  @Override
  public void connect(String server, int port) throws IOException {
      LOG.info("Connecting to server...");

      socket.connect(new InetSocketAddress(server,port));

      try{
          bis = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
          bos = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));

      }catch (IOException e){
          e.printStackTrace();
      }

      bis.readLine();

      LOG.info("Connected !");
  }

  @Override
  public void disconnect() throws IOException {
      bos.close();
      bis.close();
      socket.close();
      LOG.info("Disconnected");
  }

  @Override
  public boolean isConnected() {
      return socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {

      LOG.info("Loading student...");

      bos.write("LOAD\n");
      bos.flush();

      bis.readLine();

      bos.write(fullname+"\n");

      bos.write("ENDOFDATA\n");
      bos.flush();

      bis.readLine();


  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {

      LOG.info("Loading students...");
      bos.write("LOAD\n");

      bis.readLine();

      for(Student student:students)
          bos.write(student.getFullname()+"\n");

      bos.write("ENDOFDATA\n");
      bos.flush();

      bis.readLine();

  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {

      LOG.info("Picking random student...");
      String response;

      bos.write("RANDOM\n");
      bos.flush();

      response = bis.readLine();

      RandomCommandResponse parsedResponse = JsonObjectMapper.parseJson(response,RandomCommandResponse.class);



      if(parsedResponse.getFullname()==null)
          throw new EmptyStoreException();

      return new Student(parsedResponse.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {

      String response;

      bos.write("INFO\n");
      bos.flush();

      response = bis.readLine();

      InfoCommandResponse parsedResponse = JsonObjectMapper.parseJson(response,InfoCommandResponse.class);
      return parsedResponse.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {

      String response;

      bos.write("INFO\n");
      bos.flush();

      response = bis.readLine();

      InfoCommandResponse parsedResponse = JsonObjectMapper.parseJson(response,InfoCommandResponse.class);
      return parsedResponse.getProtocolVersion();
  }



}
