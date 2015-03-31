package ch.heigvd.res.labs.roulette.net.client;

import static org.junit.Assert.*;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
    
    
}
