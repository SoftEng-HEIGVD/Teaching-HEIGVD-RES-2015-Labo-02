
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Basile Chatillon
 * @author Nicolas Rod
 */
public class RouletteV2BasileChatillonTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
  
    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
    
    
}
