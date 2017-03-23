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
- * @author Olivier Liechti
 * @author Olivier Liechti, Michaël Berthouzoz, Thibault Schowing
  */
 public class RouletteV2ClientHandler implements IClientHandler {
 
    final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());
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
        int nbCommand = 0;

        while (!done && ((command = reader.readLine()) != null)) {
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
                    writer.flush();
                    break;
                case RouletteV1Protocol.CMD_HELP:
                    writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    writer.flush();
                    break;
                case RouletteV1Protocol.CMD_INFO:
                    InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(response));
                    break;
                case RouletteV1Protocol.CMD_LOAD:
                    writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
                    writer.flush();
                    int sizeBefore = store.listStudents().size();
                    store.importData(reader);
                    int sizeAfter = store.listStudents().size();
                    LoadCommandResponse lcs = new LoadCommandResponse();
                    lcs.setStatus("success");
                    lcs.setNumberNewStudents(sizeAfter - sizeBefore);
                    writer.println(JsonObjectMapper.toJson(lcs));
                    writer.flush();
                    break;
                case RouletteV1Protocol.CMD_BYE:
                    ByeCommandResponse bcr = new ByeCommandResponse();
                    bcr.setStatus("success");
                    bcr.setNumberOfCommands(nbCommand);
                    nbCommand = -1;
                    writer.println(JsonObjectMapper.toJson(bcr));
                    writer.flush();
                    done = true;
                    break;
                case RouletteV2Protocol.CMD_LIST:
                    StudentsList studentList = new StudentsList();
                    studentList.setStudents(store.listStudents());
                    writer.println(JsonObjectMapper.toJson(studentList));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_CLEAR:
                    store.clear();
                    writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    writer.flush();
                    break;
                default:
                    writer.println("Huh? please use HELP if you don't know what commands are available.");
                    writer.flush();
                    break;
            }
            nbCommand++;
            writer.flush();
        }
    }
 }