package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.Arrays;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Pombo Dias Miguel
 */
public class RouletteV2ClientHandler extends RouletteV1ClientHandler implements IClientHandler {

   int nbCommands = 0;
   
   public RouletteV2ClientHandler(IStudentsStore store) {
      super(store);
   }

   @Override
   protected void readCommand() throws IOException {
      nbCommands++;
      switch (command.toUpperCase()) {
         case RouletteV2Protocol.CMD_CLEAR:
            store.clear();
            writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
            writer.flush();
            break;
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
            break;
         case RouletteV2Protocol.CMD_INFO:
            InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
            writer.println(JsonObjectMapper.toJson(response));
            writer.flush();
            break;
         case RouletteV2Protocol.CMD_LOAD:
            writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
            writer.flush();
            int nbNewStudents = store.importData(reader);
            LoadCommandResponse loadResponse = new LoadCommandResponse(nbNewStudents);
            writer.println(JsonObjectMapper.toJson(loadResponse));
            writer.flush();
            break;
         case RouletteV2Protocol.CMD_LIST:
            StudentsList listStudents = new StudentsList();
            listStudents.addAll(store.listStudents());
            writer.println(JsonObjectMapper.toJson(listStudents));
            writer.flush();
            break;
         case RouletteV2Protocol.CMD_BYE:
            ByeCommandResponse byeResponse = new ByeCommandResponse(nbCommands);
            writer.println(JsonObjectMapper.toJson(byeResponse));
            writer.flush();
            done = true;
            break;
         default:
            writer.println("Huh? please use HELP if you don't know what commands are available.");
            writer.flush();
            break;
      }
      writer.flush();
   }
}
