package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientHandler implements IClientHandler {

    final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());

    private final IStudentsStore store;

    public RouletteV2ClientHandler(IStudentsStore store) {
        this.store = store;
    }

    @Override
    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

        writer.println("Hello. version 2 of server.");
        writer.flush();

        int commandsCount = 0;

        String command;
        boolean done = false;
        while (!done && ((command = reader.readLine()) != null)) {
            commandsCount++;
            LOG.log(Level.INFO, "COMMAND: {0}", command);
            switch (command.toUpperCase()) {
                case RouletteV1Protocol.CMD_RANDOM:
                    RandomCommandResponse rcResponse = new RandomCommandResponse();
                    try {
                        rcResponse.setFullname(store.pickRandomStudent().getFullname());
                    } catch (EmptyStoreException ex) {
                        rcResponse.setError("There is no student, you cannot pick a random one");
                    }
                    writer.println(JsonObjectMapper.toJson(rcResponse));
                    break;
                case RouletteV2Protocol.CMD_CLEAR:
                    store.clear();
                    writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    break;
                case RouletteV2Protocol.CMD_LIST:
                    StudentListResponse listResponse = new StudentListResponse(store.listStudents());
                    writer.println(JsonObjectMapper.toJson(listResponse));
                    break;
                case RouletteV2Protocol.CMD_LOAD:
                    writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    writer.flush();
                    int old = store.getNumberOfStudents();
                    String status = "success";
                    try {
                        store.importData(reader);
                    } catch (IOException e) {
                        status = "failure";
                    }
                    LoadCommandResponse loadResponse = new LoadCommandResponse(status, (store.getNumberOfStudents() - old));
                    writer.println(JsonObjectMapper.toJson(loadResponse));
                    break;
                case RouletteV2Protocol.CMD_INFO:
                    InfoCommandResponse infoResponse = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(infoResponse));
                    break;
                case RouletteV2Protocol.CMD_HELP:
                    writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    break;
                case RouletteV2Protocol.CMD_BYE:
                    ByeCommandResponse byeResponse = new ByeCommandResponse("success", commandsCount);
                    writer.println(JsonObjectMapper.toJson(byeResponse));
                    done = true;
                    break;
                default:
                    writer.println("Huh? please use HELP if you don't know what commands are available.");
                    break;
            }
            writer.flush();
        }
    }

}
