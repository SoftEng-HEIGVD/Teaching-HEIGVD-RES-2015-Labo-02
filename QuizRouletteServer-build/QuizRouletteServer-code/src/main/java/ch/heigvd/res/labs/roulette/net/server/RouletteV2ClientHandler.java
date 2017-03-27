package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Lucas ELISEI (faku99)
 * @author David TRUAN  (Daxidz)
 */
public class RouletteV2ClientHandler implements IClientHandler {
    // Logger.
    final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());

    // Students store.
    private final IStudentsStore store;

    public RouletteV2ClientHandler(IStudentsStore store) {
        this.store = store;
    }

    @Override
    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

        // Welcome message
        writer.println("=== Welcome to the Roulette Server v2.0 ===");
        writer.flush();

        // Command entered by the client.
        String command;

        // Indicates if the client is done.
        boolean done = false;

        // Number of commands entered by the client.
        int commandsCount = 0;

        while(!done && ((command = reader.readLine()) != null)) {
            LOG.info("COMMAND: " + command);
            ++commandsCount;

            // Handle command sent by the client.
            switch(command.toUpperCase()) {

                // 'BYE' command.
                case RouletteV2Protocol.CMD_BYE:
                    done = true;
                    String status = done ? RouletteV2Protocol.STATUS_SUCCESS : RouletteV2Protocol.STATUS_FAILURE;
                    ByeCommandResponse response = new ByeCommandResponse(status, commandsCount);
                    writer.println(JsonObjectMapper.toJson(response));
                    writer.flush();
                    break;

                // 'HELP' command.
                case RouletteV2Protocol.CMD_HELP:
                    writer.println("Available commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    writer.flush();
                    break;

                // Unknown command.
                default:
                    writer.println("Unknown command. Use HELP to see list of available commands.");
                    writer.flush();
                    break;
            }
        }
    }

}
