package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
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
 * @author Olivier Liechti
 * @author Zanone Jérémie & Wojciech Myszkorowski
 */
public class RouletteV2ClientHandler implements IClientHandler {

   final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());
   private final IStudentsStore store;
   private int nbOfCommands = 0;

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
         nbOfCommands++;
         switch (command.toUpperCase()) {
            case RouletteV2Protocol.CMD_RANDOM: // no changes from V1
               RandomCommandResponse rcResponse = new RandomCommandResponse();
               try {
                  rcResponse.setFullname(store.pickRandomStudent().getFullname());
               } catch (EmptyStoreException ex) {
                  rcResponse.setError("There is no student, you cannot pick a random one");
               }
               writer.println(JsonObjectMapper.toJson(rcResponse));
               writer.flush();
               break;
            case RouletteV2Protocol.CMD_HELP: // no changes from V1
               writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
               break;
            case RouletteV2Protocol.CMD_INFO: // no changes from V1
               InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
               writer.println(JsonObjectMapper.toJson(response));
               writer.flush();
               break;
            case RouletteV2Protocol.CMD_LOAD:
               int nbOfStudents = store.getNumberOfStudents();
               writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
               writer.flush();
               store.importData(reader);
               int nbOfNewStudents = store.getNumberOfStudents() - nbOfStudents;
               writer.println("{\"status\":\"success\",\"numberOfNewStudents\":" + nbOfNewStudents + "}");
               writer.flush();
               break;
            case RouletteV2Protocol.CMD_LIST:
               StudentsList list = new StudentsList();
               list.addAll(store.listStudents());

               writer.println(JsonObjectMapper.toJson(list));

               writer.flush();
               break;
            case RouletteV2Protocol.CMD_CLEAR:
               store.clear();
               writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
               writer.flush();
               break;
            case RouletteV2Protocol.CMD_BYE:
               done = true;
               writer.println("{\"status\":\"success\",\"numberOfCommands\":" + nbOfCommands + "}");
               break;
            default:
               nbOfCommands--;
               writer.println("Huh? please use HELP if you don't know what commands are available.");
               writer.flush();
               break;
         }
         writer.flush();
      }

   }

}
