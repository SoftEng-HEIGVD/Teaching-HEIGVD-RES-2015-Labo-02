package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
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
 * @author Miguel Santamaria
 * @author Bastien Rouiller
 */
public class RouletteV2ClientHandler implements IClientHandler {

    private final IStudentsStore store;

    public RouletteV2ClientHandler(IStudentsStore store) {
        this.store = store;
    }

    @Override
    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

        //store information usefull for byecommand response
        ByeCommandResponse byeResponse = new ByeCommandResponse();

        //store the number of students before insertions
        int numberOfStudens;

        writer.println("Hello. Online HELP is available. Will you find it?");
        writer.flush();

        String command;

        boolean done = false;
        while (!done && ((command = reader.readLine()) != null)) {
            //increment number of commands
            byeResponse.incrementNbOfCommandsUsed();

            LOG.log(Level.INFO, "COMMAND: {0}", command);
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
                    break;
                case RouletteV2Protocol.CMD_INFO:
                    InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(response));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_LOAD:
                    //we compare the number of student before and after the insert in order to inform the user
                    numberOfStudens = store.getNumberOfStudents();
                    writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    writer.flush();
                    store.importData(reader);
                    LoadCommandResponse loadResponse = new LoadCommandResponse("success", store.getNumberOfStudents() - numberOfStudens);
                    writer.println(JsonObjectMapper.toJson(loadResponse));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_BYE:
                    writer.println(JsonObjectMapper.toJson(byeResponse));
                    writer.flush();
                    done = true;
                    break;
                case RouletteV2Protocol.CMD_CLEAR:
                    //clear and inform the user
                    store.clear();
                    writer.println("DATASTORE CLEARED");
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_LIST:
                    //serialize and send the lsit of students
                    writer.println(JsonObjectMapper.toJson(store.listStudents()));
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
