package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.imageio.IIOException;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
    
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(RouletteV2ClientImpl.class.getName());
  
    
    
  @Override
  public void clearDataStore() throws IOException {
      writer.println(RouletteV2Protocol.CMD_CLEAR);
      writer.flush();

      if(!lineReader().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE)){
          throw new IOException("server response not correct....");
      }
      
  }

  @Override
  public List<Student> listStudents() throws IOException {
      writer.println(RouletteV2Protocol.CMD_LIST);
      writer.flush();
      String line = lineReader();
      StudentsList s = JsonObjectMapper.parseJson(line, StudentsList.class);
      return s.getStudents();
  }
  

//    protected void endLoad() throws IOException {
//      LoadCommandResponse lcs = JsonObjectMapper.parseJson(lineReader(), LoadCommandResponse.class);
//      if (lcs.getStatus().equalsIgnoreCase("success")) {
//          LOG.log(Level.INFO, "Added successfully: {0} students", lcs.getNbStudents());
//      } else {
//          LOG.severe("Error. Students not added...");
//      } 
//  }
  
}

