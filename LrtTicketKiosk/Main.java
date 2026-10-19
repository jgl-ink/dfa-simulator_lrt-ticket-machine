package LrtTicketKiosk;

import java.util.Scanner;

// LRT Ticket Kiosk Simulator - a real-life DFA
// The kiosk is at Antipolo station.
//
// DFA parts:
//   States:          SELECT_DESTINATION, DUE_10 ... DUE_1, TICKET_ISSUED
//                    (each DUE_x state means "x pesos still due")
//   Alphabet:        destinations (Marikina, Santolan) and coins (P1, P5, P10)
//   Start state:     SELECT_DESTINATION
//   Accepting state: TICKET_ISSUED
//   Transitions:     choosing a destination -> DUE_fare
//                    inserting a coin in DUE_x -> DUE_(x - coin), or TICKET_ISSUED
//                    when the coin covers the amount due (see nextDue)
//
// NOTE: Fares are fictional demonstration fares, not official LRT fares.
public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== LRT TICKET KIOSK ===");
        System.out.println("(Demonstration fares only - not official LRT fares)");
        System.out.println("Kiosk at: Antipolo");
        System.out.println();
        System.out.println("1. Marikina (P5)");
        System.out.println("2. Santolan (P10)");
        System.out.println();

        // Start state: SELECT_DESTINATION
        String destination = "";
        while (destination.isEmpty()) {
            String choice = ask("Select destination: ");
            if (choice.equals("1")) {
                destination = "Marikina";
            } else if (choice.equals("2")) {
                destination = "Santolan";
            } else {
                System.out.println("Invalid choice. Enter 1 or 2.");
            }
        }

        int fare = 10;
        if (destination.equals("Marikina")) {
            fare = 5;
        }

        // Transition: SELECT_DESTINATION --destination--> DUE_fare
        System.out.println("[SELECT_DESTINATION] --" + destination + "--> " + state(fare));

        int due = fare;   // current state is DUE_due
        int paid = 0;     // total coins inserted (for the ticket summary only)
        int change = 0;

        // Keep reading coins until the accepting state is reached (due = 0)
        while (due > 0) {
            int coin = readCoin(ask("\nInsert coin (1, 5, or 10): "));
            if (coin == 0) {
                System.out.println("Invalid coin. Still " + state(due) + ".");
                continue;
            }

            int next = nextDue(due, coin);
            System.out.println(state(due) + " --P" + coin + "--> " + state(next));

            // A DFA only accepts or rejects. Giving change is an action the
            // kiosk performs once it reaches the accepting state TICKET_ISSUED.
            if (next == 0) {
                change = coin - due;
            }
            paid = paid + coin;
            due = next;
        }

        // Accepting state reached: print the ticket
        System.out.println();
        System.out.println("Ticket issued!");
        System.out.println("Route:       Antipolo -> " + destination);
        System.out.println("Fare:        P" + fare);
        System.out.println("Amount paid: P" + paid);
        System.out.println("Change:      P" + change);
    }

    // Transition function for coins: from DUE_due, reading a coin.
    // Returns the new amount due (0 means TICKET_ISSUED).
    static int nextDue(int due, int coin) {
        if (coin >= due) {
            return 0;
        }
        return due - coin;
    }

    // State name for an amount due, e.g. [DUE_5] or [TICKET_ISSUED].
    static String state(int due) {
        if (due == 0) {
            return "[TICKET_ISSUED]";
        }
        return "[DUE_" + due + "]";
    }

    // Turns "5", "P5" or "₱5" (peso sign) into 5.
    // Returns 0 if the input is not a P1, P5 or P10 coin.
    static int readCoin(String text) {
        if (text.startsWith("P") || text.startsWith("p") || text.startsWith("₱")) {
            text = text.substring(1);
        }
        if (text.equals("1") || text.equals("5") || text.equals("10")) {
            return Integer.parseInt(text);
        }
        return 0;
    }

    // Prints a prompt and reads one line.
    static String ask(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }
}
