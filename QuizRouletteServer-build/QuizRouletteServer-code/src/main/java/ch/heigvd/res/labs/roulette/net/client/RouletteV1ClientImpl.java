package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client
{

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  protected BufferedReader reader;
  protected PrintWriter writer;
  protected Socket skt;

  @Override
  public void connect(String server, int port) throws IOException
  {
    //configuration du socket
    skt = new Socket(server, port);
    writer = new PrintWriter(skt.getOutputStream());
    InputStreamReader inpStrm = new InputStreamReader(skt.getInputStream());
    reader = new BufferedReader(inpStrm);

    //lecture du message du serveur
    reader.readLine();

  }

  @Override
  public void disconnect() throws IOException
  {
      if(skt.isConnected())
      {
        writer.println(RouletteV1Protocol.CMD_BYE);
        writer.flush();

        skt.close();
        writer.close();
        reader.close();
      }
      else
      {
         throw  new IOException("Already disconnected");
      }
  }

  @Override
  public boolean isConnected()
  {
      if(skt != null)
          return (skt.isConnected() && skt != null && !skt.isClosed());
      else
          return false;
  }

  @Override
  public void loadStudent(String fullname) throws IOException
  {
    if(skt.isConnected())
    {
      writer.println(RouletteV1Protocol.CMD_LOAD);
      writer.flush();
      if(!reader.readLine().equals(RouletteV1Protocol.RESPONSE_LOAD_START))
      {
        throw new IOException("Problem with server answer");
      }

      writer.println(fullname);
      writer.flush();
      writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      writer.flush();
      if(!reader.readLine().equals(RouletteV1Protocol.RESPONSE_LOAD_DONE))
      {
          throw new IOException("Problem with server answer");
      }
    }
    else
    {
       throw  new IOException("Not connected");
    }

  }

  @Override
  public void loadStudents(List<Student> students) throws IOException
  {
    if(skt.isConnected())
    {
      writer.println(RouletteV1Protocol.CMD_LOAD);
      writer.flush();
      if(!reader.readLine().equals(RouletteV1Protocol.RESPONSE_LOAD_START))
      {
        throw new IOException("Problem with server answer");
      }

      for(Student st : students)
        writer.println(st.getFullname());

      writer.flush();
      writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      writer.flush();
      if(!reader.readLine().equals(RouletteV1Protocol.RESPONSE_LOAD_DONE))
      {
        throw new IOException("Problem with server answer");
      }
    }
    else
    {
      throw  new IOException("Not connected");
    }
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException
  {
    if(skt.isConnected())
    {
      writer.println(RouletteV1Protocol.CMD_RANDOM);
      writer.flush();

      RandomCommandResponse randResp = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);

      if (randResp.getError() != null)
      {
        throw new EmptyStoreException();
      }

      return new Student(randResp.toString());
    }
    else
    {
      throw new IOException("Not connected");
    }
  }

  @Override
  public int getNumberOfStudents() throws IOException
  {
     if(skt.isConnected())
     {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();

        InfoCommandResponse randResp = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

        return randResp.getNumberOfStudents();
     }
     else
     {
        throw  new IOException("Not connected");
     }
  }

  @Override
  public String getProtocolVersion() throws IOException
  {
     if(skt.isConnected())
     {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();

       InfoCommandResponse randResp = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

       return randResp.getProtocolVersion();
     }
     else
     {
       throw  new IOException("Not connected");
     }
  }



}
