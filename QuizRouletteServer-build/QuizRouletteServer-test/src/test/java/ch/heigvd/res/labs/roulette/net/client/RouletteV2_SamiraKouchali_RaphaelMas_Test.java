package ch.heigvd.res.labs.roulette.net.client;

import static org.junit.Assert.*;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 * @author Samira Kouchali
 * @author Raphael Mas
 */
public class RouletteV2_SamiraKouchali_RaphaelMas_Test {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    /**
     * @author: Samira Kouchali
     * @author: Raphael Mas
     *
     * Test personnel ajouté à la suite de ceux implémenté par M. Liechti.
     */
    
    @Test
    @TestAuthor(githubId = {"xxxkikixxx", "SamiraKouchali"})
    public void theServerShouldBeAbleToClearTheDatabase () throws IOException {
    
        // Ajoute 2 étudiants.
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("Samira Kouchali");
        client.loadStudent("Raphael Mas");
        
        // Supprime la base de donnée.
        client.clearDataStore();
        
        // Vérifie que la DB soit bel et bien vide.
        assertEquals(0, client.getNumberOfStudents());
    }
            
    @Test
    @TestAuthor(githubId = {"xxxkikixxx", "SamiraKouchali"})
    public void theServerShouldBeAbleToClearTheDatabaseWhenEmpty () throws IOException {
        
        // Supprime le contenu de la base de donnée
        ((IRouletteV2Client)roulettePair.getClient()).clearDataStore();
    }
    
    @Test
    @TestAuthor(githubId = {"xxxkikixxx", "SamiraKouchali"})
    public void theListOfStudentFromDbShouldBeCorrect () {
        
        // Ajout d'un nombre aléatoire d'étudiants dans la DB
        Random rn = new Random();
        final int NUMBER_OF_STUDENTS = (rn.nextInt() % 10) +1;
        
        // Création de la liste d'insertion
        LinkedList<Student> students = new LinkedList<>();
        
        String fullname = "Masha";
        boolean isPair = true;
        for (byte i = 0; i < NUMBER_OF_STUDENTS; i++) {
            if (isPair) {
                
            }
        }
    }
    
}
