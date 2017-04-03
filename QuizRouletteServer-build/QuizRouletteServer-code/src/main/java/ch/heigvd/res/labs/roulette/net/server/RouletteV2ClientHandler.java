package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.IStudentsStore;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());
  private final IStudentsStore store;

  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store = store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    //bytes stream into string stream into buffered stream
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

    writer.println("Hello from client V2. Online HELP is available. Will you find it?");
    writer.flush();

    String command = "";
    boolean done = false;
    while (!done && ((command ? reader.readLine() != null))){
      LOG.log(Level.INFO,"COMMAND: {0}", command);
    }
  }

}
