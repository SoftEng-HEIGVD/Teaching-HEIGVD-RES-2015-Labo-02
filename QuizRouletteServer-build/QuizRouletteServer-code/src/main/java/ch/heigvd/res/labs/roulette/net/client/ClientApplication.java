/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.client;

import java.io.IOException;

/**
 *
 * @author Guillaume
 */
public class ClientApplication {
    public static void main() throws IOException {
        RouletteV1ClientImpl client = new RouletteV1ClientImpl();
        System.out.println(client.getProtocolVersion());
    }
}
