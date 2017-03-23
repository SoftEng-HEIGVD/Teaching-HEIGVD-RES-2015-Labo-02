package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.*;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  private Socket socket = new Socket();
  private StudentsStoreImpl students = new StudentsStoreImpl();

  @Override
  public void connect(String server, int port) throws IOException {
    socket = new Socket(server, port);
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
    students.addStudent(new Student(fullname));
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    for (Student s : students)
      this.students.addStudent(s);
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    return students.pickRandomStudent();
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    return students.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    return "1.0";
  }



}
