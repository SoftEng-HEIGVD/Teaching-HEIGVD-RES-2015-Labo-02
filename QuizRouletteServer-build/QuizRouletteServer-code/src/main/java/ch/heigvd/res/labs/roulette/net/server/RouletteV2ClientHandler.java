package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import static ch.heigvd.res.labs.roulette.net.server.RouletteV1ClientHandler.LOG;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * Modified by guiguismall, yoaaaarp
 * 
 * @author Olivier Liechti
 */
public class RouletteV2ClientHandler implements IClientHandler {

  private final IStudentsStore store;
  private int commandsIssued = 0;       // keeps track of how many commands
                                        // were issued by the client
    
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
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      switch (command.toUpperCase()) {
        case RouletteV2Protocol.CMD_RANDOM:
          commandsIssued++;
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
          commandsIssued++;
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV2Protocol.CMD_INFO:
          commandsIssued++;
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
            
        // updated command: LOAD
        case RouletteV2Protocol.CMD_LOAD:
          commandsIssued++;
          store.importData(reader);
          LoadCommandResponse loadresponse = new LoadCommandResponse("success", store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(loadresponse));
          writer.flush();
          break;
            
        // updated command: BYE
        case RouletteV2Protocol.CMD_BYE:
          commandsIssued++;
          ByeCommandResponse bcresponse = new ByeCommandResponse("success", commandsIssued);
          writer.println(JsonObjectMapper.toJson(bcresponse));
          writer.flush();
          done = true;
          break;
            
        // new command: CLEAR
        case RouletteV2Protocol.CMD_CLEAR:
          commandsIssued++;
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
            
        // new command: LIST
        case RouletteV2Protocol.CMD_LIST:
          ListCommandResponse lcresponse = new ListCommandResponse(store.listStudents());
          writer.println(JsonObjectMapper.toJson(lcresponse));
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
