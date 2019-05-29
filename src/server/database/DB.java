package server.database;

public interface DB {
    public boolean addEvent(String eventID, EventType eventType, int bookingCapacity);
    public boolean removeEvent(String eventID, EventType eventType);
    public boolean listEventAvailability(EventType eventType);
    public boolean bookEvent(String customerID, String eventID, EventType eventType);
    public boolean getBookingSchedule(String customerID);
    public boolean cancelEvent(String customerID, String eventID);
}
