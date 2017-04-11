/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author M1A2
 * 
 * This class defines a way to check client availability at intervals.
 */
public class AvailabilityCheck extends Thread{
    private final ChatServer chatServer;

    public AvailabilityCheck(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    @Override
    public void run() {
        long timeInterval = 2000;

        // Check client availability every 2 s
        try {
            while (true) {
                this.chatServer.checkAvailability();
                Thread.sleep(timeInterval);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(AvailabilityCheck.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
