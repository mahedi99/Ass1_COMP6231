package server.database;

import java.io.Serializable;

public class MessageModel implements Serializable {
    private String clientID;
    private String eventID;
    private EventType eventType;
    private int bookingCapacity;
    private RequestType requestType;
    private String newEventID;
    private EventType newEventType;



    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public int getBookingCapacity() {
        return bookingCapacity;
    }

    public void setBookingCapacity(int bookingCapacity) {
        this.bookingCapacity = bookingCapacity;
    }
    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
    public String getNewEventID() {
        return newEventID;
    }
    public void setNewEventID(String newEventID) {
        this.newEventID = newEventID;
    }

    public EventType getNewEventType() {
        return newEventType;
    }

    public void setNewEventType(EventType newEventType) {
        this.newEventType = newEventType;
    }
}
