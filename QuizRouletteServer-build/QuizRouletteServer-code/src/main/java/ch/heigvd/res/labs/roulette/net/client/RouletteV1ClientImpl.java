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
 * @author Di Pietro & Gallandat (modified)
 *
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  Socket socket = null;
  BufferedReader r;
  PrintWriter w;
  
  @Override
  public void connect(String server, int port) throws IOException {
    socket = new Socket(server, port);
    r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    w = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    LOG.info(r.readLine());
  }

    @Override
    public void disconnect() throws IOException {
        if(this.isConnected()) {
            w.println(RouletteV1Protocol.CMD_BYE);
            w.flush();
        }

        try {
            w.close();
            r.close();
            socket.close();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        w = null;
        r = null;
        socket = null;
    }

  @Override
  public boolean isConnected() {
      return socket != null && socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    if(this.isConnected()){
        w.println(RouletteV1Protocol.CMD_LOAD);
        w.flush();
        
        if(!r.readLine().equals(RouletteV1Protocol.RESPONSE_LOAD_START))
            throw new IOException();
        
        w.println(fullname);
        w.flush();
        w.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        w.flush();
        r.readLine();
    }
    else
        throw new IOException("Client not connected");
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      if(this.isConnected()){
        w.println(RouletteV1Protocol.CMD_LOAD);
        w.flush();
        if(!r.readLine().equals(RouletteV1Protocol.RESPONSE_LOAD_START))
          throw new IOException();

        for(Student s : students){
            w.println(s.getFullname());
            w.flush();
        }

        w.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        w.flush();
        r.readLine();
      }
      else
        throw new IOException("Client not connected");
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      if(this.isConnected()){
          w.println(RouletteV1Protocol.CMD_RANDOM);
          w.flush();
          
          try{
              Student randStudent = JsonObjectMapper.parseJson(r.readLine(), Student.class);
              return randStudent;
          }catch(IOException e){
              throw new EmptyStoreException();
          }
      }
      throw new IOException("Client not connected");
  }

  @Override
  public int getNumberOfStudents() throws IOException {
      if(this.isConnected()){
          w.println(RouletteV1Protocol.CMD_INFO);
          w.flush();
          return JsonObjectMapper.parseJson(r.readLine(), InfoCommandResponse.class).getNumberOfStudents();
      }
      throw new IOException("Client not connected");
  }

  @Override
  public String getProtocolVersion() throws IOException {
      if(this.isConnected()){
          w.println(RouletteV1Protocol.CMD_INFO);
          w.flush();
          return JsonObjectMapper.parseJson(r.readLine(), InfoCommandResponse.class).getProtocolVersion();
      }
      throw new IOException("Client not connected");
  }
}
