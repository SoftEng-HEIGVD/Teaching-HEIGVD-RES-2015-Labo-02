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
 * @author Camilo Pineda Serna
 * @author Antoine Nourazar
 */
public class RouletteV2ClientHandler implements IClientHandler {

    // from V1
    final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());
    private final IStudentsStore store;

    // V2 attributes
    private int numberOfCommands = 0; // for the BYE command response

    public RouletteV2ClientHandler(IStudentsStore store) {
        this.store = store;
    }


    @Override
    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
        // streams to communicate with the client
        BufferedReader fromClient = new BufferedReader(new InputStreamReader(is));
        PrintWriter toClient = new PrintWriter(new OutputStreamWriter(os));
        // welcome message
        toClient.println("Hello. Online HELP is available. Will you find it?");
        toClient.flush();

        String command;
        boolean done = false;
        while (!done && ((command = fromClient.readLine()) != null)) {
            // new command !!
            ++numberOfCommands;
            LOG.log(Level.INFO, "COMMAND: {0}", command);

            // switch through all the commands
            switch (command.toUpperCase()) {
                case RouletteV2Protocol.CMD_CLEAR: // new in V2
                {
                    // store must be cleared
                    store.clear();
                    // response must be sent
                    toClient.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    toClient.flush();
                    break;
                }
                case RouletteV2Protocol.CMD_LIST: // new in V2
                {
                    // a StudentList must be sent
                    StudentsList copyStudentsInStore = new StudentsList();
                    copyStudentsInStore.setStudents(store.listStudents());
                    toClient.println(JsonObjectMapper.toJson(copyStudentsInStore));
                    toClient.flush();
                    break;
                }
                case RouletteV2Protocol.CMD_RANDOM: // no changes from V1
                {
                    RandomCommandResponse rcResponse = new RandomCommandResponse();
                    try {
                        rcResponse.setFullname(store.pickRandomStudent().getFullname());
                    } catch (EmptyStoreException ex) {
                        rcResponse.setError("There is no student, you cannot pick a random one");
                    }
                    toClient.println(JsonObjectMapper.toJson(rcResponse));
                    toClient.flush();
                    break;
                }
                case RouletteV2Protocol.CMD_HELP: // no changes from V1
                {
                    toClient.println("Commands: " + Arrays.toString(RouletteV1Protocol.SUPPORTED_COMMANDS));
                    break;
                }
                case RouletteV2Protocol.CMD_INFO: // changes in V2
                {
                    InfoCommandResponse infoResponse = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    toClient.println(JsonObjectMapper.toJson(infoResponse));
                    toClient.flush();
                    break;
                }
                case RouletteV2Protocol.CMD_LOAD: // changes in V2
                {
                    // students loaded must be counted
                    toClient.println(RouletteV2Protocol.RESPONSE_LOAD_START); // same as V1
                    toClient.flush();
                    int oldNumberOfStudents = store.getNumberOfStudents();
                    // this is when students are added in the studentStore
                    store.importData(fromClient);
                    // counting
                    int numberOfStudentsAdded = store.getNumberOfStudents() - oldNumberOfStudents;

                    // now we prepare the response in the LoadCommandV2Response classe which will be serialised
                    LoadCommandV2Response lcv2r = new LoadCommandV2Response("success", numberOfStudentsAdded);
                    toClient.println(JsonObjectMapper.toJson(lcv2r));
                    toClient.flush();
                    break;
                }
                case RouletteV2Protocol.CMD_BYE: // changes in V2
                {
                    // an answer must be sent, we'll use the ByeCommandV2Response class
                    ByeCommandV2Response bcv2r = new ByeCommandV2Response("success", numberOfCommands);

                    toClient.println(JsonObjectMapper.toJson(bcv2r));
                    toClient.flush();
                    done = true;
                    break;
                }
                default: {
                    toClient.println("Huh? please use HELP if you don't know what commands are available.");
                    toClient.flush();
                    break;
                }
            }
            toClient.flush();
        }
    }

}
