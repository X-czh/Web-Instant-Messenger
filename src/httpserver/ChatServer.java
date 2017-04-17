/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author M1A2
 * 
 * This class defines a chat server serving for the chatting service.
 */
public class ChatServer {
    // Define request code
    private static final int M_LOGIN = 0;
    private static final int M_UPDATE = 1;
    private static final int M_CONNECT = 2;
    private static final int M_EXCHANGE = 3;
    private static final int M_DISCONNET = 4;
    private static final int M_LOGOUT = 5;
    
    // Define status code
    private static final String M_SUCC = "1";
    private static final String M_FAIL = "0";
    
    // Define initial time-to-live measured in 2 seconds
    private static final int INITIAL_TIME_TO_LIVE = 5;
    
    // Define each member's chatting status code
    private static final int S_ALONE = 0, S_TALKING = 1;
    
    private final ChatGroup group;
    private final Map<String, Integer> users;
    
    public ChatServer() {
        this.group = new ChatGroup();
        this.users = new HashMap<>();
    }
    
    public void msgHandler(Socket socket, HTTPRequest request, HTTPResponse response) {
        String requestBody = request.getRequestBody();
        String userName = request.getUserName();
        int code = (int)(requestBody.charAt(0) - '0');
        
        try {
            if (this.users.containsKey(userName) || code == M_LOGIN) {
                // valid userName or LOGIN
                switch (code) {
                    case M_LOGIN:
                        response.sendAjaxResponse(this.login(userName));
                        break;
                    case M_UPDATE:
                        int seq = Integer.parseInt(requestBody.substring(1, requestBody.length()));
                        response.sendAjaxResponse(this.update(userName, seq));
                        extendTimeToLive(userName);
                        break;
                    case M_CONNECT:
                        String peer = requestBody.substring(1, requestBody.length());
                        response.sendAjaxResponse(this.connect(userName, peer));
                        extendTimeToLive(userName);
                        break;
                    case M_EXCHANGE:
                        String msg = requestBody.substring(1, requestBody.length());
                        response.sendAjaxResponse(this.exchange(userName, msg));
                        extendTimeToLive(userName);
                        break;
                    case M_DISCONNET:
                        response.sendAjaxResponse(this.disconnect(userName));
                        extendTimeToLive(userName);
                        break;
                    case M_LOGOUT:
                        response.sendAjaxResponse(this.logout(userName));
                        break;
                    default:
                        System.out.println("Unsupported request code!");
                        break;
                }
            } else {
                // Invalid userName
                response.sendAjaxResponse(M_FAIL); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String login(String name) {
        if (this.group.isMember(name)) {
            // duplicate name, try another name
            return M_FAIL;
        } else {
            // name available, login
           this.group.join(name);
           this.users.put(name, INITIAL_TIME_TO_LIVE);
           return this.update(name, -1);
        }
    }
    
    private String update(String name, int seq) {
        String memberList, msgList;
        ChatMessageGroup chatMessageGroup;
        int groupKey = this.group.findGroup(name);
        int chattingStatus = this.group.getChattingStatus(name);

        memberList = (this.group.getMembers().isEmpty())
                ? "" : new JSONObject(this.group.getMembers()).toString();
        if (seq >= 0 && chattingStatus == S_TALKING) {
            // the user has joined a converstation and received the initial seq
            chatMessageGroup = this.group.getChatGroups().get(groupKey);
            msgList = (chatMessageGroup.getMessages(seq).isEmpty())
                    ? "" : new JSONArray(chatMessageGroup.getMessages(seq)).toString();
        } else {
            // the user hasn't joined any converstation
            msgList = "";
        }
        System.out.println(msgList);
        return M_SUCC + chattingStatus + memberList + "|" + msgList;
    }
    
    private String connect(String name, String peer) {
        if (this.group.connect(name, peer)) {
            int groupKey, seq;
            ChatMessageGroup chatMessageGroup;
            String memberList;

            groupKey = this.group.findGroup(name);
            chatMessageGroup = this.group.getChatGroups().get(groupKey);
            seq = chatMessageGroup.getSequenceNumber();
            memberList = (chatMessageGroup.getMembers()).toString();

            return M_SUCC + seq + "|" + memberList;
        } else {
            return M_FAIL;
        }
    }
    
    private String exchange(String name, String msg) {
        int groupKey = this.group.findGroup(name);
        ChatMessageGroup chatMessageGroup = this.group.getChatGroups().get(groupKey);
        
        chatMessageGroup.addMessage(name, msg);
        
        return M_SUCC;
    }
    
    private String disconnect(String name) {
        this.group.disconnect(name);
        return M_SUCC;
    }
    
    private String logout(String name) {
        this.group.leave(name);
        this.users.remove(name);
        return M_SUCC;
    }
    
    private void extendTimeToLive(String name) {
        this.users.put(name, INITIAL_TIME_TO_LIVE);
    }
    
    public void checkAvailability() {
        // used to avoid java.util.ConcurrentModificationException
        List<String> usersToRemove = new ArrayList<>();
        
        this.users.keySet().forEach((key) -> {
            int timeToLive = this.users.get(key);
            if (--timeToLive <= 0) {
                // at least 8s no connection, remove client
                usersToRemove.add(key);
            } else {
                this.users.put(key, timeToLive);
            }
        });
        
        usersToRemove.forEach((key) -> {
            this.logout(key);
        });
    }

}
