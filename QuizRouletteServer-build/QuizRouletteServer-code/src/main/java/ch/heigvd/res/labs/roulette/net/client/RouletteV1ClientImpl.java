package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 * @author Ali Miladi
 * @author Quentin Zeller
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private BufferedReader in ;
    private BufferedWriter out ;
    private List<Student> students = new ArrayList<>();
    private boolean connected = false;

    @Override
  public void connect(String server, int port) throws IOException {
    Socket clientSocket = new Socket(server, port);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
      connected = true;
  }

  @Override
  public void disconnect() throws IOException {
    in.close();
    out.close();
    connected = false;
  }

  @Override
  public boolean isConnected() {
    return connected;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    students.add(new Student(fullname));
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    this.students = students;
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      if (this.getNumberOfStudents() == 0)
          throw new EmptyStoreException();
      else {
          Random r = new Random();
          return students.get(r.nextInt());
      }
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    return students.size();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    return RouletteV1Protocol.VERSION;
  }



}
