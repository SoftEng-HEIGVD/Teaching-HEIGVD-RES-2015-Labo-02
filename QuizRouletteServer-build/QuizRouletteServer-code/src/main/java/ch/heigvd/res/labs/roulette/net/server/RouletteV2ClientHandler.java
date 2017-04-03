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
  BufferedReader reader;
  PrintWriter writer;

  private final IStudentsStore store;

  private int numberOfCommands;


  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store = store;
    numberOfCommands=0;

  }

  void sendToClient(String s){
    writer.println(s);
    writer.flush();
  }


  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    reader = new BufferedReader(new InputStreamReader(is));
    writer = new PrintWriter(new OutputStreamWriter(os));

    sendToClient("Hello. Online HELP is available. Will you find it?");

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
          sendToClient(JsonObjectMapper.toJson(rcResponse));

          break;
        case RouletteV2Protocol.CMD_HELP:
          sendToClient("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV2Protocol.CMD_INFO:
          InfoCommandResponse responseInfo = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());

          sendToClient(JsonObjectMapper.toJson(responseInfo));
          break;
        case RouletteV2Protocol.CMD_LOAD:
          sendToClient(RouletteV2Protocol.RESPONSE_LOAD_START);
          int numberOfCurrentStudent = store.getNumberOfStudents();
          String status = STATUS_SUCCESS;
          try{
             store.importData(reader);
            LoadCommandResponse responseLoad = new LoadCommandResponse(status,store.getNumberOfStudents()-numberOfCurrentStudent);
            sendToClient(JsonObjectMapper.toJson(responseLoad));
          } catch (IOException e){
            System.out.println("HELLO FAIL");
              status = STATUS_FAILURE;
          }
          System.out.println(status);

          break;
        case RouletteV2Protocol.CMD_BYE:
          done = true;
          ByeCommandResponse responseBye = new ByeCommandResponse(STATUS_SUCCESS, numberOfCommands);
          sendToClient(JsonObjectMapper.toJson(responseBye));

          break;
        case RouletteV2Protocol.CMD_CLEAR:
          store.clear();
          sendToClient(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          break;

        case RouletteV2Protocol.CMD_LIST:
          ListCommandResponse responseList = new ListCommandResponse(store.listStudents());
          sendToClient(JsonObjectMapper.toJson(responseList));
          break;

        default:
          sendToClient("Huh? please use HELP if you don't know what commands are available.");
          break;
      }
    }

  }
}
