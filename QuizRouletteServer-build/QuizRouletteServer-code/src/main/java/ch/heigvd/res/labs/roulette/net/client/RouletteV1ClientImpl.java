package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;

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
  private Socket clientSocket;
  private BufferedReader in;
  private PrintWriter out;

  protected String getServerResponse() throws IOException {
    return in.readLine();
  }

  protected void sendRequestToServer(String req){
    out.println(req);
    out.flush();
  }

  @Override
  public void connect(String server, int port) throws IOException {
    clientSocket = new Socket(server, port);
    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    getServerResponse(); // Welcome msg from the server
  }

  @Override
  public void disconnect() throws IOException {
    sendRequestToServer(RouletteV1Protocol.CMD_BYE);
    clientSocket.close();
  }

  @Override
  public boolean isConnected() {
    return clientSocket != null && !clientSocket.isClosed() && clientSocket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    // set server in load mode
    sendRequestToServer(RouletteV1Protocol.CMD_LOAD);
    getServerResponse();

    // send to student fullname
    sendRequestToServer(fullname);

    // notify the server that transmission is done
    sendRequestToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    // get server response
    getServerResponse();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    // set server in load mode
    sendRequestToServer(RouletteV1Protocol.CMD_LOAD);
    getServerResponse();

    for (Student s: students) {
      sendRequestToServer(s.getFullname());
    }

    // notify the server that transmission is done
    sendRequestToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    // get server response
    getServerResponse();

  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    // ask a random student to the server
    sendRequestToServer(RouletteV1Protocol.CMD_RANDOM);
    // store server response
    String res = getServerResponse();
    // parse the response
    RandomCommandResponse parsedResponse = JsonObjectMapper.parseJson(res, RandomCommandResponse.class);

    // throw error if the server responded with an error
    if (parsedResponse.getError() != null) {
      throw new EmptyStoreException();
    }

    return new Student(parsedResponse.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    // ask the current status of the server
    sendRequestToServer(RouletteV1Protocol.CMD_INFO);
    // store server response
    String res = getServerResponse();
    // parse the response
    InfoCommandResponse parsedResponse = JsonObjectMapper.parseJson(res, InfoCommandResponse.class);

    return parsedResponse.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    // ask the current status of the server
    sendRequestToServer(RouletteV1Protocol.CMD_INFO);
    // store server response
    String res = getServerResponse();
    // parse the response
    InfoCommandResponse parsedResponse = JsonObjectMapper.parseJson(res, InfoCommandResponse.class);

    return parsedResponse.getProtocolVersion();
  }



}