package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
        
    @Override
    public void clearDataStore() throws IOException {
        if(isConnected()){
            writer.println(RouletteV2Protocol.CMD_CLEAR);
            writer.flush();
            //read the answer
            reader.readLine();
        }else{
            throw new IOException("The client is not connected");
        }
    }

    @Override
    public List<Student> listStudents() throws IOException {
        if(isConnected()){
            writer.println(RouletteV2Protocol.CMD_LIST);
            writer.flush();
            //read the answer and parse it
            return Arrays.asList(JsonObjectMapper.parseJson(reader.readLine(), Student[].class));
        }else{
            throw new IOException("The client is not connected");
        }
    }


}
