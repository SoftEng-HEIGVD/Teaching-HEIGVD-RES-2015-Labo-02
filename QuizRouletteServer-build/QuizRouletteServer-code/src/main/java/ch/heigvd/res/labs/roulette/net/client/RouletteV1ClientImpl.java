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
import java.util.Iterator;
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
  protected Socket socket= new Socket();
  protected BufferedReader reader = null;
  protected PrintWriter writer = null;
  private final String hello ="Hello. Online HELP is available. Will you find it?";
  private final String errorCommand = "Huh? please use HELP if you don't know what commands are available.";

  /**
   * 
   * Permet de régler le problème du Unrecognized token 'Hello'
   * Permet également de filtrer le erreur de commande pour ne pas bloquer le programme
   * @return une ligne
   * @throws IOException 
   */
  protected String lineReader() throws IOException{
      String line;
      do{
          line = reader.readLine();
      }while(line.equalsIgnoreCase(hello) || line.equalsIgnoreCase(errorCommand));
      return line;
  }
  
  @Override
  public void connect(String server, int port) throws IOException {
    socket = new Socket(server,port);
    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    
  }

  @Override
  public void disconnect() throws IOException {
    writer.println(RouletteV1Protocol.CMD_BYE);
    writer.flush();
    writer.close();
    reader.close();
    socket.close();
  }

  @Override
  public boolean isConnected() {
      if(socket==null){
          return false;
      }
      else{
          return socket.isConnected() && !socket.isClosed();
      }
  }
  
  private void endLoad()throws IOException{
      if(!lineReader().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
          throw new IOException("server response not correct....");
      }
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      List<Student> temp = new LinkedList<Student>();
      temp.add(new Student(fullname));
      loadStudents(temp);
              
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      writer.println(RouletteV1Protocol.CMD_LOAD);
      writer.flush();
      
      if(!lineReader().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START)){
          throw new IOException("server response not correct....");
      }
      Iterator<Student> s = students.iterator();
      while(s.hasNext()){
          writer.println(s.next().getFullname());
          writer.flush();
      }
      writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      writer.flush();
//      if(!lineReader().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
//          throw new IOException("server response not correct....");
//      }
      endLoad();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      String line;
      writer.println(RouletteV1Protocol.CMD_RANDOM);
      writer.flush();
      line=lineReader();
      RandomCommandResponse RCR = JsonObjectMapper.parseJson(line, RandomCommandResponse.class);
      if(RCR.getError() == null){
          return new Student(RCR.getFullname());
      }
      else{
          throw new EmptyStoreException();
      }
  }

  @Override
  public int getNumberOfStudents() throws IOException {
      String line;
      writer.println(RouletteV1Protocol.CMD_INFO);
      writer.flush();
      line=lineReader();
      int nombStudents = JsonObjectMapper.parseJson(line, InfoCommandResponse.class).getNumberOfStudents();
      return nombStudents;
  }

  @Override
  public String getProtocolVersion() throws IOException {
      String line;
      writer.println(RouletteV1Protocol.CMD_INFO);
      writer.flush();
      line=lineReader();
      return JsonObjectMapper.parseJson(line, InfoCommandResponse.class).getProtocolVersion();
      
  }



}