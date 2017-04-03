package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti (& small contribution Kevin Moreira)
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());

  private int nbrCommand = 0; // number of commands called
  private final IStudentsStore store;

  public RouletteV2ClientHandler(IStudentsStore store)
  {
    this.store = store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

    writer.println("Hello. Online HELP is available. Will you find it?");
    writer.flush();

    String command;
    boolean done = false;
    while (!done && ((command = reader.readLine()) != null)) {
      nbrCommand++;
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      switch (command.toUpperCase()) {
        case RouletteV1Protocol.CMD_RANDOM:
          RandomCommandResponse rcResponse = new RandomCommandResponse();
          try {
            rcResponse.setFullname(store.pickRandomStudent().getFullname());
          } catch (EmptyStoreException ex) {
            rcResponse.setError("There is no student, you cannot pick a random one");
          }
          writer.println(JsonObjectMapper.toJson(rcResponse));
          writer.flush();
          break;
        case RouletteV1Protocol.CMD_HELP:
          writer.println("Commands: " + Arrays.toString(RouletteV1Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV1Protocol.CMD_INFO: //just change the version from v1 to v2
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        case RouletteV1Protocol.CMD_LOAD:
          writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
          writer.flush();
          store.importData(reader);

          //different code from v1 (creation of JSON)
          LoadCommandResponse loadC = new LoadCommandResponse(RouletteV2Protocol.SUCCESS, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(loadC));
          writer.flush();
          break;
        case RouletteV1Protocol.CMD_BYE:
          //different from v1 creation of JSON
          ByeCommandResponse byeC = new ByeCommandResponse(RouletteV2Protocol.SUCCESS, nbrCommand);
          writer.println(JsonObjectMapper.toJson(byeC));
          writer.flush();
          done = true;
          break;
        case RouletteV2Protocol.CMD_LIST: // Adding command not in v1
          StudentsList listStu = new StudentsList();
          listStu.addAll(store.listStudents());
          // creation of JSON
          writer.println(JsonObjectMapper.toJson(listStu));
          writer.flush();
          break;

        case RouletteV2Protocol.CMD_CLEAR: // Adding command not in v1
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        default:
          writer.println("Huh? please use HELP if you don't know what commands are available.");
          writer.flush();
          break;
      }
      writer.flush();
    }

  }

}
