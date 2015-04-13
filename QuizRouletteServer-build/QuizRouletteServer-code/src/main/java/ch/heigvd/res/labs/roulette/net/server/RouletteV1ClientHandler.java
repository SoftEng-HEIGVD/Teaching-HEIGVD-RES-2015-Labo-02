package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
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
 * This class implements the Roulette protocol (version 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());

  private final IStudentsStore store;

  public RouletteV1ClientHandler(IStudentsStore store) {
    this.store = store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));

    pw.println("Hello. Online HELP is available. Will you find it?");
    pw.flush();

    String command;
    boolean done = false;
    while (!done && ((command = br.readLine()) != null)) {
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      switch (command.toUpperCase()) {
        case RouletteV1Protocol.CMD_RANDOM:
          RandomCommandResponse rcResponse = new RandomCommandResponse();
          try {
            rcResponse.setFullname(store.pickRandomStudent().getFullname());
          } catch (EmptyStoreException ex) {
            rcResponse.setError("There is no student, you cannot pick a random one");
          }
          pw.println(JsonObjectMapper.toJson(rcResponse));
          pw.flush();
          break;
        case RouletteV1Protocol.CMD_HELP:
          pw.println("Commands: " + Arrays.toString(RouletteV1Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV1Protocol.CMD_INFO:
          InfoCommandResponse response = new InfoCommandResponse(RouletteV1Protocol.VERSION, store.getNumberOfStudents());
          pw.println(JsonObjectMapper.toJson(response));
          pw.flush();
          break;
        case RouletteV1Protocol.CMD_LOAD:
          pw.println(RouletteV1Protocol.RESPONSE_LOAD_START);
          pw.flush();
          store.importData(br);
          pw.println(RouletteV1Protocol.RESPONSE_LOAD_DONE);
          pw.flush();
          break;
        case RouletteV1Protocol.CMD_BYE:
          done = true;
          break;
        default:
          pw.println("Huh? please use HELP if you don't know what commands are available.");
          pw.flush();
          break;
      }
      pw.flush();
    }

  }

}
