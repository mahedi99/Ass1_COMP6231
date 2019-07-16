package client;

import java.util.Scanner;

public class Main {

	private static Scanner sc = new Scanner(System.in);

	// For RMI
	/*
	 * public static void main(String[] args) { while(true) {
	 * System.out.println("Enter your id: "); String customerClientID = sc.next();
	 * if (customerClientID.substring(3, 4).contains("M")) { EventManagerClient emc1
	 * = new EventManagerClient(); emc1.eventManagerClientRequest(customerClientID);
	 * } else if (customerClientID.substring(3, 4).contains("C")) { CustomerClient
	 * cc1 = new CustomerClient(); cc1.customerClientRequest(customerClientID); }
	 * System.out.println("\n"); } }
	 */

	// For Corba
	public static void main(String[] args) {
		try {
			while (true) {
				System.out.println("Enter your id: ");
				String customerClientID = sc.next();
				if (customerClientID.substring(3, 4).contains("M")) {
					EventManagerClientCORBA emc1 = new EventManagerClientCORBA();
					emc1.eventManagerClientRequest(customerClientID);
				} else if (customerClientID.substring(3, 4).contains("C")) {
					CustomerClientCORBA cc1 = new CustomerClientCORBA();
					cc1.customerClientRequest(customerClientID);
				}
				System.out.println("\n");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
