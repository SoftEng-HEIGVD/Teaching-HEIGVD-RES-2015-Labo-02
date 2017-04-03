package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientHandler implements IClientHandler {

  private int nbComands;

  final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());

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

     nbComands = 0;

    String command;
    boolean done = false;
    while (!done && ((command = reader.readLine()) != null)) {
       nbComands++;
       LOG.log(Level.INFO, "COMMAND: " + nbComands + " : " + command);
       switch (command.toUpperCase())
       {
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

          default:
             writer.println("Huh? please use HELP if you don't know what commands are available.");
             writer.flush();
             break;
          //V2 PROTOCOL
          case RouletteV2Protocol.CMD_CLEAR:
             store.clear();
             writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
             writer.flush();
             break;

          case RouletteV2Protocol.CMD_LIST:
             ListCommandResponse listResponse = new ListCommandResponse(store.listStudents());
             writer.println(JsonObjectMapper.toJson(listResponse));
             writer.flush();
             break;

          case RouletteV2Protocol.CMD_LOAD:
             writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
             writer.flush();
             int nbStudents = store.getNumberOfStudents();
             store.importData(reader);
             nbStudents = store.getNumberOfStudents() - nbStudents;
             LoadCommandResponse lcr = new LoadCommandResponse("success",nbStudents);
             writer.println(JsonObjectMapper.toJson(lcr));
             writer.flush();
             break;

          case RouletteV2Protocol.CMD_BYE:
             ByeCommandResponse bcr = new ByeCommandResponse("success",nbComands);
             writer.println(JsonObjectMapper.toJson(bcr));
             writer.flush();
             done = true;
             break;

          case RouletteV2Protocol.CMD_INFO:
             InfoCommandResponse icr = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
             writer.println(JsonObjectMapper.toJson(icr));
             writer.flush();
             break;

       }
      writer.flush();
    }
  }
}
