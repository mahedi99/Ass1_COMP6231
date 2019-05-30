package server.database;

import java.io.Serializable;

public class EventDetails implements Serializable {
    public String eventID;
    public int bookingCapacity;
    public int totalBooked;
    public int spaceAvailable(){
        return bookingCapacity - totalBooked;
    }
}
