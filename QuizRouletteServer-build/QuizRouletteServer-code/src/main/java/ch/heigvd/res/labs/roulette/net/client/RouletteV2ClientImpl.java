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
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
    
    Socket clientSocket = super.getSocket();
    BufferedReader is = super.getBufferedReader();
    PrintWriter os = super.getPrintWriter();
    
    @Override
    public void clearDataStore() throws IOException {
        os.println(RouletteV2Protocol.CMD_CLEAR);
        os.flush();
        is.readLine();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        os.println(RouletteV2Protocol.CMD_CLEAR);
        os.flush();
        
        ListCommandResponse res = JsonObjectMapper.parseJson(is.readLine(), ListCommandResponse.class);
        return res.getStudents();
    }
    
    @Override
    public void disconnect() throws IOException {
        if (clientSocket.isConnected()) {
            os.println(RouletteV1Protocol.CMD_BYE);
            os.flush();
            is.readLine();

            clientSocket.close();
            is.close();
            os.close();
        } else {
            System.err.println("Can't disconnected (already disconnected)");
        }
    }

}
