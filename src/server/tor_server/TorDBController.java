package server.tor_server;

import server.database.DB;
import server.LogUtils;
import server.Utils;
import server.database.DB;
import server.database.EventDetails;
import server.database.EventType;
import server.database.RequestType;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class TorDBController implements DB {
	private static Map<EventType, ConcurrentHashMap<String, EventDetails>> database = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, Integer> eventInOtherCitiesMap = new ConcurrentHashMap<>();

	private static TorDBController instance;

	private TorDBController() {

	}

	public static TorDBController getInstance() {
		if (instance == null) {
			instance = new TorDBController();
		}
		return instance;
	}

	@Override
	public synchronized String addEvent(String eventID, EventType eventType, int bookingCapacity) {
		String response = "false";
		switch (eventID.substring(0, 3)) {
		case "MTL":
			String UDPMsg2 = RequestType.ADD_EVENT + "|" + eventID + "|" + eventType + "|" + bookingCapacity;
			response = TorServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg2);
			break;
		case "OTW":
			String UDPMsg = RequestType.ADD_EVENT + "|" + eventID + "|" + eventType + "|" + bookingCapacity;
			response = TorServer.sendMsg(Utils.OTW_SERVER_PORT, UDPMsg);
			break;
		case "TOR":
			EventDetails eventDetails = new EventDetails();
			eventDetails.bookingCapacity = bookingCapacity;
			eventDetails.eventID = eventID;

			if (!database.containsKey(eventType)) {
				database.put(eventType, new ConcurrentHashMap<>());
			}
			if (!database.get(eventType).containsKey(eventID)) {
				database.get(eventType).putIfAbsent(eventID, eventDetails);
				response = "Event " + eventID + " Added!";
			} else {
				EventDetails details = database.get(eventType).get(eventID);
				details.bookingCapacity = bookingCapacity;
				database.get(eventType).put(eventID, eventDetails);
				response = "Event already exists, capacity updated!";
			}
			break;
		}
		LogUtils.writeToFile("tor_server.txt", RequestType.ADD_EVENT + " | " + "Event ID : " + eventID + " | "
				+ "Booking Capacity : " + bookingCapacity + "\nResponse : " + response);
		return response;
	}

	@Override
	public synchronized String removeEvent(String eventID, EventType eventType) {
		String response = "false";
		switch (eventID.substring(0, 3)) {
		case "TOR":
			if (database.containsKey(eventType)) {
				if (database.get(eventType).containsKey(eventID)) {
					database.get(eventType).remove(eventID);
					response = "successfully deleted!";
				}
			}
			break;
		}
		LogUtils.writeToFile("tor_server.txt",
				RequestType.REMOVE_EVENT + " | " + "Event ID : " + eventID + "\nResponse : " + response);
		return response;
	}

	@Override
	public synchronized String listEventAvailability(EventType eventType) {
		String response = "false";

		ExecutorService service = Executors.newFixedThreadPool(3);
		Future<String> mtl = service.submit(new CalculateEvent(Utils.MTL_SERVER_PORT, eventType));
		Future<String> otw = service.submit(new CalculateEvent(Utils.OTW_SERVER_PORT, eventType));
		Future<String> tor = service.submit(() -> {
			String tmpResponse = "";
			if (database.containsKey(eventType)) {
				ConcurrentHashMap<String, EventDetails> allEvents = database.get(eventType);
				Set<String> keys = allEvents.keySet();
				for (String tmpKey : keys) {
					EventDetails tmpEvent = allEvents.get(tmpKey);
					tmpResponse = tmpResponse + tmpEvent.eventID + " " + tmpEvent.spaceAvailable() + "|";
				}
			}
			return tmpResponse;
		});

		service.shutdown();

		try {
			service.awaitTermination(5, TimeUnit.SECONDS); // waits at-most 5 seconds
			response = mtl.get() + otw.get() + tor.get();
			response = response.replace("|", ", ").trim();
			response = response.substring(0, response.length() - 1) + ".";
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtils.writeToFile("tor_server.txt", RequestType.LIST_EVENT_AVAILABILITY + "\nResponse : " + response);
		return response;
	}

	public String listEventAvailabilityForOthers(EventType eventType) {
		String response = "";
		if (database.containsKey(eventType)) {
			ConcurrentHashMap<String, EventDetails> allEvents = database.get(eventType);
			Set<String> keys = allEvents.keySet();
			for (String tmpKey : keys) {
				EventDetails tmpEvent = allEvents.get(tmpKey);
				response = response + tmpEvent.eventID + " " + tmpEvent.spaceAvailable() + "|";
			}
		}
		return response;
	}

	@Override
	public synchronized String bookEvent(String customerID, String eventID, EventType eventType) {
		String response = "unsuccessful";
		if (customerID.substring(3, 5).contains("C")) {

			switch (eventID.substring(0, 3)) {
			case "MTL":
				if (getTotalEventsOutside(customerID, eventID) < 3) {
					// String tmpKey = customerID + eventID.substring(6, eventID.length()).trim();
					// //tmpKey represents the month+year : 0519
					// if (!eventInOtherCitiesMap.containsKey(tmpKey) ||
					// (eventInOtherCitiesMap.containsKey(tmpKey) &&
					// (eventInOtherCitiesMap.get(tmpKey) < 3))){
					String UDPMsg = RequestType.BOOK_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
					response = TorServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg);
					// if (response.contains("added")){
					//
					// if (eventInOtherCitiesMap.containsKey(tmpKey)){
					// int i = eventInOtherCitiesMap.get(tmpKey);
					// eventInOtherCitiesMap.put(tmpKey, i+1);
					// }
					// else{
					// eventInOtherCitiesMap.put(tmpKey, 1);
					// }
					// }
				} else {
					response = "You can book at-most 3 events from other cities!";
				}
				break;
			case "OTW":
				if (getTotalEventsOutside(customerID, eventID) < 3) {
					// String tmpKey2 = customerID + eventID.substring(6, eventID.length()).trim();
					// //tmpKey represents the month+year : 0519
					// if (!eventInOtherCitiesMap.containsKey(tmpKey2) ||
					// (eventInOtherCitiesMap.containsKey(tmpKey2) &&
					// (eventInOtherCitiesMap.get(tmpKey2) < 3))){
					String UDPMsg = RequestType.BOOK_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
					response = TorServer.sendMsg(Utils.OTW_SERVER_PORT, UDPMsg);
					// if (response.contains("added")){
					//
					// if (eventInOtherCitiesMap.containsKey(tmpKey2)){
					// int i = eventInOtherCitiesMap.get(tmpKey2);
					// eventInOtherCitiesMap.put(tmpKey2, i+1);
					// }
					// else{
					// eventInOtherCitiesMap.put(tmpKey2, 1);
					// }
					// }
				} else {
					response = "You can book at-most 3 events from other cities!";
				}
				break;
			case "TOR":
				if (database.containsKey(eventType)) {
					if (database.get(eventType).containsKey(eventID)) {
						EventDetails event = database.get(eventType).get(eventID);
						if (0 < event.spaceAvailable()) {
							if (event.listCustomers.add(customerID)) {
								event.totalBooked++;
								response = customerID + " added to " + eventID + " event.";
							} else {
								response = "Duplicate call for" + eventID + " event";
							}
						} else {
							response = "Capacity full for " + eventID + " event.";
						}
					}
				}

				break;
			}
		}
		LogUtils.writeToFile("tor_server.txt", RequestType.BOOK_EVENT + " | " + "Event ID : " + eventID + " | "
				+ "Customer ID : " + customerID + "\nResponse : " + response);
		return response;
	}

	public String getEventsForOneMonth(String customerID, String month) {
		int countEvent = 0;
		Set<EventType> keys = database.keySet();
		for (EventType tmpEventType : keys) {
			Set<String> eventKeys = database.get(tmpEventType).keySet();
			for (String tmpEventKey : eventKeys) {
				EventDetails tmpEvent = database.get(tmpEventType).get(tmpEventKey);
				String tmpMonth = tmpEvent.eventID.substring(6, tmpEvent.eventID.length()).trim();
				if (tmpEvent.listCustomers.contains(customerID) && tmpMonth.equals(month)) {
					countEvent++;
				}
			}
		}
		return "" + countEvent;
	}

	private int getTotalEventsOutside(String customerID, String eventID) {
		String month = eventID.substring(6, eventID.length()).trim();
		int totalEventsOutside;
		String UDPMsg1 = RequestType.GET_TOTAL_EVENTS + "|" + customerID + "|" + month;
		String numberOfEvents = TorServer.sendMsg(Utils.OTW_SERVER_PORT, UDPMsg1);
		totalEventsOutside = Integer.parseInt(numberOfEvents);
		if (totalEventsOutside < 3) {
			UDPMsg1 = RequestType.GET_TOTAL_EVENTS + "|" + customerID + "|" + month;
			String numberOfEvents2 = TorServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg1);
			totalEventsOutside += Integer.parseInt(numberOfEvents2);
		}
		return totalEventsOutside;
	}

	@Override
	public synchronized String getBookingSchedule(String customerID) {
		String response;
		String UDPMsg = RequestType.GET_BOOKING_SCHEDULE + "|" + customerID;
		response = TorServer.sendMsg(Utils.OTW_SERVER_PORT, UDPMsg);
		response = response + TorServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg);

		Set<EventType> keys = database.keySet();
		for (EventType tmpEventTypeKey : keys) {
			Set<String> eventKeys = database.get(tmpEventTypeKey).keySet();
			for (String tmpEventKey : eventKeys) {
				EventDetails tmpEvent = database.get(tmpEventTypeKey).get(tmpEventKey);
				if (tmpEvent.listCustomers.contains(customerID)) {
					response = response + tmpEvent.eventID + "|";
				}
			}
		}
		LogUtils.writeToFile("tor_server.txt",
				RequestType.GET_BOOKING_SCHEDULE + " | " + "Customer ID : " + customerID + "\nResponse : " + response);
		return "All events  : " + response;
	}

	public String getBookingScheduleForOthers(String customerID) {
		String response = "";
		Set<EventType> keys = database.keySet();
		for (EventType tmpEventTypeKey : keys) {
			Set<String> eventKeys = database.get(tmpEventTypeKey).keySet();
			for (String tmpEventKey : eventKeys) {
				EventDetails tmpEvent = database.get(tmpEventTypeKey).get(tmpEventKey);
				if (tmpEvent.listCustomers.contains(customerID)) {
					response = response + tmpEvent.eventID + "|";
				}
			}
		}
		// System.out.println("response : " +response);
		return response;
	}

	@Override
	public String cancelEvent(String customerID, String eventID, EventType eventType) {
		String response = "false";
		switch (eventID.substring(0, 3)) {
		case "MTL":
			String UDPMsg = RequestType.CANCEL_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
			response = TorServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg);
			break;
		case "OTW":
			String UDPMsg2 = RequestType.CANCEL_EVENT + "|" + customerID + "|" + eventID + "|" + eventType;
			response = TorServer.sendMsg(Utils.OTW_SERVER_PORT, UDPMsg2);
			break;
		case "TOR":
			if (database.containsKey(eventType)) {
				ConcurrentHashMap<String, EventDetails> allEvents = database.get(eventType);
				Set<String> keys = allEvents.keySet();
				for (String tmpKey : keys) {
					EventDetails tmpEvent = allEvents.get(tmpKey);
					if (eventID.equals(tmpEvent.eventID)) {
						allEvents.get(eventID).totalBooked--;
						response = "" + allEvents.get(eventID).listCustomers.remove(customerID);
						response = (response.contains("true") ? "successfully removed!" : "unsuccessful");
						break;
					}
				}
			}

			break;
		}
		LogUtils.writeToFile("tor_server.txt", RequestType.CANCEL_EVENT + " | " + "Event ID : " + eventID + " | "
				+ "Customer ID : " + customerID + "\nResponse : " + response);
		return response;
	}

	class CalculateEvent implements Callable<String> {

		private final int portNum;
		private final EventType eventType;

		CalculateEvent(int portNum, EventType eventType) {
			this.portNum = portNum;
			this.eventType = eventType;
		}

		@Override
		public String call() {
			String UDPMsg = RequestType.LIST_EVENT_AVAILABILITY + "|" + eventType;
			return TorServer.sendMsg(portNum, UDPMsg);
		}
	}

	@Override
    public String swapEvent(String customerID, String oldEventID, EventType oldEventType, String newEventID, EventType newEventType) {
        String response = "false";
        String isOldEventBooked = "false";
        String isNewEventAvailable = "false";

        ExecutorService service = Executors.newFixedThreadPool(5);

        //check if oldEvent was booked before
        Future<String> mtlOld = null, mtlNew = null, otwOld = null, otwNew = null, torOld = null, torNew = null;
        switch (oldEventID.substring(0, 3)) {
            case "MTL":
            	mtlOld = service.submit(new MakeSwapRequest(Utils.MTL_SERVER_PORT, customerID, oldEventID, oldEventType, RequestType.CHECK_IS_REGISTERED));
                break;
            case "OTW":
                otwOld = service.submit(new MakeSwapRequest(Utils.OTW_SERVER_PORT, customerID, oldEventID, oldEventType, RequestType.CHECK_IS_REGISTERED));
                break;
            case "TOR":
                torOld = service.submit(() -> {
                    String tmpResult = "false";
                    if (database.containsKey(oldEventType)) {
                        ConcurrentHashMap<String, EventDetails> allEvents = database.get(oldEventType);
                        if (allEvents.containsKey(oldEventID)){
                            EventDetails tmpEvent = allEvents.get(oldEventID);
                            if (tmpEvent.listCustomers.contains(customerID)){
                                tmpResult = "true";
                            }
                        }
                    }
                    return tmpResult;
                });
                break;
        }

        switch (newEventID.substring(0, 3)) {
            case "MTL":
            	mtlNew = service.submit(new MakeSwapRequest(Utils.MTL_SERVER_PORT, customerID, newEventID, newEventType, RequestType.CHECK_CAPACITY));
                break;
            case "OTW":
                otwNew = service.submit(new MakeSwapRequest(Utils.OTW_SERVER_PORT, customerID, newEventID, newEventType, RequestType.CHECK_CAPACITY));
                break;
            case "TOR":
                torNew = service.submit(() -> {
                    String tmpResult = "false";
                    if (database.containsKey(newEventType)) {
                        ConcurrentHashMap<String, EventDetails> allEvents = database.get(newEventType);
                        if (allEvents.containsKey(newEventID)){
                            EventDetails tmpEvent = allEvents.get(newEventID);
                            
                                if (tmpEvent.spaceAvailable() > 0){
                                    tmpResult = "true";
                                }
                            
                        }
                    }
                    return tmpResult;
                });
                break;
        }

        service.shutdown();
        try {
            service.awaitTermination(5, TimeUnit.SECONDS); //waits at-most 5 seconds
            switch (oldEventID.substring(0, 3)){
                case "MTL" :
                    isOldEventBooked  = mtlOld.get();
                    break;
                case "OTW" :
                    isOldEventBooked = otwOld.get();
                    break;
                case "TOR" :
                    isOldEventBooked = torOld.get();
                    break;
            }
            switch (newEventID.substring(0, 3)){
                case "MTL" :
                    isNewEventAvailable  = mtlNew.get();
                    break;
                case "OTW" :
                    isNewEventAvailable = otwNew.get();
                    break;
                case "TOR" :
                    isNewEventAvailable = torNew.get();
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isOldEventBooked.contains("true") && isNewEventAvailable.contains("true")){
//            String month = newEventID.substring(6, newEventID.length()).trim();
//            int totalEventsOutside;
//            String UDPMsg1 = RequestType.GET_TOTAL_EVENTS + "|" + customerID + "|" + month;
//            String numberOfEvents = TorServer.sendMsg(Utils.MTL_SERVER_PORT, UDPMsg1);
//            totalEventsOutside = Integer.parseInt(numberOfEvents);
//            if (totalEventsOutside < 3){
//                UDPMsg1 = RequestType.GET_TOTAL_EVENTS + "|" + customerID + "|" + month;
//                String numberOfEvents2 = TorServer.sendMsg(Utils.OTW_SERVER_PORT, UDPMsg1);
//                totalEventsOutside += Integer.parseInt(numberOfEvents2);
//            }
//
//            if (totalEventsOutside < 3){
//                //cancel event and book event
//                service = Executors.newFixedThreadPool(2);
//            }

            String bookResponse = bookEvent(customerID, newEventID, newEventType);
            if (bookResponse.contains("added")){
                String cancelResponse = cancelEvent(customerID, oldEventID, oldEventType);
                if (cancelResponse.contains("successfully")){
                    response = "Swap Successful!";
                }
            }
        }
        else {
            response = "Either no capacity or event was not booked!";
        }

        return response;
    }

	public String getIsTheUserRegistered(String customerID, String eventID, EventType eventType){
        String response = "false";
        if (database.containsKey(eventType)) {
            ConcurrentHashMap<String, EventDetails> allEvents = database.get(eventType);
            if (allEvents.containsKey(eventID)){
                EventDetails tmpEvent = allEvents.get(eventID);
                if (tmpEvent.listCustomers.contains(customerID)){
                    response = "true";
                }
            }
        }
        return response;
    }
    public String checkCapacityForTheEvent(String customerID, String eventID, EventType eventType) {
    	String tmpResult = "false";
        if (database.containsKey(eventType)) {
            ConcurrentHashMap<String, EventDetails> allEvents = database.get(eventType);
            if (allEvents.containsKey(eventID)){
                EventDetails tmpEvent = allEvents.get(eventID);
                    if (tmpEvent.spaceAvailable() > 0){
                        tmpResult = "true";
                    }
                
            }
        }
        return tmpResult;
    }

    class MakeSwapRequest implements Callable<String> {

        private EventType eventType;
        private String eventID;
        private String customerID;
        private int portNumber;
        private RequestType reqType;

        public MakeSwapRequest(int portNumber, String customerID, String eventID, EventType eventType, RequestType reqType){
            this.portNumber = portNumber;
            this.customerID = customerID;
            this.eventID = eventID;
            this.eventType = eventType;
            this.reqType = reqType;
        }
        @Override
        public String call(){
            String UDPMsg = reqType + "|" + customerID + "|" + eventID + "|" + eventType;
            return TorServer.sendMsg(portNumber, UDPMsg);
        }
    }
}
