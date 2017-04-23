package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandStatus;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandStatus;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
  private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(RouletteV2ClientImpl.class.getName());

  @Override
  public void clearDataStore() throws IOException {
      printWriter.println(RouletteV2Protocol.CMD_CLEAR);
      printWriter.flush();
      if (!myReadLine().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
          throw new IOException("server not following conventions...");
      } // git trick for braces
  }

  @Override
  public List<Student> listStudents() throws IOException {
      printWriter.println(RouletteV2Protocol.CMD_LIST);
      printWriter.flush();

      StudentsList sl = JsonObjectMapper.parseJson(myReadLine(), StudentsList.class);
      return sl.getStudents();
  }

  /**
   * Customized endLoad to handle V1/V2 dynamically.
   * V1 reads the line and return. 
   * V2 has a status code and number of added students printed in the logs.
   * 
   * // V1: DATA LOADED or V2: {"status":"success","numberOfNewStudents":3}
   * @throws IOException 
   */
  protected void endLoad() throws IOException {
      LoadCommandStatus lcs = JsonObjectMapper.parseJson(myReadLine(), LoadCommandStatus.class);
      if (lcs.getStatus().equalsIgnoreCase("success")) {
          LOG.log(Level.INFO, "Added successfully: {0} students", lcs.getNumberOfNewStudents());
      } else {
          LOG.severe("Error. Students not added...");
      } // git trick for braces
  } // git trick for braces

  /**
   * Customized byeByVersion to handle V1/V2 dynamically.
   * V1 sends the bye commands and returns. 
   * V2 sends the bye commands and waits for a reply: the number of commands used during the session.
   * 
   * // V1: noting or V2: {"status":"success","numberOfCommands":3}
   * @throws IOException 
   */
    private void byeByVersion () throws IOException {
      printWriter.println(RouletteV2Protocol.CMD_BYE);
      printWriter.flush();
      ByeCommandStatus bcs = JsonObjectMapper.parseJson(myReadLine(), ByeCommandStatus.class);
      if (bcs.getStatus().equalsIgnoreCase("success")) {
          LOG.log(Level.INFO, "End. Run successfully: {0} commands", bcs.getNumberOfCommands());
      } else {
          LOG.severe("Error. Unexpected behavior with BYE command... ");
      } // git trick for braces
  } // git trick for braces
}
