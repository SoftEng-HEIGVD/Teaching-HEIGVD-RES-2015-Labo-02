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
  private Socket connection;
  private PrintWriter writer;
  private BufferedReader reader;

  @Override
  public void connect(String server, int port) throws IOException {
     connection = new Socket(server, port);
     writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
     reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
     
     //lit le message de bienvenue
     readLine();
  }

  @Override
  public void disconnect() throws IOException {
     write(RouletteV1Protocol.CMD_BYE);
     writer.close();   
     reader.close();
     
     connection.close();
     connection = new Socket();
  }

  @Override
  public boolean isConnected() {
     return (connection != null) && connection.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
     write(RouletteV1Protocol.CMD_LOAD);
     
     //lit le message de début
     readLine();
     
     write(fullname);
     write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
     
     //lit le message de fin
     readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
     write(RouletteV1Protocol.CMD_LOAD);
     
     //lit le message de début
     readLine();
     
     for(Student student : students){
        write(student.getFullname());
     }
     write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
     
     //lit le message de fin
     readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      write(RouletteV1Protocol.CMD_RANDOM);
      
      RandomCommandResponse student = JsonObjectMapper.parseJson(readLine(), RandomCommandResponse.class);
      
      //vérifie s'il y a une erreur (base vide)
      if(student.getError() != null){
         throw new EmptyStoreException();
      }
      return new Student(student.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
      write(RouletteV1Protocol.CMD_INFO);
      
      InfoCommandResponse info = JsonObjectMapper.parseJson(readLine(), InfoCommandResponse.class);
      
      return info.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
      write(RouletteV1Protocol.CMD_INFO);
      
      InfoCommandResponse info = JsonObjectMapper.parseJson(readLine(), InfoCommandResponse.class);
      
      return info.getProtocolVersion();
  }
  
  // ces fonctions servirons pour ceux qui hériteront de cette classe
  
  public void write(String msg) throws IOException {
     writer.println(msg);
     writer.flush();
  }
  
  public String readLine() throws IOException  {
     return reader.readLine();
  }
}
