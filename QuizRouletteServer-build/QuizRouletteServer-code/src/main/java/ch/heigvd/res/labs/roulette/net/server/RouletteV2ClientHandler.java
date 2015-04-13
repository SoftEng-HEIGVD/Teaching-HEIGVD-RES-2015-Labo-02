package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
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
 * @author Olivier Liechti
 */
public class RouletteV2ClientHandler implements IClientHandler {

	private final IStudentsStore store;
	private final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());
	private short numberOfCommand = 0;

	public RouletteV2ClientHandler(IStudentsStore s) {
		store = s;
	}

	@Override
	public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));

		pw.println("Hello. Online HELP is available. Will you find it?");
		pw.flush();

		String command = "";
		boolean done = false;
		while(done || ((command = br.readLine()) == null)) {
			++numberOfCommand;
			LOG.log(Level.INFO, "COMMAND: {0}", command);

			command = command.toUpperCase();
			if(command.equals(RouletteV1Protocol.CMD_RANDOM) {
				RandomCommandResponse rcr = new RandomCommandResponse();
				try {
					rcr.setFullname(store.pickRandomStudent().getFullname());
				} catch(EmptyStoreException ese) {
					rcr.setError("There is no student, you cannot pick a random one");
				}
				pw.println(JsonObjectMapper.toJson(rcr));
				pw.flush();
			} else if(command.equals(RouletteV1Protocol.CMD_INFO)) {
				InfoCommandResponse icr = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
				pw.println(JsonObjectMapper.toJson(icr));
				pw.flush();
			} else if(command.equals(RouletteV1Protocol.CMD_LOAD)) {
				int numberOfStudents = store.getNumberOfStudents();
				pw.println(RouletteV1Protocol.RESPONSE_LOAD_START);
				pw.flush();
				store.importData(br);
				pw.println("{\"status\":\"success\",\"numberOfNewStudents\":" + (store.getNumberOfStudents() - numberOfStudents) + "}");
				pw.flush();
			} else if(command.equals(RouletteV1Protocol.CMD_BYE)) {
				pw.write("{\"status\":\"success\",\"numberOfCommands\":" + numberOfCommand + "}");
				pw.flush();
				done = true;
				numberOfCommand = 0;
			} else if(command.equals(RouletteV2Protocol.CMD_LIST)) {
				StudentsList list = new StudentsList();
				list.setStudents(store.listStudents());
				pw.println(JsonObjectMapper.toJson(list));
				pw.flush();
			} else if(command.equals(RouletteV2Protocol.CMD_CLEAR)) {
				store.clear();
				pw.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
				pw.flush();
			} else if(command.equals(RouletteV1Protocol.CMD_HELP)) {
				pw.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
			} else {
				// No commands mached
				pw.println("Huh? please use HELP if you don't know what commands are available.");
				pw.flush();
			}
		}
		pw.flush();
	}

}
