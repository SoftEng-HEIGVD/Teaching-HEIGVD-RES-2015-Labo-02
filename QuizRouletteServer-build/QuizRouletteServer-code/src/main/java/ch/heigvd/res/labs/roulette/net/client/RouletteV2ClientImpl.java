package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.IIOException;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  @Override
  public void clearDataStore() throws IOException {
      writer.println(RouletteV2Protocol.CMD_CLEAR);
      writer.flush();
      String line;
      line=lineReader();
      if(!line.equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE)){
          throw new IOException("server response not correct....");
      }
      
  }

  @Override
  public List<Student> listStudents() throws IOException {
      writer.println(RouletteV2Protocol.CMD_LIST);
      writer.flush();
      StudentsList s = JsonObjectMapper.parseJson(lineReader(), StudentsList.class);
      return s.getStudents();
  }
  
  @Override
  public void disconnect() throws IOException{
      String line = lineReader();
      ByeCommandResponse bcr = JsonObjectMapper.parseJson(line, ByeCommandResponse.class);
      
      if(!bcr.getStatus().equalsIgnoreCase("success")){
          throw new IOException("Operation failure!");
      }
  }
  
    @Override
  public void loadStudents(List<Student> students) throws IOException {
      writer.println(RouletteV1Protocol.CMD_LOAD);
      writer.flush();
      String line;
      line=lineReader();
      if(!line.equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START)){
          throw new IOException("server response not correct....");
      }
      System.out.println(line);
      Iterator<Student> s = students.iterator();
      while(s.hasNext()){
          writer.println(s.next().getFullname());
          writer.flush();
      }
      writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      writer.flush();
      line=lineReader();
      LoadCommandResponse lcr = JsonObjectMapper.parseJson(line, LoadCommandResponse.class);
      System.out.println("Gnarf");
      if(!lcr.getStatus().equalsIgnoreCase("success")){
          throw new IOException("Operation failure!");
      }
      line=lineReader();
      if(!line.equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
          throw new IOException("server response not correct....");
      }
  }
  
}


