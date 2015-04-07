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
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti, Sébastien Henneberger
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

   private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

   private Socket socket;
   private BufferedReader reader;
   protected PrintWriter writer;

   protected String readLine() throws IOException {
      String line = null;

      do {
         line = reader.readLine();
      } while (line == null);

      return line;
   }

   @Override
   public void connect(String server, int port) throws IOException {
      // To change body of generated methods, choose Tools | Templates.
      //throw new UnsupportedOperationException("Not supported yet.");

      socket = new Socket(server, port);
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

      System.out.println(readLine());

   }

   @Override
   public void disconnect() throws IOException {
      // To change body of generated methods, choose Tools | Templates.
      //throw new UnsupportedOperationException("Not supported yet.");

      writer.println(RouletteV1Protocol.CMD_BYE);
      writer.flush();
      writer.close();
      reader.close();
      socket.close();
   }

   @Override
   public boolean isConnected() {
      // To change body of generated methods, choose Tools | Templates.
      //throw new UnsupportedOperationException("Not supported yet.");

      if (socket != null) {
         return socket.isConnected() && !socket.isClosed();
      }

      // Si on arrive ici, il n'est forcément pas connecté
      return false;
   }

   @Override
   public void loadStudent(String fullname) throws IOException {
      // To change body of generated methods, choose Tools | Templates.
      //throw new UnsupportedOperationException("Not supported yet.");

      writer.println(RouletteV1Protocol.CMD_LOAD);
      writer.flush();
      writer.println(fullname);
      writer.flush();
      writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      writer.flush();

      System.out.println(readLine());

   }

   @Override
   public void loadStudents(List<Student> students) throws IOException {
      // To change body of generated methods, choose Tools | Templates.
      //throw new UnsupportedOperationException("Not supported yet.");

      // On parcourt la liste d'étudiant fournie
      for (int i = 0; i < students.size(); ++i) {
         // On utilise la méthode loadStudent en lui passant le prénom de l'étudiant         
         loadStudent(students.get(i).getFullname());

         /* Remarque : On aurait aussi très bien pu faire l'inverse,
          * c.-à-d de coder la méthode loadStudents et on aurait créé une liste d'un
          * seul étudiant dans la méthode loadStudent et on aurait appelé loadStudents
          * en lui passant cette liste. Mais cela aurait été un poil plus couteux du
          * à la création de la liste supplémentaire.
          */
      }
   }

   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {
      // To change body of generated methods, choose Tools | Templates.
      //throw new UnsupportedOperationException("Not supported yet.");

      writer.println(RouletteV1Protocol.CMD_RANDOM);
      writer.flush();

      RandomCommandResponse rcr = JsonObjectMapper.parseJson(readLine(), RandomCommandResponse.class);
      if (rcr.getError() != null) {
         throw new EmptyStoreException();
      }

      return new Student(rcr.getFullname());
   }

   @Override
   public int getNumberOfStudents() throws IOException {
      // To change body of generated methods, choose Tools | Templates.
      //throw new UnsupportedOperationException("Not supported yet.");

      writer.println(RouletteV1Protocol.CMD_INFO);
      writer.flush();

      InfoCommandResponse icr = JsonObjectMapper.parseJson(readLine(), InfoCommandResponse.class);
      return icr.getNumberOfStudents();
   }

   @Override
   public String getProtocolVersion() throws IOException {
      //To change body of generated methods, choose Tools | Templates.
      //throw new UnsupportedOperationException("Not supported yet.");

      writer.println(RouletteV1Protocol.CMD_INFO);
      writer.flush();

      InfoCommandResponse icr = JsonObjectMapper.parseJson(readLine(), InfoCommandResponse.class);
      return icr.getProtocolVersion();
   }
}