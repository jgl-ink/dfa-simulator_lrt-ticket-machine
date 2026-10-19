package LrtTicketKiosk;

import java.util.Scanner;

// LRT Ticket Kiosk Simulator - a real-life DFA
// The kiosk is at Antipolo station.
//
// DFA parts:
//   States:          Select Destination, Due P20 ... Due P1, Ticket Issued
//                    (each "Due Px" state means "x pesos still due")
//   Alphabet:        destinations (Marikina, Santolan, Anonas) and coins (P1, P5, P10, P20)
//   Start state:     Select Destination
//   Accepting state: Ticket Issued
//   Transitions:     choosing a destination -> Due P(fare)
//                    inserting a coin in Due Px -> Due P(x - coin), or Ticket Issued
//                    when the coin covers the amount due (see nextDue)
//
// NOTE: Fares are fictional demonstration fares, not official LRT fares.
public class Main {

    static Scanner sc = new Scanner(System.in);

    // Destinations and their fares (same position in both arrays).
    static String[] stations = {"Marikina", "Santolan", "Anonas"};
    static int[] fares = {5, 10, 20};

    static final String LINE = "======================================";

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.println("     LRT TICKET MACHINE SIMULATOR");
        System.out.println("         You are at: Antipolo");
        System.out.println(LINE);

        // Each ticket purchase is one complete run of the DFA:
        // it starts in Select Destination and ends in Ticket Issued.
        // Buying another ticket starts a NEW run from the start state,
        // just like testing another input string in a DFA simulator.
        while (true) {
            buyTicket();

            String again = ask("\nBuy another ticket? (y/n): ");
            if (!again.equalsIgnoreCase("y") && !again.equalsIgnoreCase("yes")) {
                break;
            }
        }
        System.out.println("\nThank you for riding the LRT!");
    }

    // One complete run of the DFA: from the start state to the accepting state.
    static void buyTicket() {
        System.out.println();

        // Start state: Select Destination
        System.out.println("Where are you going?");
        for (int i = 0; i < stations.length; i++) {
            System.out.printf("  [%d] %-10s P%d%n", i + 1, stations[i], fares[i]);
        }
        System.out.println();

        int choice = readDestination();
        String destination = stations[choice];
        int fare = fares[choice];

        // Transition: Select Destination --destination--> Due P(fare)
        System.out.println("  DFA: [Select Destination] --" + destination + "--> " + state(fare));
        System.out.println();
        System.out.println("Fare to " + destination + ": P" + fare);
        System.out.println("Accepted coins: P1, P5, P10, P20");

        int due = fare;   // current state is "Due P(due)"
        int paid = 0;     // total coins inserted (for the ticket only)
        int change = 0;

        // Keep reading coins until the accepting state is reached (due = 0)
        while (due > 0) {
            String input = ask("\nInsert coin: ");
            int coin = readCoin(input);
            if (coin == 0) {
                // Invalid coin: the DFA stays in the same state
                System.out.println("  '" + input + "' is not accepted. Use P1, P5, P10 or P20.");
                System.out.println("  DFA: stays in " + state(due));
                continue;
            }

            int next = nextDue(due, coin);
            System.out.println("  DFA: " + state(due) + " --P" + coin + "--> " + state(next));

            // A DFA only accepts or rejects. Giving change is an action the
            // kiosk performs once it reaches the accepting state Ticket Issued.
            if (next == 0) {
                change = coin - due;
                System.out.println("  Inserted P" + coin + ". Fully paid!");
            } else {
                System.out.println("  Inserted P" + coin + ". Remaining: P" + next);
            }
            paid = paid + coin;
            due = next;
        }

        // Accepting state reached: print the ticket
        System.out.println();
        System.out.println(LINE);
        System.out.println("              LRT TICKET");
        System.out.println("  From:    Antipolo");
        System.out.println("  To:      " + destination);
        System.out.println("  Fare:    P" + fare);
        System.out.println("  Paid:    P" + paid);
        System.out.println("  Change:  P" + change);
        System.out.println(LINE);
        System.out.println("Please take your ticket and change.");
    }

    // Transition function for coins: from Due P(due), reading a coin.
    // Returns the new amount due (0 means Ticket Issued).
    static int nextDue(int due, int coin) {
        if (coin >= due) {
            return 0;
        }
        return due - coin;
    }

    // Readable state name, e.g. [Due P5] or [Ticket Issued].
    static String state(int due) {
        if (due == 0) {
            return "[Ticket Issued]";
        }
        return "[Due P" + due + "]";
    }

    // Keeps asking until the user picks a listed destination. Returns its index.
    static int readDestination() {
        while (true) {
            String line = ask("Enter 1, 2 or 3: ");
            for (int i = 0; i < stations.length; i++) {
                if (line.equals(String.valueOf(i + 1))) {
                    return i;
                }
            }
            System.out.println("  Please enter 1, 2 or 3.");
        }
    }

    // Turns "5", "P5" or "₱5" (peso sign) into 5.
    // Returns 0 if the input is not a P1, P5, P10 or P20 coin.
    static int readCoin(String text) {
        if (text.startsWith("P") || text.startsWith("p") || text.startsWith("₱")) {
            text = text.substring(1);
        }
        if (text.equals("1") || text.equals("5") || text.equals("10") || text.equals("20")) {
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
