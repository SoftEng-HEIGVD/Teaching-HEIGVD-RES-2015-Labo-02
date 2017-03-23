package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
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

  @Override
  public void clearDataStore() throws IOException {
    printWrite.println(RouletteV2Protocol.CMD_CLEAR);
    printWrite.flush();
  }

  @Override
  public List<Student> listStudents() throws IOException {
    printWrite.println(RouletteV2Protocol.CMD_LIST);
    printWrite.flush();
    
    return JsonObjectMapper.parseJson(readLine(), StudentsList.class).getStudents();
  }
  
  @Override
  public void loadStudents(List<Student> students) throws IOException {
    printWrite.println(RouletteV1Protocol.CMD_LOAD);
    printWrite.flush();
    if(readLine().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START)) {
        for(Student s : students) {
            printWrite.println(s.getFullname());
        }
        
        printWrite.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        printWrite.flush();
        
        LoadCommandResponse lcs = JsonObjectMapper.parseJson(readLine(), LoadCommandResponse.class);
        if (lcs.getStatus().equalsIgnoreCase("success")) {
            LOG.log(Level.INFO, "Added successfully: {0} students", lcs.getNumberOfNewStudents());
        }
        else {
            LOG.severe("Error. Students not added...");
        }
    }
    else {
        throw new IOException("Unexpected response");
    }
  }
}
