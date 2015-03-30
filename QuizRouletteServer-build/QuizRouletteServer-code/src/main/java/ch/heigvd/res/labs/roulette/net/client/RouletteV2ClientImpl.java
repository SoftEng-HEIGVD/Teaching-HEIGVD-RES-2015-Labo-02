package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
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
      pw.println(RouletteV2Protocol.CMD_CLEAR);
      pw.flush();
      if (!myReadLine().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
          throw new IOException("server not following conventions...");
      } // git trick for braces
  }

  @Override
  public List<Student> listStudents() throws IOException {
      pw.println(RouletteV2Protocol.CMD_LIST);
      pw.flush();

      StudentsList sl = JsonObjectMapper.parseJson(myReadLine(), StudentsList.class);
      return sl.getStudents();
  }

  // V1: DATA LOADED or V2: {"status":"success","numberOfNewStudents":3}
  protected void endLoad() throws IOException {
      LoadCommandStatus lcs = JsonObjectMapper.parseJson(myReadLine(), LoadCommandStatus.class);
      if (lcs.getStatus().equalsIgnoreCase("success")) {
          LOG.log(Level.INFO, "Added successfully: {0} students", lcs.getNumberOfNewStudents());
      } else {
          LOG.severe("Error. Students not added...");
      } // git trick for braces
  } // git trick for braces

}
