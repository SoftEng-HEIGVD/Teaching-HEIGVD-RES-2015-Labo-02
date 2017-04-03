package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author modified by Gallouche & dipietroa
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
    private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

    @Override
    public void clearDataStore() throws IOException {
        w.println(RouletteV2Protocol.CMD_CLEAR);
        w.flush();
        LOG.info(r.readLine());
    }

    @Override
    public List<Student> listStudents() throws IOException {
        if (this.isConnected()) {
            w.println(RouletteV2Protocol.CMD_LIST);
            w.flush();
            try {
                return JsonObjectMapper.parseJson(r.readLine(), StudentsList.class).getStudents();
            } catch (IOException ie) {
                throw new IOException("Error on getting students list");
            }
        } else {
            throw new IOException("Client not connected");
        }
    }

    @Override
    public void disconnect() throws IOException {
        if (isConnected()) {
            w.println(RouletteV2Protocol.CMD_BYE);
            w.flush();
            LOG.info(r.readLine());
        }
        try {
            w.close();
            r.close();
            socket.close();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        w = null;
        r = null;
        socket = null;
    }
}
