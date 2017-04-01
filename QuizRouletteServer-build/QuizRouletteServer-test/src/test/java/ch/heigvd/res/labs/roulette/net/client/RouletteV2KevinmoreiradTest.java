package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.io.IOException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Kevin Moreira
 */
public class RouletteV2KevinmoreiradTest
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


    @Test
    @TestAuthor(githubId = "kevinmoreirad")
    public void serverHasToCountStudents() throws IOException
    {
        IRouletteV2Client clt = new RouletteV2ClientImpl();

        clt.loadStudent("student1");
        clt.loadStudent("student2");
        clt.loadStudent("sutdent3");
        clt.loadStudent("student4");
        assertEquals(4, clt.getNumberOfStudents());

    }


    @Test
    @TestAuthor(githubId = "kevinmoreirad")
    public void serverHasToClearCorrectly() throws IOException
    {
        IRouletteV2Client clt = new RouletteV2ClientImpl();

        clt.loadStudent("student1");
        clt.loadStudent("student2");
        clt.loadStudent("sutdent3");
        clt.loadStudent("student4");

        clt.clearDataStore();
        assertEqals(0, clt.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "kevinmoreirad")
    public void serverHasToGiveGoodVersion() throws IOException, IOException
    {

        IRouletteV2Client clt = new RouletteV2ClientImpl();

        clt.connect("localhost", roulettePair.getServer().getPort());

        assertEquals(clt.getProtocolVersion(), RouletteV2Protocol.VERSION);

        clt.disconnect();
    }
}
