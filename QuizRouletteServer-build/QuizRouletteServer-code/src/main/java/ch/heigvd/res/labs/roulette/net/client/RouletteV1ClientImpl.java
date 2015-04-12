package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti, Valentin Schaad
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

   private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
   private Socket socket;
   protected BufferedReader reader;
   protected PrintWriter writer;

   @Override
   public void connect(String server, int port) throws IOException {
      socket = new Socket(server, port);

      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      //writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
      writer = new PrintWriter(socket.getOutputStream());
      
      reader.readLine();
   }

   @Override
   public void disconnect() throws IOException {
      if (!socket.isClosed()) {
         writer.println(RouletteV1Protocol.CMD_BYE);
         writer.flush();
         writer.close();
         reader.close();
         socket.close();
      }
   }

   @Override
   public boolean isConnected() {
      
      // isConnected et isClosed retournent true si le socket à déjà été connecté 
      //(respectivement déconnecté) auparavant. C-à-d que isConnected va retourner 
      // true même si le socket vient d'être fermé, c'est pourquoi il est nécessaire
      // de faire les deux vérifications. 
      // Remarque : la fonction connect va recréer un nouveau socket, donc pas de 
      // risque que isClosed retourne true alors qu'il vient d'être réouvert.
      return socket != null && socket.isConnected() && !socket.isClosed();
   }

   @Override
   public void loadStudent(String fullname) throws IOException {
      /*writer.println(RouletteV1Protocol.CMD_LOAD);
      writer.flush();

      reader.readLine();

      writer.println(fullname);
      writer.flush();
      writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      writer.flush();

      reader.readLine();*/
      
      ArrayList list = new ArrayList();
      list.add(new Student(fullname));
      
      loadStudents(list);
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException {
      writer.println(RouletteV1Protocol.CMD_LOAD);
      writer.flush();
      reader.readLine();
      for (Student s : students) {
         writer.println(s.getFullname());
         writer.flush();
      }
      writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      writer.flush();
      reader.readLine();
   }

   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {
      writer.println(RouletteV1Protocol.CMD_RANDOM);
      writer.flush();

      RandomCommandResponse randomResponse = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);

      if (randomResponse.getError() != null) {
         throw new EmptyStoreException();
      }

      return new Student(randomResponse.getFullname());
   }

   @Override
   public int getNumberOfStudents() throws IOException {
      writer.println(RouletteV1Protocol.CMD_INFO);
      writer.flush();

      InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

      return infoResponse.getNumberOfStudents();
   }

   @Override
   public String getProtocolVersion() throws IOException {
      writer.println(RouletteV1Protocol.CMD_INFO);
      writer.flush();
      
      InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
      
      return infoResponse.getProtocolVersion();
   }
}
