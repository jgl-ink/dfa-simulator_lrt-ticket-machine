package DfaSimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// DFA Simulator
// Guides the user step by step to build a DFA, shows it as a table,
// then tests input strings on it.
//
// DFA parts (all entered by the user):
//   States:           Step 1  (states list)
//   Alphabet:         Step 2  (alphabet list, one character per symbol)
//   Start state:      Step 3  (start)
//   Accepting states: Step 4  (accepting list)
//   Transitions:      Step 5  (table: "state,symbol" -> next state)
public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== DFA SIMULATOR ===");

        // Step 1: States
        System.out.println("\nStep 1 of 5: States");
        ArrayList<String> states = new ArrayList<>();
        while (states.isEmpty()) {
            String line = ask("Enter state names separated by spaces (example: q0 q1 q2): ");
            for (String s : line.split("\\s+")) {
                if (!s.isEmpty() && !states.contains(s)) {
                    states.add(s);
                }
            }
        }
        System.out.println("States: " + states);

        // Step 2: Alphabet (each symbol is one character)
        System.out.println("\nStep 2 of 5: Alphabet");
        ArrayList<Character> alphabet = new ArrayList<>();
        while (alphabet.isEmpty()) {
            String line = ask("Enter input symbols, each ONE character, separated by spaces (example: 0 1): ");
            for (String s : line.split("\\s+")) {
                if (s.length() > 1) {
                    System.out.println("  '" + s + "' is not a single character. Try again.");
                    alphabet.clear();
                    break;
                }
                if (s.length() == 1 && !alphabet.contains(s.charAt(0))) {
                    alphabet.add(s.charAt(0));
                }
            }
        }
        System.out.println("Alphabet: " + alphabet);

        // Step 3: Start state
        System.out.println("\nStep 3 of 5: Start state");
        String start = askState("Choose the start state " + states + ": ", states);

        // Step 4: Accepting states
        System.out.println("\nStep 4 of 5: Accepting states");
        ArrayList<String> accepting = new ArrayList<>();
        while (true) {
            String line = ask("Choose accepting states separated by spaces " + states + ": ");
            accepting.clear();
            boolean valid = true;
            for (String s : line.split("\\s+")) {
                if (s.isEmpty()) {
                    continue;
                }
                if (!states.contains(s)) {
                    System.out.println("  '" + s + "' is not a state. Try again.");
                    valid = false;
                    break;
                }
                if (!accepting.contains(s)) {
                    accepting.add(s);
                }
            }
            // At least one accepting state is required
            if (valid && accepting.isEmpty()) {
                System.out.println("  Enter at least one accepting state.");
                valid = false;
            }
            if (valid) {
                break;
            }
        }

        // Step 5: Transitions - exactly one next state for every (state, symbol)
        System.out.println("\nStep 5 of 5: Transitions " + states);
        HashMap<String, String> table = new HashMap<>();
        int total = states.size() * alphabet.size();
        int count = 1;
        for (String state : states) {
            for (char symbol : alphabet) {
                String next = askState("(" + count + " of " + total + ") From " + state
                        + ", reading '" + symbol + "', go to: ", states);
                table.put(state + "," + symbol, next);
                count++;
            }
        }

        printTable(states, alphabet, start, accepting, table);

        // Test strings until the user is done
        while (true) {
            String input = ask("\nEnter input string (blank for empty string): ");
            run(input, alphabet, start, accepting, table);

            String again = ask("\nTest another string? (y/n): ");
            if (!again.equalsIgnoreCase("y") && !again.equalsIgnoreCase("yes")) {
                break;
            }
        }
    }

    // Shows the finished DFA as a transition table.
    // "->" marks the start state, "*" marks accepting states.
    static void printTable(ArrayList<String> states, ArrayList<Character> alphabet, String start,
                           ArrayList<String> accepting, HashMap<String, String> table) {
        System.out.println("\nYour DFA  (-> start, * accepting)");
        System.out.printf("%-8s", "");
        for (char symbol : alphabet) {
            System.out.printf("%-8s", symbol);
        }
        System.out.println();

        for (String state : states) {
            String label = state;
            if (accepting.contains(state)) {
                label = "*" + label;
            }
            if (state.equals(start)) {
                label = "->" + label;
            }
            System.out.printf("%-8s", label);
            for (char symbol : alphabet) {
                System.out.printf("%-8s", table.get(state + "," + symbol));
            }
            System.out.println();
        }

        // The DFA's formal definition (Q, Sigma, delta, q0, F)
        // Σ is the Greek letter Sigma, δ is the Greek letter delta.
        System.out.println("\nDFA 5-Tuple:");
        System.out.println("Q  = " + toSet(states.toString()));
        System.out.println("Σ  = " + toSet(alphabet.toString()));
        System.out.println("δ  = see table above");
        System.out.println("q0 = " + start);
        System.out.println("F  = " + toSet(accepting.toString()));
    }

    // Turns list text like "[q0, q1]" into set notation "{q0, q1}".
    static String toSet(String listText) {
        return "{" + listText.substring(1, listText.length() - 1) + "}";
    }

    // Processes the string one symbol at a time and prints each transition.
    static void run(String input, ArrayList<Character> alphabet, String start,
                    ArrayList<String> accepting, HashMap<String, String> table) {
        System.out.println();

        // Reject symbols outside the alphabet (positions counted from 1)
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!alphabet.contains(c)) {
                System.out.println("Position " + (i + 1) + ": '" + c + "' is not in the alphabet "
                        + alphabet + " -> REJECTED");
                return;
            }
        }

        String current = start;
        if (input.isEmpty()) {
            System.out.println("No symbols read, so the DFA stays in the start state " + start + ".");
        }

        // Read one symbol at a time and follow the transition table
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            String next = table.get(current + "," + c);
            String left = input.substring(i + 1);
            if (left.isEmpty()) {
                left = "none";
            }
            System.out.println("Step " + (i + 1) + ": " + current + " reads '" + c + "' -> "
                    + next + "   (left: " + left + ")");
            current = next;
        }

        // Accept only if the DFA ends in an accepting state
        if (accepting.contains(current)) {
            System.out.println("Ended in " + current + ", which is an accepting state -> ACCEPTED");
        } else {
            System.out.println("Ended in " + current + ", which is NOT an accepting state -> REJECTED");
        }
    }

    // Prints a prompt and reads one line.
    static String ask(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    // Keeps asking until the user types one of the existing states.
    static String askState(String prompt, ArrayList<String> states) {
        while (true) {
            String s = ask(prompt);
            if (states.contains(s)) {
                return s;
            }
            System.out.println("  '" + s + "' is not a state. Choose from " + states + ".");
        }
    }
}
