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
 * @author Matthieu Chatelan
 * @author Lara Chauffoureaux
 */
public class RouletteV2ClientHandler implements IClientHandler
{

   final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());

   private final IStudentsStore store;
   private int numberOfCommands = 0; // Used to count the number of commands

   public RouletteV2ClientHandler(IStudentsStore store)
   {
      this.store = store;
   }

   @Override
   public void handleClientConnection(InputStream is, OutputStream os) throws IOException
   {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

      writer.println("Hello. Online HELP is available. Will you find it?");
      writer.flush();

      String command;
      boolean done = false;
      while (!done && ((command = reader.readLine()) != null))
      {
         // Increment for each line considered as a command (thus not student seizure) 
         numberOfCommands++;

         LOG.log(Level.INFO, "COMMAND: {0}", command);
         switch (command.toUpperCase())
         {
            case RouletteV1Protocol.CMD_RANDOM: // Same as v1
               RandomCommandResponse rcResponse = new RandomCommandResponse();
               try
               {
                  rcResponse.setFullname(store.pickRandomStudent().getFullname());
               } catch (EmptyStoreException ex)
               {
                  rcResponse.setError("There is no student, you cannot pick a random one");
               }
               writer.println(JsonObjectMapper.toJson(rcResponse));
               writer.flush();
               break;

            case RouletteV1Protocol.CMD_HELP: // Same as v1
               writer.println("Commands: " + Arrays.toString(RouletteV1Protocol.SUPPORTED_COMMANDS));
               break;

            case RouletteV1Protocol.CMD_INFO: // Same as v1 (but with the v2 protocol version in the object)
               InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
               writer.println(JsonObjectMapper.toJson(response));
               writer.flush();
               break;

            case RouletteV1Protocol.CMD_LOAD: // Modified since v1
               writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
               writer.flush();
               store.importData(reader);
               
               // Creation /Jsonification of a LoadCommandResponse object before sending it
               LoadCommandResponse reply = new LoadCommandResponse(RouletteV2Protocol.SUCCESS, store.getNumberOfStudents());
               writer.println(JsonObjectMapper.toJson(reply));
               writer.flush();
               break;

            case RouletteV1Protocol.CMD_BYE: // Modified since v1
               // Creation / Jsonification of a ByeCommandResponse object before sending it
               ByeCommandResponse bye = new ByeCommandResponse(RouletteV2Protocol.SUCCESS, numberOfCommands);
               writer.println(JsonObjectMapper.toJson(bye));
               writer.flush();
               done = true;
               break;

            case RouletteV2Protocol.CMD_LIST: // New command
               // Creation of the student list
               StudentsList list = new StudentsList();
               list.addAll(store.listStudents());

               // Jsonification before sending it
               writer.println(JsonObjectMapper.toJson(list));
               writer.flush();
               break;

            case RouletteV2Protocol.CMD_CLEAR: // New command
               store.clear();
               writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
               writer.flush();
               break;

            default: // Same as v1
               writer.println("Huh? please use HELP if you don't know what commands are available.");
               writer.flush();
               break;
         }
         writer.flush();
      }
   }
}
