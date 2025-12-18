/**
 * title: PoDServer.java
 * description: A simple TCP Poem of the Day server that sends poems to a client based on the client's choice.
 * date: July 8, 2025
 * author: Dylan Phillips
 * copyright: 2025 Dylan Phillips
 */

/**
 * I declare that this assignment is my own work and that all material previously written or published
 * in any source by any other person has been duly acknowledged in the assignment.
 * I have not submitted this work, or a significant part thereof, previously as part of any academic program.
 * In submitting this assignment I give permission to copy it for assessment purposes only.
 *
 * <H1>Poem of the Day Server</H1>
 *
 * <H3>Purpose and Description</H3>
 *
 * <P>
 * This program implements a simple TCP server that sends a poem to a client using a custom "Poem of the Day" protocol.
 * Clients connect using telnet, see a list of available poems, select one by number,
 * and receive the poem or an error message if the input is invalid.
 * </P>
 *
 * <P>
 * The server reads all poems from a text file once at startup and uses a loop to accept new client connections.
 * The server uses blocking sockets and processes one client at a time per thread.
 * </P>
 *
 * <H3>Classes</H3>
 * 
 * <UL>
 *   <LI><b>PoDServer</b>: Main server class that loads poems, listens for client connections, and handles requests.</LI>
 * </UL>
 *
 * <H3>Key Methods</H3>
 *
 * <UL>
 *   <LI><b>public static void main(String[] args)</b> - Entry point that checks arguments and starts the server.</LI>
 *   <LI><b>public void startServer(String fileName)</b> - Loads poems and starts the server socket to wait for clients.</LI>
 *   <LI><b>private void loadPoems(String fileName)</b> - Reads poems from the specified file and stores them in a list.</LI>
 *   <LI><b>private void handleClient(Socket client)</b> - Handles one client connection: shows menu, reads choice, sends poem or error.</LI>
 * </UL>
 *
 * <H3>Compiling and Running</H3>
 *
 * <DL>
 *   <DT>Compile:</DT><DD>javac PoDServer.java</DD>
 *   <DT>Run:</DT><DD>java PoDServer poems.txt</DD>
 *   <DT>Test:</DT><DD>telnet localhost 9000</DD>
 * </DL>
 *
 * <H3>Test Plan</H3>
 *
 * <P>
 * 1. Start the server using: <code>java PoDServer poems.txt</code><br/>
 * EXPECTED: The server should display "PoD Server started on port 9000" and wait for clients.<br/>
 * 
 * 2. Connect using telnet: <code>telnet localhost 9000</code>.<br/>
 * EXPECTED: The client should see a welcome message and a numbered poem list.<br/>
 * 
 * 3. Enter a valid poem number (e.g., 1).<br/>
 * EXPECTED: The selected poem text should be displayed.<br/>
 * 
 * 4. Enter an invalid number (e.g., 5 if only 3 poems).<br/>
 * EXPECTED: The server should reply with an invalid selection message and close the connection.<br/>
 * 
 * 5. Enter text instead of a number.<br/>
 * EXPECTED: The server should detect invalid input and respond with an error message.<br/>
 * 
 * 6. Try multiple connections from different terminals.<br/>
 * EXPECTED: The server should handle each connection separately.
 * </P>
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class PoDServer {
    // List to store all poems loaded from file
    private List<String> poems = new ArrayList<>();

    // Main method: starts the server
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java PoDServer mypoems.txt");
            return;
        }
        new PoDServer().startServer(args[0]);
    }

    // Loads poems from the given file and starts listening for clients
    public void startServer(String fileName) {
        loadPoems(fileName);

        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            System.out.println("PoD Server started on port 9000. Waiting for connections...");

            while (true) {
                try (Socket client = serverSocket.accept()) {
                    System.out.println("Client connected: " + client.getInetAddress());

                    handleClient(client);

                } catch (IOException e) {
                    System.err.println("Client connection error: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
    }

    
    // Reads the poems from the file and stores them in the list
    private void loadPoems(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder currentPoem = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("---")) {
                    continue; // skip separator lines if present

                }
                if (line.trim().isEmpty()) {
                     // blank line marks end of poem
                    if (currentPoem.length() > 0) {
                        poems.add(currentPoem.toString().trim());
                        currentPoem.setLength(0);
                    }
                } else {
                    currentPoem.append(line).append("\n");
                }
            }
             // Add last poem if file doesn't end with blank line
            if (currentPoem.length() > 0) {
                poems.add(currentPoem.toString().trim());
            }
            System.out.println("Loaded " + poems.size() + " poems.");
        } catch (IOException e) {
            System.err.println("Error reading poems file: " + e.getMessage());
            System.exit(1);
        }
    }

    // Handles one client connection: shows menu, gets choice, sends poem or error
    private void handleClient(Socket client) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        ) {
            // Send welcome and list of poems
            out.println("Welcome to Poem of the Day Server!");
            out.println("Please choose a poem by number (1 to " + poems.size() + "):");
            for (int i = 0; i < poems.size(); i++) {
                out.println((i + 1) + ")");
            }
            out.println("Enter your choice:");

             // Read client's choice
            String input = in.readLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
                if (choice < 1 || choice > poems.size()) {
                    out.println("Invalid selection. Goodbye!");
                } else {
                    out.println("\nHere is your poem:\n");
                    out.println(poems.get(choice - 1));
                }
            } catch (NumberFormatException e) {
                out.println("Invalid input. Goodbye!");
            }

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }
}

