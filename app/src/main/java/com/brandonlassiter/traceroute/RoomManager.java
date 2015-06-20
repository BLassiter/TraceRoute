package com.brandonlassiter.traceroute;

import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by brandon on 6/19/15.
 */
public class RoomManager {

    private static RoomManager sharedInstance;
    private ArrayList<ParseObject> rooms;
    private ParseObject currentRoom;

    public static RoomManager getInstance() {

        if(sharedInstance == null) {
            sharedInstance = new RoomManager();
        }

        return sharedInstance;

    }

    public void setCurrentRoom(ParseObject currentRoom) {
        this.currentRoom = currentRoom;
    }

    public ParseObject getCurrentRoom() {
        return currentRoom;
    }

    public void setRooms(ArrayList<ParseObject> rooms) {
        this.rooms = rooms;
    }

    public ArrayList<ParseObject> getRooms() {
        return rooms;
    }
}
