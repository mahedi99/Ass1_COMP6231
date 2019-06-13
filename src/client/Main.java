package client;

import java.util.Scanner;

public class Main {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("Enter your id: ");
            String customerClientID = sc.next();
            if (customerClientID.substring(3, 4).contains("M")) {
                EventManagerClient emc1 = new EventManagerClient();
                emc1.eventManagerClientRequest(customerClientID);
            } else if (customerClientID.substring(3, 4).contains("C")) {
                CustomerClient cc1 = new CustomerClient();
                cc1.customerClientRequest(customerClientID);
            }
            System.out.println("\n");
        }
    }
}

