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
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import java.util.LinkedList;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti, Henrik Akesson
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  private Socket sock = null;
  private BufferedReader in = null;
  protected PrintWriter out = null;
  
  protected String readLine() throws IOException {
      String line;
      do {
          line = in.readLine();
      } while (line.equalsIgnoreCase("Hello. Online HELP is available. Will you find it?") 
            || line.equalsIgnoreCase("Huh? please use HELP if you don't know what commands are available."));
      return line;
  }
  
  @Override
    public void connect(String server, int port) throws IOException {
        if (isConnected()) {
            throw new IOException();
        }

        sock = new Socket();
        sock.connect(new InetSocketAddress(server, port));
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out = new PrintWriter(sock.getOutputStream());
    }
    

  @Override
    public void disconnect() throws IOException {
        if (!isConnected()) {
            throw new IOException();
        }

        out.println(RouletteV1Protocol.CMD_BYE);
        out.flush();

        in.close();
        out.close();
        sock.close();
    }

  @Override
  public boolean isConnected() {
      if (sock == null) {
         return false; 
      } else {
          return sock.isConnected() && !sock.isClosed();
      }
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      List<Student> list = new ArrayList<Student>();
      list.add(new Student(fullname));
      loadStudents(list);
  }

  @Override
    public void loadStudents(List<Student> students) throws IOException {
        out.println(RouletteV1Protocol.CMD_LOAD);
        out.flush();

        if (!readLine().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START)) {
            throw new IOException();
        }

        for (Student s : students) {
            out.println(s.getFullname());
        }

        out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        out.flush();

        readLine();
    }

  @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        out.println(RouletteV1Protocol.CMD_RANDOM);
        out.flush();

        RandomCommandResponse response = JsonObjectMapper.parseJson(readLine(), RandomCommandResponse.class);
        if (response.getError() != null) {
            throw new EmptyStoreException();
        }

        return new Student(response.getFullname());
    }

  @Override
  public int getNumberOfStudents() throws IOException {
      out.println(RouletteV1Protocol.CMD_INFO);
      out.flush();

      InfoCommandResponse icr = JsonObjectMapper.parseJson(readLine(), InfoCommandResponse.class);
      return icr.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
      out.println(RouletteV1Protocol.CMD_INFO);
      out.flush();

      InfoCommandResponse icr = JsonObjectMapper.parseJson(readLine(), InfoCommandResponse.class);
      return icr.getProtocolVersion();
  }



}