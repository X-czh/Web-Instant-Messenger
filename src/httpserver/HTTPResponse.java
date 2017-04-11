/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author M1A2
 * 
 * This class encapsulate the HTTP response message
 * and provides methods to generate it.
 */
public class HTTPResponse {
    private static final int BUFFER_SIZE = 1024;
    private final OutputStream output;
    private HTTPRequest request;

    public HTTPResponse(OutputStream output) {
        this.output = output;
    }

    public void setRequest(HTTPRequest request) {
        this.request = request;
    }

    public void sendAjaxResponse(String responseString) throws IOException {
        String HTTPResponseMsg = "HTTP/1.1 200 OK\r\n"
            + "Content-Type: application/json\r\n"
            + "Content-Length: " + responseString.length() + "\r\n" + "\r\n"
            + responseString;
        try {
            this.output.write(HTTPResponseMsg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendStaticResource() throws IOException {
        FileInputStream fis = null;
        byte[] bytes = new byte[BUFFER_SIZE];
        
        try {
            File file = new File(HTTPServer.WEB_ROOT, this.request.getUri());
            if (file.exists() && file.isFile()) {
                String header = "HTTP/1.1 200 OK\r\n"
                        + "Content-Length: " + file.length() + "\r\n" + "\r\n";
                this.output.write(header.getBytes());
                fis = new FileInputStream(file);
                int readSize = fis.read(bytes, 0, BUFFER_SIZE);
                while (readSize != -1) {
                    this.output.write(bytes, 0, readSize); 
                    readSize = fis.read(bytes, 0, BUFFER_SIZE);
                }
            } else {
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n"
                        + "Content-Type: text/html\r\n"
                        + "Content-Length: 23\r\n" + "\r\n"
                        + "<h1>File Not Found</h1>";
                this.output.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            if (fis != null)
                fis.close();
        }
    }
    
}
