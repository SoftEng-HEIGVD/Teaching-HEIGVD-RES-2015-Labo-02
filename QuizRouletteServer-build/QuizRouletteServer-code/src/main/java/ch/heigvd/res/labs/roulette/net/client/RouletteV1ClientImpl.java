package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
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

  static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  
  private Socket s;
  
  private BufferedReader buffRead;
  protected PrintWriter printWrite;

  @Override
  public void connect(String server, int port) throws IOException {
    s = new Socket(server, port);
  }

  @Override
  public void disconnect() throws IOException {
    printWrite.println(RouletteV1Protocol.CMD_BYE);
    printWrite.flush();
    printWrite.close();
    buffRead.close();
    s.close();
  }

  @Override
  public boolean isConnected() {
    return (s != null && s.isConnected() && !s.isClosed());
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    List<Student> l = new LinkedList<Student>();
    l.add(new Student(fullname));
    loadStudents(l);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    printWrite.println(RouletteV1Protocol.CMD_LOAD);
    printWrite.flush();
    if(readLine().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START)) {
        for(Student s : students) {
            printWrite.println(s.getFullname());
        }
        
        printWrite.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        printWrite.flush();
        
        readLine();
    }
    else {
        throw new IOException("Unexpected response");
    }
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    RandomCommandResponse r = getRCR();
    
    if(r.getError() == null) {
        return new Student(r.getFullname());
    }
    else {
        throw new EmptyStoreException();
    }
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    return getICR().getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    return getICR().getProtocolVersion();
  }
  
  protected InfoCommandResponse getICR() throws IOException {
    printWrite.println(RouletteV1Protocol.CMD_INFO);
    printWrite.flush();
    return JsonObjectMapper.parseJson(readLine(), InfoCommandResponse.class);
  }
  
  protected RandomCommandResponse getRCR() throws IOException {
    printWrite.println(RouletteV1Protocol.CMD_RANDOM);
    printWrite.flush();
    return JsonObjectMapper.parseJson(readLine(), RandomCommandResponse.class);
  }

  protected String readLine() throws IOException {
    String s;
    do {
      s = buffRead.readLine();
    }
    while (s.equalsIgnoreCase("Hello. Online HELP is available. Will you find it?") || s.equalsIgnoreCase("Huh? please use HELP if you don't know what commands are available."));
    return s;
  }

}
