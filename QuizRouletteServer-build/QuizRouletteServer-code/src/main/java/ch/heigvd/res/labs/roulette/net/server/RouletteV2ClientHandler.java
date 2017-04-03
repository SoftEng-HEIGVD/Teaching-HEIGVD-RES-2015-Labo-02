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
 * @author silver kameni & nguefack zacharie
 */
public class RouletteV2ClientHandler implements IClientHandler {

    final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());
    private final IStudentsStore store;
    private int numberOfCommands; // variable use to count number of command executed

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
                    ++numberOfCommands;
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
                    ++numberOfCommands;
                    writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    break;
                    
                case RouletteV2Protocol.CMD_INFO:
                    ++numberOfCommands;
                    InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(response));
                    writer.flush();
                    break;
                    
                case RouletteV2Protocol.CMD_LIST:
                    ++numberOfCommands;
                    StudentsList studentsList = new StudentsList();
                    studentsList.setStudents(store.listStudents());
                    writer.println(JsonObjectMapper.toJson(studentsList));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_CLEAR:
                    ++numberOfCommands;
                    store.clear();
                    writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    writer.flush();
                    break;
                    
                case RouletteV2Protocol.CMD_LOAD:
                    ++numberOfCommands;
                    writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    writer.flush();
                    int NumberOfStudentsBefore = store.getNumberOfStudents();
                    store.importData(reader);
                    int NumberOfNewStudent = store.getNumberOfStudents() - NumberOfStudentsBefore;
                    LoadCommandResponse loadCommandResponse = new LoadCommandResponse("success", NumberOfNewStudent);
                    writer.println(JsonObjectMapper.toJson(loadCommandResponse));
                    writer.flush();
                    break;
                    
                case RouletteV2Protocol.CMD_BYE:
                    ++numberOfCommands;
                    ByeCommandResponse byeCommandResponse = new ByeCommandResponse("success", numberOfCommands);
                    writer.println(JsonObjectMapper.toJson(byeCommandResponse));
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

}
