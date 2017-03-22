package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
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
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti, Thibaud Duchoud, Mario Ferreira
 */
public class RouletteV2ClientHandler implements IClientHandler {

    final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());
    private final IStudentsStore store;
    
    public RouletteV2ClientHandler(IStudentsStore store) {
        this.store = store;
    }

    @Override
    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(os));

        printWriter.println("Hello. Online HELP is available. Will you find it?");
        printWriter.flush();

        String command;
        int nbCommands = 0;
        boolean done = false;
        while (!done && ((command = bufferedReader.readLine()) != null)) {
            LOG.log(Level.INFO, "COMMAND: {0}", command);
            nbCommands++;
            switch (command.toUpperCase()) {
                case RouletteV2Protocol.CMD_RANDOM:
                    RandomCommandResponse rcResponse = new RandomCommandResponse();
                    try {
                        rcResponse.setFullname(store.pickRandomStudent().getFullname());
                    } catch (EmptyStoreException ex) {
                        rcResponse.setError("There is no student, you cannot pick a random one");
                    }
                    printWriter.println(JsonObjectMapper.toJson(rcResponse));
                    printWriter.flush();
                    break;
                case RouletteV2Protocol.CMD_HELP:
                    printWriter.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    break;
                case RouletteV2Protocol.CMD_INFO:
                    InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    printWriter.println(JsonObjectMapper.toJson(response));
                    printWriter.flush();
                    break;
                case RouletteV2Protocol.CMD_LOAD:
                    printWriter.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    printWriter.flush();
                    
                    // On stocke le nombre d'étudiants avant et après importation
                    // (pour connaître le nombre de nouveaux étudiants)
                    int nbStudentsBeforeImport = store.getNumberOfStudents();
                    store.importData(bufferedReader);
                    int nbStudentsAfterImport = store.getNumberOfStudents();
                    
                    // On crée une LoadCommandResponse
                    LoadCommandResponse loadCommandResponse = new LoadCommandResponse("success", nbStudentsAfterImport - nbStudentsBeforeImport);
                    
                    // On la print en sérialisant (POJO -> String (JSON))
                    printWriter.println(JsonObjectMapper.toJson(loadCommandResponse));
                    printWriter.flush();
                    
                    printWriter.println(RouletteV2Protocol.RESPONSE_LOAD_DONE);
                    printWriter.flush();
                    break;
                case RouletteV2Protocol.CMD_BYE:
                    // On crée une ByeCommandResponse
                    ByeCommandResponse byeCommandResponse = new ByeCommandResponse("success", nbCommands);
                    
                    // On réinitialise le nombre de commande
                    nbCommands = 0;
                    
                    // On la print en sérialisant (POJO -> String (JSON))
                    printWriter.println(JsonObjectMapper.toJson(byeCommandResponse));
                    printWriter.flush();
                    
                    done = true;
                    break;
                case RouletteV2Protocol.CMD_CLEAR:
                    // On nettoie (vide) le store
                    store.clear();
                    
                    printWriter.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    printWriter.flush();
                    break;
                case RouletteV2Protocol.CMD_LIST:
                    // On print la liste d'étudiants (sérialisé (POJO -> String (JSON))
                    printWriter.println(JsonObjectMapper.toJson(store.listStudents()));
                    printWriter.flush();
                    break;
                default:
                    printWriter.println("Huh? please use HELP if you don't know what commands are available.");
                    printWriter.flush();
                    break;
            }
            printWriter.flush();
        }
    }

}
