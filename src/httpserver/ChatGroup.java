/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author M1A2
 * 
 * This class manages the chatting groups in the system.
 */
public class ChatGroup {
    // Define each member's chatting status code
    private static final int S_ALONE = 0, S_TALKING = 1;
    
    private final Map<String, Integer> members;
    private final Map<Integer, ChatMessageGroup> chatGroups;
    private int groupEver;

    public ChatGroup() {
    	this.members = new LinkedHashMap<>();
    	this.chatGroups = new HashMap<>();
    	this.groupEver = 0;
    }

    public void join(String name) {
    	this.members.put(name, S_ALONE);
    }
    
    public boolean isMember(String name) {
        return this.members.containsKey(name);
    }
    
    public void leave(String name) {
    	this.disconnect(name);
    	this.members.remove(name);
    }
    
    public int findGroup(String name) {
        int groupKey = -1;
        
        for (Integer key: this.chatGroups.keySet()) {
            if (this.chatGroups.get(key).isMember(name)) {
                groupKey = key;
                break;
            }
        }
        
        return groupKey;
    }

    public boolean connect(String name, String peer) {
        int groupKey = this.findGroup(peer);

        if (groupKey > 0) {
            // if peer is in a group, join it
            if (this.chatGroups.get(groupKey).addMember(name)) {
                this.members.put(name, S_TALKING); 
            } else {
                // group upper limit reached, connection fails
                return false;
            }
        } else {
            // otherwise, create a new group
            this.groupEver += 1;
            groupKey = this.groupEver;
            this.chatGroups.put(groupKey, new ChatMessageGroup());
            this.chatGroups.get(groupKey).addMember(name);
            this.chatGroups.get(groupKey).addMember(peer);
            this.members.put(name, S_TALKING);
            this.members.put(peer, S_TALKING);
        }
        return true; // successfully connected
    }

    public void disconnect(String name) {
        int groupKey = this.findGroup(name);
        
        if (groupKey > 0) {
            // find myself in the group, quit
            ChatMessageGroup chatMessageGroup = this.chatGroups.get(groupKey);
            
            chatMessageGroup.removeMember(name);
            this.members.put(name, S_ALONE);
            if (chatMessageGroup.getNumOfMembers() == 1) {
                // peer may be the only one left as well...
                String peer = chatMessageGroup.getFirstMember();
                this.members.put(peer, S_ALONE);
                this.chatGroups.remove(groupKey);
            }
        }
    }
    
    public Map<String, Integer> getMembers() {
        return this.members;
    }
    
    public Map<Integer, ChatMessageGroup> getChatGroups() {
        return this.chatGroups;
    }
    
}
