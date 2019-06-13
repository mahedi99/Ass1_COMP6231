package server.database;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class EventDetails implements Serializable {
    public String eventID;
    public int bookingCapacity;
    public int totalBooked;
    public Set<String> listCustomers = new HashSet<>();
    public int spaceAvailable(){
        return bookingCapacity - totalBooked;
    }
}
