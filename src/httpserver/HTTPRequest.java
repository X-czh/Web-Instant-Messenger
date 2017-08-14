/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author M1A2
 * 
 * This class encapsulate the HTTP request message
 * and provides methods to parse it.
 */
public class HTTPRequest {
    private final InputStream input;
    private String uri;
    private String requestBody;
    private String userName;

    public HTTPRequest(InputStream input) {
        this.input = input;
    }

    public void parse() {
        int bufferSize = 2048;
        int readSize;
        byte[] buffer = new byte[bufferSize];
        StringBuilder request = new StringBuilder(bufferSize);
        String requestString;

        // Construct requestString from byte stream
        try {
            readSize = this.input.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            readSize = -1;
        }
        for (int i = 0; i < readSize; i++) {
            request.append((char)buffer[i]);
        }
        requestString = request.toString();
        System.out.print(requestString); // print out requestString
        
        // Parse URI
        this.uri = this.parseUri(requestString);
        if (this.uri.equals("/")) {
            this.uri = "/index.html"; // automatically redirect to /index.html
        }
        
        // Parse RequestBody
        this.requestBody = this.parseRequestBody(requestString);
        
        // Parse UserName
        if (this.requestBody != null) {
            this.userName = this.parseUserName(this.uri);
        }
    }

    private String parseUri(String requestString) {
        int index1, index2;

        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1) {
                return requestString.substring(index1 + 1, index2);
            }
        }
        return null;
    }
    
    private String parseRequestBody(String requestString) { 
        int index = requestString.indexOf("\r\n\r\n");
        
        if (index != -1) {
            return requestString.substring(index + 4, requestString.length());
        }
        return null;
    }
    
    private String parseUserName(String uri) { 
        int index = uri.indexOf('/', 1);
        
        if (index != -1) {
            return uri.substring(index + 1, uri.length());
        }
        return null;
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public String getRequestBody() {
        return this.requestBody;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
}
