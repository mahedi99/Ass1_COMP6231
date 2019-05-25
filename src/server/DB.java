package server;

public interface DB {
    public boolean addEvent(String eventID, String eventType, int bookingCapacity);
    public boolean removeEvent(String eventID, String eventType);
    public boolean listEventAvailability(String eventType);
    public boolean bookEvent(String customerID, String eventID, String eventType);
    public boolean getBookingSchedule(String customerID);
    public boolean cancelEvent(String customerID, String eventID);
}
