package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.net.protocol.*;
import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;


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
 * @author Edward Ransome
 * @author Michael Spierer
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());
  static final String STATUS_SUCCESS = "success";
  static final String STATUS_FAILURE = "failure";


  private final IStudentsStore store;

  private int numberOfCommands;


  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store = store;
    numberOfCommands=0;
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
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      numberOfCommands++;
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
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_INFO:
          InfoCommandResponse responseInfo = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(responseInfo));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LOAD:
          writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
          writer.flush();
          int numberOfCurrentStudent = store.getNumberOfStudents();
          String status = STATUS_SUCCESS;
          try{
             store.importData(reader);
          } catch (IOException e){
              status = STATUS_FAILURE;
          }
          LoadCommandResponse responseLoad = new LoadCommandResponse(status,store.getNumberOfStudents()-numberOfCurrentStudent);
          writer.println(responseLoad);
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:
          done = true;
          ByeCommandResponse responseBye = new ByeCommandResponse(STATUS_SUCCESS, numberOfCommands);
          writer.println(JsonObjectMapper.toJson(responseBye));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_CLEAR:
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;

        case RouletteV2Protocol.CMD_LIST:
          ListCommandResponse responseList = new ListCommandResponse(store.listStudents());
          writer.println(JsonObjectMapper.toJson(responseList));
          writer.flush();
          break;

        default:
          writer.println("Huh? please use HELP if you don't know what commands are available.");
          writer.flush();
          break;
      }
    }

  }
}
