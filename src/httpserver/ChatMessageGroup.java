/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author M1A2
 * 
 * This class manages the members and messages in a chatting group.
 */
public class ChatMessageGroup {
    // Define the maximum size of the chatting group
    private static final int MAXIMUM_SIZE = 5;
    
    private final List<String> members;
    private final List<List<String>> messages;
    
    public ChatMessageGroup() {
        this.members = new ArrayList<>();
        this.messages = new ArrayList<>();
    }
    
    public boolean isMember(String name) {
        return this.members.contains(name);
    }
    
    public int getNumOfMembers() {
        return this.members.size();
    }
    
    public List<String> getMembers() {
        return this.members;
    }
    
    public boolean addMember(String name) {
        if (this.members.size() < MAXIMUM_SIZE) {
            this.members.add(name);
            this.addMessage(
                "SYSTEM MESSAGE", 
                name + " joined, welcome!");
            return true;
        } else {
            return false;
        }
    }
    
    public void removeMember(String name) {
        this.members.remove(name);
        this.addMessage(
                "SYSTEM MESSAGE", 
                name + " left");
    }
    
    public int getSequenceNumber() {
        return this.messages.size() - 1;
    }
    
    public void addMessage(String name, String msg) {
        this.messages.add(Arrays.asList(name, msg));
    }
    
    public List<List<String>> getMessages(int seq) {
        if (seq <= this.messages.size() && seq >= 0) {
            return this.messages.subList(seq, this.messages.size());
        } else {
            // illegal sequence number
            return null;
        }
    }
    
}
