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
 * @author Olivier Liechti
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

        writer.println("Hello. Online HELP is available. Will you find it?");
        writer.flush();
        
        ByeCommandResponse byeCommandResponse = new ByeCommandResponse();
        byeCommandResponse.setStatus(RouletteV2Protocol.STAT_SUCCESS);
        
        LoadCommandResponse loadCommandResponse = new LoadCommandResponse();
        
        String command;
        boolean done = false;
        while (!done && ((command = reader.readLine()) != null)) {
            LOG.log(Level.INFO, "COMMAND: {0}", command);
            switch (command.toUpperCase()) {
                case RouletteV2Protocol.CMD_RANDOM:
                    byeCommandResponse.increasenumberOfCommands();
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
                    byeCommandResponse.increasenumberOfCommands();
                    writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_INFO:
                    byeCommandResponse.increasenumberOfCommands();                    
                    InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(response));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_LOAD:
                    byeCommandResponse.increasenumberOfCommands();                    
                    writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    writer.flush();
                    int tmpNbrOfStudent = store.getNumberOfStudents();
                    
                    try{
                        store.importData(reader);
                        loadCommandResponse.setStatus(RouletteV2Protocol.STAT_SUCCESS);
                    }catch(IOException e){
                        loadCommandResponse.setStatus(RouletteV2Protocol.STAT_BAD);
                    }
                    
                    loadCommandResponse.setnumberOfNewStudents(store.getNumberOfStudents() - tmpNbrOfStudent);
                    writer.println(JsonObjectMapper.toJson(loadCommandResponse));
                    writer.flush();
                    
                    break;
                case RouletteV2Protocol.CMD_BYE:
                    byeCommandResponse.increasenumberOfCommands();
                    done = true;
                    //Send the answer, according to V2 protocol
                    writer.println(JsonObjectMapper.toJson(byeCommandResponse));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_CLEAR:
                    byeCommandResponse.increasenumberOfCommands();
                    store.clear();
                    writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_LIST:
                    byeCommandResponse.increasenumberOfCommands();
                    writer.println(JsonObjectMapper.toJson(store.listStudents()));
                    writer.flush();
                    break;
                default:
                    byeCommandResponse.increasenumberOfCommands();
                    writer.println("Huh? please use HELP if you don't know what commands are available.");
                    writer.flush();
                    break;
            }
            writer.flush();
        }

    }
}
