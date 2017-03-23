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
 * @autor Loan Lassalle and Tano Iannetta
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;

  @Override
  public void connect(String server, int port) throws IOException {
    socket = new Socket(server, port);

    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream());

    receive();
  }

  @Override
  public void disconnect() throws IOException {
    send(RouletteV1Protocol.CMD_BYE);

    in.close();
    out.close();
    socket.close();

    LOG.info("Disconnected");
  }

  @Override
  public boolean isConnected() {
      return socket != null && socket.isConnected();
  }

  public String receive() throws IOException {
    return in.readLine();
  }

  public void send(String message) throws IOException {
    out.println(message);
    out.flush();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    send(RouletteV1Protocol.CMD_LOAD);
    receive();

    send(fullname);

    send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    receive();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    send(RouletteV1Protocol.CMD_LOAD);
    receive();

    for (Student s : students) {
        send(s.getFullname());
    }

    send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    receive();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      send(RouletteV1Protocol.CMD_RANDOM);

      RandomCommandResponse reponse = JsonObjectMapper.parseJson(receive(), RandomCommandResponse.class);

      if (!reponse.getError().isEmpty())
      {
          throw new EmptyStoreException();
      }

      return new Student(reponse.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
      send(RouletteV1Protocol.CMD_INFO);

      InfoCommandResponse reponse = JsonObjectMapper.parseJson(receive(), InfoCommandResponse.class);

      return reponse.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    send(RouletteV1Protocol.CMD_INFO);

    InfoCommandResponse reponse = JsonObjectMapper.parseJson(receive(), InfoCommandResponse.class);

    return reponse.getProtocolVersion();
  }



}
