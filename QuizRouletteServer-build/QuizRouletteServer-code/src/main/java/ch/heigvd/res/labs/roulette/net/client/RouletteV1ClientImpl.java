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
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client
{

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  protected Socket socket;

   PrintWriter out;
   BufferedReader in;


   protected String serverRead() throws IOException
  {
    return in.readLine();
  }

  protected void serverWrite(String toWrite)
  {
    out.println(toWrite);
    out.flush();
  }

  public RouletteV1ClientImpl()
  {
      socket = new Socket();
  }

  @Override
  public void connect(String server, int port) throws IOException
  {

    socket = new Socket(server, port);

     try
     {
        out = new PrintWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
     }
     catch (IOException e)
     {
        LOG.warning(e.toString());
     }
    LOG.info(serverRead());
  }

  @Override
  public void disconnect() throws IOException
  {
    serverWrite(RouletteV1Protocol.CMD_BYE);
    out.close();
    in.close();
    socket.close();
  }

  @Override
  public boolean isConnected()
  {
    LOG.info(String.valueOf(socket.isConnected()));
    return socket.isConnected() && !socket.isClosed();
  }

  @Override
  public void loadStudent(String fullname) throws IOException
  {
    serverWrite(RouletteV1Protocol.CMD_LOAD);

    if(!serverRead().equals(RouletteV1Protocol.RESPONSE_LOAD_START))
      throw new IOException();

    serverWrite(fullname);
    serverWrite(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    if(!serverRead().equals(RouletteV1Protocol.RESPONSE_LOAD_DONE))
      throw new IOException();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException
  {

    serverWrite(RouletteV1Protocol.CMD_LOAD);
    if(!serverRead().equals(RouletteV1Protocol.RESPONSE_LOAD_START))
      throw new IOException();

    for (Student student : students) {
      serverWrite(student.getFullname());
    }
    serverWrite(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    if(!serverRead().equals(RouletteV1Protocol.RESPONSE_LOAD_DONE))
      throw new IOException();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException
  {
      serverWrite(RouletteV1Protocol.CMD_RANDOM);
      Student gerard = new Student();
      RandomCommandResponse rcr = JsonObjectMapper.parseJson(serverRead(),
              RandomCommandResponse.class);

      if(rcr.getError() != null) throw new EmptyStoreException();

      gerard.setFullname(rcr.getFullname());
      return gerard;
  }

  @Override
  public int getNumberOfStudents() throws IOException
  {
    serverWrite(RouletteV1Protocol.CMD_INFO);
    return JsonObjectMapper.parseJson(serverRead(),InfoCommandResponse.class).getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException
  {
    serverWrite(RouletteV1Protocol.CMD_INFO);
    return JsonObjectMapper.parseJson(serverRead(),InfoCommandResponse.class).getProtocolVersion();
  }
}
