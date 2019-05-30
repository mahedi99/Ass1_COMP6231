package server.database;

public interface DB {
    public String addEvent(String eventID, EventType eventType, int bookingCapacity);
    public String removeEvent(String eventID, EventType eventType);
    public String listEventAvailability(EventType eventType);
    public String bookEvent(String customerID, String eventID, EventType eventType);
    public String getBookingSchedule(String customerID);
    public String cancelEvent(String customerID, String eventID);
}
