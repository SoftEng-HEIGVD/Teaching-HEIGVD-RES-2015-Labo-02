package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.ByeResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.ListResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadResponse;
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
 * @author Guillaume Milani
 */
public class RouletteV2ClientHandler implements IClientHandler {

    private final IStudentsStore store;
    private int nbCommands = 0;

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
                case RouletteV2Protocol.CMD_CLEAR:
                    nbCommands++;

                    store.clear();

                    writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_LIST:
                    nbCommands++;
                    ListResponse listResponse = new ListResponse(store.listStudents());
                    writer.println(JsonObjectMapper.toJson(listResponse));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_RANDOM:
                    nbCommands++;
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
                    nbCommands++;
                    writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    break;

                case RouletteV2Protocol.CMD_INFO:
                    nbCommands++;
                    InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(response));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_LOAD:
                    nbCommands++;
                    int numberOfNewStudents = store.getNumberOfStudents();
                    writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    writer.flush();
                    LoadResponse loadResponse;
                    try {
                        store.importData(reader);
                        numberOfNewStudents = store.getNumberOfStudents() - numberOfNewStudents;
                        loadResponse = new LoadResponse("success", numberOfNewStudents);
                    } catch (IOException e) {
                        loadResponse = new LoadResponse("failure", 0);
                    }
                    writer.println(JsonObjectMapper.toJson(loadResponse));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_BYE:
                    nbCommands++;
                    ByeResponse byeResponse = new ByeResponse("success", nbCommands);
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

}
