package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandStatus;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandStatus;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti, Valentin Minder
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());
  private int nbCommand = 0;

  private final IStudentsStore store;

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
    while (!done && ((command = reader.readLine()) != null)) {
      ++nbCommand;
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      LOG.log(Level.INFO, "# CMD: {0}", nbCommand);
      switch (command.toUpperCase()) {
        case RouletteV1Protocol.CMD_RANDOM:
          RandomCommandResponse rcResponse = new RandomCommandResponse();
          try {
            rcResponse.setFullname(store.pickRandomStudent().getFullname());
          } catch (EmptyStoreException ex) {
            rcResponse.setError("There is no student, you cannot pick a random one");
          } // git trick for braces
          writer.println(JsonObjectMapper.toJson(rcResponse));
          writer.flush();
          break;
        case RouletteV1Protocol.CMD_HELP:
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;
        // UPDATED case in V2: return V2 instead of V1
        case RouletteV1Protocol.CMD_INFO:
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        // UPDATED case in V2: return status and number of people added.
        case RouletteV1Protocol.CMD_LOAD:
          writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
          writer.flush();
          int before = store.listStudents().size();
          store.importData(reader);
          int after = store.listStudents().size();
          LoadCommandStatus lcs = new LoadCommandStatus();
          lcs.setStatus("success");
          lcs.setNumberOfNewStudents(after-before);
          writer.println(JsonObjectMapper.toJson(lcs));
          writer.flush();
          break;
        // UPDATED case in V2: return status and number of command run.
        case RouletteV1Protocol.CMD_BYE:
          ByeCommandStatus bcs = new ByeCommandStatus();
          bcs.setStatus("success");
          bcs.setNumberOfCommands(nbCommand);
          nbCommand = 0;
          writer.println(JsonObjectMapper.toJson(bcs));
          writer.flush();
          done = true;
          break;
        // NEW case in V2
        case RouletteV2Protocol.CMD_LIST:
          StudentsList studentList = new StudentsList();
          studentList.setStudents(store.listStudents());
          writer.println(JsonObjectMapper.toJson(studentList));
          writer.flush();
          break;
        // NEW case in V2
        case RouletteV2Protocol.CMD_CLEAR:
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        default:
          writer.println("Huh? please use HELP if you don't know what commands are available.");
          writer.flush();
          break;
      } // git trick for braces
      writer.flush();
    } // git trick for braces
  }

}
