package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
    
    @Override
    public void clearDataStore() throws IOException {
        getPrintWriter().println(RouletteV2Protocol.CMD_CLEAR);
        getPrintWriter().flush();
        getBufferedReader().readLine();
    }
    
    @Override
    public List<Student> listStudents() throws IOException {
        getPrintWriter().println(RouletteV2Protocol.CMD_LIST);
        getPrintWriter().flush();
        
        ListCommandResponse res = JsonObjectMapper.parseJson(getBufferedReader().readLine(), ListCommandResponse.class);
        return res.getStudents();
    }
    
    @Override
    public void disconnect() throws IOException {
        if (getClientSocket().isConnected()) {
            getPrintWriter().println(RouletteV1Protocol.CMD_BYE);
            getPrintWriter().flush();
            getBufferedReader().readLine();
            
            getClientSocket().close();
            getBufferedReader().close();
            getPrintWriter().close();
        } else {
            System.err.println("Can't disconnected (already disconnected)");
        }
    }
    
}
