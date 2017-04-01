package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.RouletteV1ClientHandler;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * modified by abass mahdavi
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());

    @Override
    public void clearDataStore() throws IOException {
        writer.println(RouletteV2Protocol.CMD_CLEAR);
        writer.flush();
        if (reader != null) {
            reader.readLine();

        } else {
            throw new IOException();
        }
    }

    @Override
    public List<Student> listStudents() throws IOException {
        StudentsList result;
        String response;
        writer.println(RouletteV2Protocol.CMD_LIST);
        writer.flush();
        if (reader != null) {
            response = reader.readLine();
            try {
                result = JsonObjectMapper.parseJson(response, StudentsList.class);
                return result.getStudents();
            } catch (IOException e) {
                throw new IOException();
            }
        } else {
            throw new IOException();
        }
    }
    
    
    public String getByeMessage()throws IOException{        
        writer.println(RouletteV2Protocol.CMD_BYE);
        writer.flush();
        if (reader != null) {
            return reader.readLine();
            
        } else {
            throw new IOException();
        }       
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        super.loadStudent(fullname);
        if (reader != null) {
            reader.readLine();
        }
    }
    
    @Override
    public String getProtocolVersion() throws IOException {        
        return RouletteV2Protocol.VERSION;
    }
    
    
}
