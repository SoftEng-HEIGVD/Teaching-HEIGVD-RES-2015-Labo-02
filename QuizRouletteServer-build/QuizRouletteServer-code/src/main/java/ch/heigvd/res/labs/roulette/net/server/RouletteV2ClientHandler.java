package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 * @author Sydney Hauke
 * @author Thuy-My Tran
 */
public class RouletteV2ClientHandler implements IClientHandler {
  private final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());
  private IStudentsStore store;

  public RouletteV2ClientHandler(IStudentsStore store) {
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
    int nbCommands = 0;
    while (!done && ((command = reader.readLine()) != null)) {
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      nbCommands++;
      switch (command.toUpperCase()) {
        case RouletteV2Protocol.CMD_RANDOM:
          RandomCommandResponse rcResponse = new RandomCommandResponse();
          try {
            rcResponse.setFullname(store.pickRandomStudent().getFullname());
          } catch (EmptyStoreException ex) {
            rcResponse.setError("There is no student, you cannot pick a random one");
          }
          writer.println(JsonObjectMapper.toJson(rcResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_HELP:
          writer.println("Commands: " + Arrays.toString(RouletteV1Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV2Protocol.CMD_INFO:
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LOAD:
          writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
          writer.flush();
          int countBefore = store.getNumberOfStudents();
          store.importData(reader);
          int countAfter = store.getNumberOfStudents();
          writer.println(JsonObjectMapper.toJson(
                  new LoadCommandResponse(Status.Success, countAfter-countBefore)));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LIST:
            StudentsList studentsList = new StudentsList();
            studentsList.addAll(store.listStudents());
          writer.println(JsonObjectMapper.toJson(studentsList));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_CLEAR:
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:
          writer.println(JsonObjectMapper.toJson(
                  new ByeCommandResponse(Status.Success, nbCommands)
          ));
          writer.flush();
          done = true;
          break;
        default:
          writer.println("Huh? please use HELP if you don't know what commands are available.");
          writer.flush();
          nbCommands--;
          break;
      }
      writer.flush();
    }
  }

}
