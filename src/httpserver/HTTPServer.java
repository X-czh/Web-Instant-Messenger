/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author M1A2
 * 
 * This class defines a HTTP server which opens a chat server,
 * listens to client requests, and opens new threads to process the requests.
 */
public class HTTPServer {
    public static final String WEB_ROOT = System.getProperty("user.dir")
            + File.separator + "webroot";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java HTTPServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;
        
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            // Start chat server
            ChatServer chatServer = new ChatServer();
            
            // Check client availability
            new AvailabilityCheck(chatServer).start();
            
            // Start listening
            while (listening) {
                Socket clientSocket = serverSocket.accept();
	        new HTTPServerThread(clientSocket, chatServer).start();
	    }
	} catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
    
}
