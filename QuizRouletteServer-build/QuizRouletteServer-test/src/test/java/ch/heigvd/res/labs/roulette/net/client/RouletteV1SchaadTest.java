package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Valentin Schaad
 */
public class RouletteV1SchaadTest {
   
   @Rule
   private final int NB_STUDENT = 2;
   
   @Rule
   private IRouletteV1Client tabClient[] = new RouletteV1ClientImpl[NB_STUDENT];

   @Rule
   public ExpectedException exception = ExpectedException.none();

   @Rule
   public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV1Protocol.VERSION);

   @Test
   @TestAuthor(githubId = "schaad")
   public void aClientShouldBeConnected() throws IOException {
      assertTrue(roulettePair.getClient().isConnected());
   }
   
   @Test
   @TestAuthor(githubId = "schaad")
   public void twoClientMuseBeAbleToConnectSimultaneously() throws IOException {
      int port = roulettePair.getServer().getPort();
      
      for(int i = 0 ; i < NB_STUDENT ; ++i){
         tabClient[i] = new RouletteV1ClientImpl();
         tabClient[i].connect("localhost", port);
      }
      
      for(int i = 0 ; i < NB_STUDENT ; ++i){
         assertTrue(tabClient[i].isConnected());
      }
   }
   
   @Test
   @TestAuthor(githubId = "schaad")
   public void aStudentShouldStayInTheServerAfterClientDisconnect() throws IOException {
      int port = roulettePair.getServer().getPort();
      IRouletteV1Client client = new RouletteV1ClientImpl();
      client.connect("localhost", port);
      client.loadStudent("student test");
      client.disconnect();
      assertEquals(roulettePair.getClient().getNumberOfStudents(), 1);
   }
   
   @Test
   @TestAuthor(githubId = "schaad")
   public void theServerShouldBeAbleToLoadStudentsFromDifferentClient() throws IOException {
      int port = roulettePair.getServer().getPort();
      //IRouletteV1Client tabClient[] = new RouletteV1ClientImpl[NB_STUDENT];
      
      for(int i = 0 ; i < NB_STUDENT ; ++i){        
         tabClient[i].loadStudent("student " + i);
      }
      
      // On vérifie qu'il y aie NB_STUDENT + 1 étudiants chargés 
      // (le "+ 1" pour l'élève chargé dans aStudentShouldStayInTheServerAfterClientDisconnect)
      assertEquals(NB_STUDENT + 1, roulettePair.getClient().getNumberOfStudents());
   }
   
   @Test
   @TestAuthor(githubId = "schaad")
   public void itShouldBePossibleToPickAStudent() throws IOException {
      try{
         roulettePair.getClient().pickRandomStudent();
      } catch(EmptyStoreException e){
         assertTrue(false);
      }
      assertTrue(true);
   }
   
   @Test
   @TestAuthor(githubId = "schaad")
   public void twoClientShouldBeAbleToDisconnectSimultaneously() throws IOException {
      for(int i = 0 ; i < NB_STUDENT ; ++i){
         tabClient[i].disconnect();
      }
      
      for(int i = 0 ; i < NB_STUDENT ; ++i){
         assertFalse(tabClient[i].isConnected());
      }
   }
}