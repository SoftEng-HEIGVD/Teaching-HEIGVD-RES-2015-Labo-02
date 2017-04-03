package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author silverkameni & nguefack zacharie
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    @Override
    public void clearDataStore() throws IOException{
        if(!connected) return;
        pw.println(RouletteV2Protocol.CMD_CLEAR);
        pw.flush();
        if (!bf.readLine().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE))
          LOG.log(Level.SEVERE, "not reply from server", RouletteV2Protocol.RESPONSE_CLEAR_DONE);
        
    }

    @Override
    public List<Student> listStudents() throws IOException {
        if(!connected) return null;
        pw.println(RouletteV2Protocol.CMD_LIST);
        pw.flush();
        return JsonObjectMapper.parseJson(bf.readLine(), StudentsList.class).getStudents(); 
    }
    
    
 
 
}
