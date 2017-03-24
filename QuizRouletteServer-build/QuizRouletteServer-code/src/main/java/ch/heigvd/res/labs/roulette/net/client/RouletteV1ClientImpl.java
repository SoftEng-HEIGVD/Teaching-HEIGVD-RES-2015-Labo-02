package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.*;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 * @authors Ludovic Richard, Luana Martelli
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  /**
   * We used the decorator BufferedReader and PrintWriter like it was used in other files
   * InputStream reads the next byte and InputStreamReader is more specific, it reads next char
   * Finally, BufferedReader reads a string
   * For writing, we use PrintWriter because it's more powerful that BufferedWriter. It has method like println()
   * that is more specific than just write
   */

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  private Socket socket = new Socket();


  @Override
  public void connect(String server, int port) throws IOException {
    /* Connexion to a server with a specific port */
    socket = new Socket(server, port);

    /* We read the welcome message */
    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    reader.readLine();
  }

  @Override
  public void disconnect() throws IOException {
    /* Close connexion */
    socket.close();
  }

  @Override
  public boolean isConnected() {
    return socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {

    BufferedReader reader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

    /* Sends the command */
    writer.println(RouletteV1Protocol.CMD_LOAD);
    writer.flush();
    /* Reads the answer */
    reader.readLine();

    /* Writes the name of the student */
    writer.println(fullname);
    writer.flush();

    /* End of data */
    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();

    /* Reads server's message */
    reader.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

    /* Sends command */
    writer.println(RouletteV1Protocol.CMD_LOAD);
    writer.flush();

    /* Reads the answer */
    reader.readLine();

    /* Add students */
    for (Student s : students) {
      writer.println(s.getFullname());
      writer.flush();
      reader.readLine();
    }

    /* End of command */
    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();
    reader.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

    /* Sends command */
    writer.println(RouletteV1Protocol.CMD_RANDOM);
    writer.flush();

    /* We get the answer from the server */
    RandomCommandResponse info = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);

    /* If the student has no name, then the list is empty */
    if (info.getFullname() == null) {
      throw new EmptyStoreException();
    }
    return new Student(info.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

    /* Sends command */
    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();

    /* Gets answer from the json class */
    InfoCommandResponse info = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

    return info.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

    /* Sends command */
    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();

    /* Gets answer from json class */
    InfoCommandResponse info = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
    return info.getProtocolVersion();
  }

}
