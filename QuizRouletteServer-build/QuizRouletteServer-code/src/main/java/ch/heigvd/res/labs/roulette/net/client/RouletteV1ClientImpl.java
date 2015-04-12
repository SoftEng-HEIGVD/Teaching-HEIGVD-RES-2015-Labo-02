package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  protected BufferedReader reader = null;
  protected PrintWriter writer = null;
  protected Socket socket = null;


  @Override
  public void connect(String server, int port) throws IOException {

    // Connection avec le serveur
    socket = new Socket(server,port);

    // le reader et writer proviennent de notre socket
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    writer = new PrintWriter(socket.getOutputStream());

    // Lecture du message de bienvenue
    reader.readLine();

  }

  @Override
  public void disconnect() throws IOException {



  }

  @Override
  public boolean isConnected() {

    if (socket != null)
      return socket.isConnected();

    return false;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {

    writer.println(RouletteV1Protocol.CMD_LOAD);
    writer.flush();
    // On lit le message reçu après la commande
    System.out.println(reader.readLine());

    writer.println(fullname);
    writer.flush();

    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();

    // On lit le message reçu après la commande
    System.out.println(reader.readLine());

  }

  /**
   * TODO
   * @param students
   * @throws IOException
   */
  @Override
  public void loadStudents(List<Student> students) throws IOException {
    for (Student student : students) {
      writer.println(student.getFullname());
    }
  }

  /**
   * TODO
   * @return
   * @throws EmptyStoreException
   * @throws IOException
   */
  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    Student s = new Student();
    return s;
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();

    // On déserialise le message reçu dans un objet InfoCommandResponse
    InfoCommandResponse icr = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
    return icr.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();

    // On déserialise le message reçu dans un objet InfoCommandResponse
    InfoCommandResponse icr = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
    return icr.getProtocolVersion();
  }


}