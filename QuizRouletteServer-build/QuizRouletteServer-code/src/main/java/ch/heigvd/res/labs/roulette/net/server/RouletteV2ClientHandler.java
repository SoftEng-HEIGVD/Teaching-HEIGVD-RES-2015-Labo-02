package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti & ALi Miladi & Zeller Quentin
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());
  private final IStudentsStore store;

  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store = store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    //bytes stream into string stream into buffered stream
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));


    //The starting hello --> not specified in the specs
    writer.println("Hello from client V2. Online HELP is available. Will you find it?");
    writer.flush();

    String command = "";
    boolean done = false;
    int numberOfCommands = 0;
    while (!done && ((command = reader.readLine()) != null)){
      // log the events
      LOG.log(Level.INFO,"COMMAND: {0}", command);
      numberOfCommands++;//store the number of command of the current session
      //default --> handle bad command by decrementing

      switch (command.toUpperCase()) {
        case RouletteV2Protocol.CMD_CLEAR:
          //clear the store (remove students)
          store.clear();
          //send the response (client stored)
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          //be sure it send
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LIST:
          StudentsList list = new StudentsList();//object of student list
          list.setStudents(store.listStudents());//get the list of student in the object
          writer.println(JsonObjectMapper.toJson(list)); writer.flush();//print and serialize
          break;
        case RouletteV2Protocol.CMD_RANDOM: //same as v1, send a random student
          RandomCommandResponse rcResponse = new RandomCommandResponse();
          try {
            rcResponse.setFullname(store.pickRandomStudent().getFullname());//use the randomCommandResponse object to serialise
          } catch (EmptyStoreException ex) {
            rcResponse.setError("There is no student, you cannot pick a random one");
          }
          writer.println(JsonObjectMapper.toJson(rcResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_HELP:
          //write the supported command of the v2 protocol
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          writer.flush();
          break;
        case RouletteV1Protocol.CMD_INFO: //get the info fot the v2 protocol, the number of student and the protocol version
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          //serialize to json
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LOAD:
          //store the previous number of students
          int previousNumStudent = store.getNumberOfStudents();
          //Say user can start writing names
          writer.println(RouletteV2Protocol.RESPONSE_LOAD_START); writer.flush();

          try {
            store.importData(reader);
          } catch (IOException e){
            //Fail response not specified by protocol, assume fail and 0
            writer.println(JsonObjectMapper.toJson(new LoadV2CommandResponse("fail","0")));
            writer.flush();
          }
          int numberOfStudentAdded = store.getNumberOfStudents() - previousNumStudent;
          //create the object serializable
          LoadV2CommandResponse loadResp  = new LoadV2CommandResponse(RouletteV2Protocol.RESPONSE_SUCCESS_LOWERCASE
                  , numberOfStudentAdded+"");
          //send the serialized object in json form to client
          writer.println(JsonObjectMapper.toJson(loadResp));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:
          //the object to be parseed by json: assume sucessfull, and send the number of commands
          ByeV2CommandResponse byeResp = new ByeV2CommandResponse(RouletteV2Protocol.RESPONSE_SUCCESS_LOWERCASE,Integer.toString(numberOfCommands));
          //send in json form
          writer.println(JsonObjectMapper.toJson(byeResp));writer.flush();
          done = true;
          break;
        default:
          //a bad command is not a command
          numberOfCommands--;
          writer.println("Huh? please use HELP if you don't know what commands are available. (V2)");
          writer.flush();
          break;
      }
    }
  }

}
