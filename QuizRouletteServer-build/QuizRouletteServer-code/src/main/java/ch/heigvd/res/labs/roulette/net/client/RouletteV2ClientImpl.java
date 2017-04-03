package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.*;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

  public RouletteV2ClientImpl()
  {
      super();
  }

  @Override
  public void clearDataStore() throws IOException {
      serverWrite(RouletteV2Protocol.CMD_CLEAR);
      if(!serverRead().equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE))
          throw new IOException();
      LOG.info("Clear Data Store");
  }

  @Override
  public List<Student> listStudents() throws IOException{
    serverWrite(RouletteV2Protocol.CMD_LIST);
      ListCommandResponse ListStudents = JsonObjectMapper.parseJson(serverRead(),
              ListCommandResponse.class);

      return ListStudents.getStudents();
  }

    @Override
    public void loadStudent(String fullname) throws IOException {
        serverWrite(RouletteV2Protocol.CMD_LOAD);

        if (!serverRead().equals(RouletteV2Protocol.RESPONSE_LOAD_START))
            throw new IOException();

        serverWrite(fullname);
        serverWrite(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        LoadCommandResponse lcr = JsonObjectMapper.parseJson(serverRead(),
                LoadCommandResponse.class);

        if(!lcr.getStatus().equals("Success") && lcr.getNumberOfNewStudents() != 1)
            throw new LoadFailedException();

        LOG.info("Load student : " + fullname);
    }



    @Override
    public void loadStudents(List<Student> students) throws IOException
    {

        serverWrite(RouletteV2Protocol.CMD_LOAD);

        if(!serverRead().equals(RouletteV2Protocol.RESPONSE_LOAD_START))
            throw new IOException();

        for (Student student : students) {
            serverWrite(student.getFullname());
        }

        serverWrite(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        LoadCommandResponse lcr = JsonObjectMapper.parseJson(serverRead(),
                LoadCommandResponse.class);

        if(!lcr.getStatus().equals("Success") && lcr.getNumberOfNewStudents() != students.size())
            throw new LoadFailedException();

        for (Student student : students) {
            LOG.info("Load student : " + student.getFullname());
        }

    }

    public void disconnect() throws IOException{
      if(!socket.isConnected()){
          LOG.info("You are not connected");
          return;
      }
        serverWrite(RouletteV2Protocol.CMD_BYE);
        ByeCommandResponse bcr = JsonObjectMapper.parseJson(serverRead(),
                ByeCommandResponse.class);

        if(!bcr.getStatus().equals("success"))
            throw new ByeFailedException();
        socket.close();
        LOG.info("You type " + bcr.getNumberOfCommands() + "commands");

    }

}

