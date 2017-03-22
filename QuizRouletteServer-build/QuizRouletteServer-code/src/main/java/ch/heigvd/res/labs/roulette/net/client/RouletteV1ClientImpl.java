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

  private Socket socket = new Socket();

  public RouletteV1ClientImpl(){
      socket = new Socket();
  }

  @Override
  public void connect(String server, int port) throws IOException {
      socket.connect(new InetSocketAddress(server,port));
      BufferedReader bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      bis.readLine();
  }

  @Override
  public void disconnect() throws IOException {
      socket.close();
  }

  @Override
  public boolean isConnected() {
      return socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      OutputStream os = socket.getOutputStream();
      BufferedReader bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      os.write("LOAD".getBytes("UTF-8"));
      os.write('\n');

      bis.readLine();

      os.write(fullname.getBytes("UTF-8"));
      os.write('\n');

      os.write("ENDOFDATA".getBytes("UTF-8"));
      os.write('\n');

      bis.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {

      OutputStream os = socket.getOutputStream();
      BufferedReader bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      os.write("LOAD".getBytes("UTF-8"));
      os.write('\n');

      bis.readLine();

      for(Student student:students) {
          os.write(student.getFullname().getBytes("UTF-8"));
          os.write('\n');
      }

      os.write("ENDOFDATA".getBytes("UTF-8"));
      os.write('\n');

      bis.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {

      OutputStream os = socket.getOutputStream();
      os.write("RANDOM".getBytes("UTF-8"));
      os.write('\n');

      BufferedReader bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      String response = bis.readLine();

      RandomCommandResponse parsedResponse = JsonObjectMapper.parseJson(response,RandomCommandResponse.class);

      if(parsedResponse.getFullname()==null)
          throw new EmptyStoreException();

      return new Student(parsedResponse.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
      OutputStream os = socket.getOutputStream();
      os.write("INFO".getBytes("UTF-8"));
      os.write('\n');

      BufferedReader bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      String response = bis.readLine();

      InfoCommandResponse parsedResponse = JsonObjectMapper.parseJson(response,InfoCommandResponse.class);
      return parsedResponse.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
      OutputStream os = socket.getOutputStream();
      os.write("INFO".getBytes("UTF-8"));
      os.write('\n');

      BufferedReader bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      String response = bis.readLine();

      InfoCommandResponse parsedResponse = JsonObjectMapper.parseJson(response,InfoCommandResponse.class);
      return parsedResponse.getProtocolVersion();
  }



}
