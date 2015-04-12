package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
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
 * @author Toni Dias
 * @author Bryan Perroud
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());
  private int numberOfCommands = 0;
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
      ++numberOfCommands;  
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      LOG.log(Level.INFO, "# CMD: {0}", numberOfCommands);
      switch (command.toUpperCase()) {
          
        //La commande RANDOM ne change pas dans la V2 donc appel de celui de la V1
        case RouletteV1Protocol.CMD_RANDOM:
          RandomCommandResponse rcResponse = new RandomCommandResponse();
          try {
            rcResponse.setFullname(store.pickRandomStudent().getFullname());
          } catch (EmptyStoreException ex) {
            rcResponse.setError("There is no student, you cannot pick a random one");
          }
          writer.println(JsonObjectMapper.toJson(rcResponse));
          writer.flush();
          break;
            
        //La commande HELP ne change pas dans la V2 donc appel de celui de la V1
        case RouletteV1Protocol.CMD_HELP:
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;
            
        //La commande INFO est mise à jour pour la V2 (Retourne V2)
        case RouletteV1Protocol.CMD_INFO:
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        
        //La commande LOAD est mise à jour pour la V2
        case RouletteV1Protocol.CMD_LOAD:
          writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
          writer.flush();
          int numberStudentBefore = store.getNumberOfStudents();
          store.importData(reader);
          int numberStudentAfter = store.getNumberOfStudents();
          LoadCommandResponse lcr = new LoadCommandResponse();
          lcr.setStatus("success");
          lcr.setNumberOfNewStudents(numberStudentAfter-numberStudentBefore);
          writer.println(JsonObjectMapper.toJson(lcr));
          writer.flush();
          break;
        
        //La commande BYE est mise à jour pour la V2 (Retourne le statut et le nombre de commandes)
        case RouletteV1Protocol.CMD_BYE:
          ByeCommandResponse bcr = new ByeCommandResponse();
          bcr.setStatus("success");
          bcr.setNumberOfCommands(numberOfCommands);
          numberOfCommands = 0;
          writer.println(JsonObjectMapper.toJson(bcr));
          writer.flush();
          done = true;
          break;
            
        //La commande LIST seulement dans la V2
        case RouletteV2Protocol.CMD_LIST:
          StudentsList studentList = new StudentsList();
          studentList.setStudents(store.listStudents());
          writer.println(JsonObjectMapper.toJson(studentList));
          writer.flush();
          break;
            
        //La commande CLEAR seulement dans la V2
        case RouletteV2Protocol.CMD_CLEAR:
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        
        //Messa
        default:
          writer.println("Huh? please use HELP if you don't know what commands are available.");
          writer.flush();
          break;
      }
      writer.flush();
    }
      
  }

}
