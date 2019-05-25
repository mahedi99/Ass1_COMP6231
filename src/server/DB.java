package server;

public interface DB {
    public boolean addEvent();
    public boolean removeEvent();
    public boolean listEventAvailability();
    public boolean bookEvent(String clientID, String eventID);
    public boolean getBookingSchedule();
    public boolean cancelEvent();
}
