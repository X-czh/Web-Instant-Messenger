/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author M1A2
 * 
 * This class defines a thread of the HTTP server serving a single client.
 */
public class HTTPServerThread extends Thread{
    private final Socket socket;
    private final ChatServer chatServer;

    public HTTPServerThread(Socket socket, ChatServer chatServer) {
        super("HTTPServerThread");
        this.socket = socket;
        this.chatServer = chatServer;
    }
    
    @Override
    public void run() {
        try (
            InputStream input = this.socket.getInputStream();
            OutputStream output = this.socket.getOutputStream();
        ) {
            // Encapsulate request into HTTPRequest object
            HTTPRequest request = new HTTPRequest(input);
            request.parse();
            
            // Encapsulate response into HTTPResponse object
            HTTPResponse response = new HTTPResponse(output);
            response.setRequest(request);
            
            // Handle request
            if (request.getUri().startsWith("/chat.do/")) {
                this.chatServer.msgHandler(this.socket, request, response);
            } else {
                response.sendStaticResource();
            }
            
            // Close socket
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
