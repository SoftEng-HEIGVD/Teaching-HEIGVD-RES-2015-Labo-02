/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.RouletteServer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.rules.ExternalResource;

/**
 *
 * @author amh
 */
public class EphemeralClientServerV2Pair extends ExternalResource {

  RouletteServer server;
  IRouletteV2Client client;
  String protocolVersion;

  public EphemeralClientServerV2Pair(String protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  @Override
  protected void before() throws Throwable {
    server = new RouletteServer(protocolVersion);
    server.startServer();
    if (RouletteV2Protocol.VERSION.equals(protocolVersion)) {
      client = new RouletteV2ClientImpl();
    } 
    client.connect("localhost", server.getPort());
  }

  @Override
  protected void after() {
    try {
      client.disconnect();
    } catch (IOException ex) {
      Logger.getLogger(EphemeralClientServerPair.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
      server.stopServer();
    } catch (IOException ex) {
      Logger.getLogger(EphemeralClientServerPair.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public RouletteServer getServer() {
    return server;
  }

  public IRouletteV1Client getClient() {
    return client;
  }
    
}
